package com.test;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ReactorTest {

    @Test
    public void test1(){
        Flux.just(1, 2, 3, 4, 5, 6).subscribe(System.out::print);
        System.out.println();
        Mono.just(1).subscribe(System.out::println);
    }




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


    @Test
    public void test3(){
        //map操作可以将数据元素进行转换/映射，得到一个新元素
        Flux.just(1, 2, 3, 4, 5, 6).map(i->i*i).subscribe(i-> System.out.print(i+"   "));
    }

    @Test
    public void test4(){
        //flatMap操作可以将每个数据元素转换/映射为一个流，然后将这些流合并为一个大的数据流。
        Flux<String> flux = Flux.just("a","b").flatMap(a -> Flux.just(a + "1"));
        flux.subscribe(System.out::println);
    }

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

}
