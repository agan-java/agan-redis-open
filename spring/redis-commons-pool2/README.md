### 为什么需要对象池？
系统中一般都会存在很多可重用并长期使用的对象，比如线程、TCP 连接、数据库连接等。虽然我们可以简单的在使用这些对象时进行创建、使用结束后销毁，
但初始化和销毁对象的操作会造成一些资源消耗。可以使用对象池将这些对象集中管理，减少对象初始化和销毁的次数以节约资源消耗。

顾名思义，对象池简单来说就是存放对象的池子，可以存放任何对象，并对这些对象进行管理。
它的优点就是可以复用池中的对象，避免了分配内存和创建堆中对象的开销；避免了释放内存和销毁堆中对象的开销，进而减少JVM垃圾收集器的负担；
避免内存抖动，不必重复初始化对象状态。对于构造和销毁比较耗时的对象来说非常合适。

Apache 提供了一个通用的对象池技术的实现: Common Pool2，可以很方便的实现自己需要的对象池。Jedis 的内部对象池就是基于 Common Pool2 实现的。


### 对象池入门例子
#### 步骤0：加入依赖包
``` 
    <dependency>
		<groupId>org.apache.commons</groupId>
		<artifactId>commons-pool2</artifactId>
		<version>2.4.2</version>
	</dependency>
```
#### 步骤1：新建一个原始对象
``` 
public class Resource {

	private static int id=1;
	private int rid;
	
	public Resource() {
		synchronized (this) {
			this.rid = id++;
		}
	}
	
	public int getRid() {
		return this.rid;
	}
	
	@Override
	public String toString() {
		return "id:" + this.rid;
	}
	
}
```
#### 步骤2：新建一个对象工厂
对象工厂，负责对象的创建、初始化、销毁和验证等工作。
工厂主要干2件事
1.create()创建一个原始对象
2.wrap()把原始对象，包装成PooledObject对象，因为内部都用PooledObject来处理
``` 
public class MyPoolableObjectFactory extends BasePooledObjectFactory<Resource>{
	
	/**
	 * 创建一个对象实例
	 */
	@Override
	public Resource create() throws Exception {
		return new Resource();
	}
	
	/**
	 * 包裹创建的对象实例，返回一个pooledobject
	 */
	@Override
	public PooledObject<Resource> wrap(Resource obj) {
		return new DefaultPooledObject<Resource>(obj);
	}
	
}
```
#### 步骤3：对象池的读写操作
对象池一般只干2件事：借、还
1.借：借出对象borrowObject
2.还：使用完后，调用ObjectPool接口的returnObject方法，归还对象，供其他人继续使用
``` 
public class Test {

	public static void main(String[] args) {


		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		// 最大空闲数
		poolConfig.setMaxIdle(5);
		// 最小空闲数, 池中只有一个空闲对象的时候，池会在创建一个对象，并借出一个对象，从而保证池中最小空闲数为1
		poolConfig.setMinIdle(1);
		// 最大池对象总数
		poolConfig.setMaxTotal(20);
		// 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
		poolConfig.setMinEvictableIdleTimeMillis(1800000);
		// 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
		poolConfig.setTimeBetweenEvictionRunsMillis(1800000 * 2L);
		// 在获取对象的时候检查有效性, 默认false
		poolConfig.setTestOnBorrow(true);
		// 在归还对象的时候检查有效性, 默认false
		poolConfig.setTestOnReturn(false);
		// 在空闲时检查有效性, 默认false
		poolConfig.setTestWhileIdle(false);
		// 最大等待时间， 默认的值为-1，表示无限等待。
		poolConfig.setMaxWaitMillis(5000);
		// 是否启用后进先出, 默认true
		poolConfig.setLifo(true);
		// 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		poolConfig.setBlockWhenExhausted(true);
		// 每次逐出检查时 逐出的最大数目 默认3
		poolConfig.setNumTestsPerEvictionRun(3);


		// 创建池对象工厂
		PooledObjectFactory<Resource> factory = new MyPoolableObjectFactory();

		// 创建对象池
		final GenericObjectPool<Resource> pool = new GenericObjectPool<Resource>(factory, poolConfig);

		borrowObject(pool);

		for (int i = 0; i < 40; i++) {
			new Thread(new Runnable() {
				public void run() {
					borrowObject(pool);
				}
			}).start();
		}
	}
	private static void borrowObject(GenericObjectPool<Resource> pool){
		try {
			// 注意，如果对象池没有空余的对象，那么这里会block，可以设置block的超时时间
			Resource resource = pool.borrowObject();
			System.out.println(resource);
			Thread.sleep(1000);
			// 申请的资源用完了记得归还，不然其他人要申请时可能就没有资源用了
			pool.returnObject(resource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
```









