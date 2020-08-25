package com.agan.redis;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**

 */
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

