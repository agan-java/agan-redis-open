## 反应式编程基础：反应式编程Reactor入门例子

###1. 开始阶段和消费阶段
Mono 实现了 org.reactivestreams.Publisher 接口，代表0到1个元素的发布者。
Flux 同样实现了 org.reactivestreams.Publisher 接口，代表0到N个元素的发表者。
```
@Test
public void test1(){
    Flux.just(1, 2, 3, 4, 5, 6).subscribe(System.out::print);
    System.out.println();
    Mono.just(1).subscribe(System.out::println);
}
```
执行结果：
```
123456
1
```


just 和 defer的区别
```
@Test
public void test2() {
    Mono<Date> m1 = Mono.just(new Date()); //Mono.just 在声明阶段构造Date对象
    Mono<Date> m2 = Mono.defer(()->Mono.just(new Date()));//Mono.defer在声明阶段不会构造对象
    m1.subscribe(System.out::println);//Mono.just 每次调用subscribe方法不会创建对象
    m2.subscribe(System.out::println);//Mono.defer每次调用subscribe方法都会创建Date对象
    //延迟5秒钟
    try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    m1.subscribe(System.out::println);//Mono.just 每次调用subscribe方法不会创建对象
    m2.subscribe(System.out::println);//Mono.defer每次调用subscribe方法都会创建Date对象
}
```
执行结果：
```
Sun Aug 09 15:40:07 CST 2020
Sun Aug 09 15:40:07 CST 2020
Sun Aug 09 15:40:07 CST 2020
Sun Aug 09 15:40:12 CST 2020
```
Mono.just会在声明阶段构造Date对象，只创建一次，但是Mono.defer却是在subscribe阶段才会创建对应的Date对象，
每次调用subscribe方法都会创建Date对象


###2. 中间阶段的处理:处理 Mono 和 Flux
#### map
map操作可以将数据元素进行转换/映射，得到一个新元素
```
public void test3(){
    //map操作可以将数据元素进行转换/映射，得到一个新元素
    Flux.just(1, 2, 3, 4, 5, 6).map(i->i*i).subscribe(i-> System.out.print(i+"   "));
}
```
执行结果：
```
1   4   9   16   25   36   
```
#### flatMap()
flatMap操作可以将每个数据元素转换/映射为一个流，然后将这些流合并为一个大的数据流。
```
public void test4(){
    //flatMap操作可以将每个数据元素转换/映射为一个流，然后将这些流合并为一个大的数据流。
    Flux<String> flux = Flux.just("a","b").flatMap(a -> Flux.just(a + "1"));
    flux.subscribe(System.out::println);
}
```
执行结果：
```
a1
b1
```
flatMap() 和 map()
先看下源码
public final <V> Flux<V> map(Function<? super T, ? extends V> mapper)
public final <R> Flux<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper)

flatMap() 和 map() 的区别在于，
flatMap() 中的入参 Function 的返回值要求是一个 Publisher对象，即Flux或Mono 对象，
而 map 的入参 Function 只要求返回一个 普通对象。

###3. doOn系列方法
```
@Test
public void test5(){
    Flux<String> flux = Flux.just("a","b","c")
            .flatMap(a -> Flux.just(a + "c"))
            .doOnNext(o->{
                System.out.println("进入同步钩子doOnNext");
                if(o.equals("cc")){
                    int i=9/0;
                }else{
                    System.out.println("--"+o);
                }
            })
            .doOnError(e->{
                System.out.println("触发异常事件："+e.getMessage()); })
            .onErrorResume(e -> Mono.just("异常重新重新处理业务逻辑，恢复逻辑cc"));

    flux.subscribe(System.out::println);
}
```
执行结果：
```
进入同步钩子doOnNext
--ac
ac
进入同步钩子doOnNext
--bc
bc
进入同步钩子doOnNext
触发异常事件：/ by zero
异常重新重新处理业务逻辑，恢复逻辑cc
```
doOn系列方法是publisher的同步钩子方法，在subscriber触发一系列事件的时候触发。
onErrorResume方法能够在收到错误信号的时候提供一个新的数据流(重新做业务).

###  fromCompletionStage
fromCompletionStage用于接收一个CompletableFuture对象，然后执行toFuture()方法。toFuture()的底层原理就是去执行subscribe方法。
``` 
    @Test
    public void test6()  {
        Mono.fromCompletionStage(CompletableFuture.supplyAsync(() -> {
            int n=8;
            System.out.println("-----completableFuture"+n);
            return n;
        }))
        .doOnNext(o-> {
            System.out.println(o);
        }).toFuture();
    }
```
执行结果：
``` 
-----completableFuture8
8
```