### 对象池原理

### 核心接口
在讲述其实现原理前，先提一下其中有几个重要的对象：
- Object Pool（对象池）。
- PooledObject（池中对象）。
- PooledObjectFactory（池对象工厂）。
从下图（图1），我们来详细讲解
### Object Pool（对象池）
Object Pool负责管理PooledObject，如：借出对象，返回对象，校验对象，有多少激活对象，有多少空闲对象。
```
public interface ObjectPool<T> {
    // 从池中获取一个对象，客户端在使用完对象后必须使用 returnObject 方法返还获取的对象
    T borrowObject() throws Exception, NoSuchElementException,IllegalStateException;

    // 将对象返还到池中。对象必须是从 borrowObject 方法获取到的
    void returnObject(T obj) throws Exception;

    // 使池中的对象失效，当获取到的对象被确定无效时（由于异常或其他问题），应该调用该方法
    void invalidateObject(T obj) throws Exception;

    // 池中当前闲置的对象数量
    int getNumIdle();

    // 当前从池中借出的对象的数量
    int getNumActive();

    // 清除池中闲置的对象
    void clear() throws Exception, UnsupportedOperationException;

    // 关闭这个池，并释放与之相关的资源
    void close();

    ...
}
```



### PooledObject（池对象）
用于封装对象（如：线程、数据库连接、TCP连接），将其包裹成可被池管理的对象。
```
public interface PooledObject<T> extends Comparable<PooledObject<T>> {
    // 获取封装的对象
    T getObject();

    // 对象创建的时间
    long getCreateTime();

    // 对象上次处于活动状态的时间
    long getActiveTimeMillis();

    // 对象上次处于空闲状态的时间
    long getIdleTimeMillis();

    // 对象上次被借出的时间
    long getLastBorrowTime();

    // 对象上次返还的时间
    long getLastReturnTime();

    // 对象上次使用的时间
    long getLastUsedTime();

    // 将状态置为 PooledObjectState.INVALID
    void invalidate();

    // 更新 lastUseTime
    void use();

    // 获取对象状态
    PooledObjectState getState();

    // 将状态置为 PooledObjectState.ABANDONED
    void markAbandoned();

    // 将状态置为 PooledObjectState.RETURNING
    void markReturning();
}
```


### PooledObjectFactory
对象工厂，负责对象的创建、初始化、销毁和验证等工作。Factory 对象由ObjectPool持有并使用。
```
public interface PooledObjectFactory<T> {
    // 创建一个池对象
    PooledObject<T> makeObject() throws Exception;

    // 销毁对象
    void destroyObject(PooledObject<T> p) throws Exception;

    // 验证对象是否可用
    boolean validateObject(PooledObject<T> p);

    // 激活对象，从池中取对象时会调用此方法
    void activateObject(PooledObject<T> p) throws Exception;

    // 钝化对象，向池中返还对象时会调用此方法
    void passivateObject(PooledObject<T> p) throws Exception;
}
```
Common Pool2 并没有提供 PooledObjectFactory 可以直接使用的子类实现，因为对象的创建、初始化、销毁和验证的工作无法通用化，需要由使用方自己实现。
不过它提供了一个抽象子类 BasePooledObjectFactory，实现自己的工厂时可以继承BasePooledObjectFactory，就只需要实现 create 和 wrap 两个方法了。


