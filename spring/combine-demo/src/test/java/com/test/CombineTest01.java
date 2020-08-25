package com.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;
@Slf4j
public class CombineTest01 {

    @Test
    public void runAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            Common.sleep(1);
        });

        //方法是阻塞的。它会一直等到Future完成并且在完成后返回结果。
        f1.get();
        log.debug("{}------runAsync 异步运行无返回值",Thread.currentThread().getName());
    }

    @Test
    public void supplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            Common.sleep(1);
            return 1;
        });

        Integer result = future.get();
        log.debug("{}------supplyAsync 异步运行有返回值={}",Thread.currentThread().getName(),result);
    }

    @Test
    public  void completedFuture() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.completedFuture("hello agan");

        String result = future.get();
        log.debug("{}------completedFuture--{}",Thread.currentThread().getName(),result);
    }


    @Test
    public  void join()  {
        CompletableFuture<String> future = CompletableFuture.completedFuture("hello agan");

        String result = future.join();
        log.debug("{}------completedFuture--{}",Thread.currentThread().getName(),result);
    }



    @Test
    public void complete() throws ExecutionException, InterruptedException {
        //案例1：future.get()在等待执行结果时，程序会一直block，如果此时调用complete(T t)会立即执行。
        CompletableFuture<String> f1 = new CompletableFuture<>();
        //开启complete就不会阻塞
        //f1.complete("World");

        try {
            log.debug("{}------complete--{}",Thread.currentThread().getName(),f1.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //案例2：如果future已经执行完毕能够返回结果，此时再调用complete(T t)则会无效。
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "Hello");

        Common.sleep(5);
        //如果future已经执行完毕能够返回结果，此时再调用complete(T t)则会无效。
        f2.complete("World");

        try {
            log.debug("{}------complete--{}",Thread.currentThread().getName(),f2.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    /**
     * 异步任务，无返回值 参数Runnable
     * public CompletionStage<Void> thenRun(Runnable action);
     * public CompletionStage<Void> thenRunAsync(Runnable action);
     * public CompletionStage<Void> thenRunAsync(Runnable action,Executor executor);
     */
    @Test
    public  void thenRun() throws Exception{
        CompletableFuture<Integer> f1 = Common.getCompletionStage(2);
        //先执行f1，再执行下面
        f1.thenRun(() -> {//异步任务和f1是同一条线程
            int n=5;
            Common.sleep(n);
        }).thenRunAsync(() -> {//异步任务和f1 不是同一条线程
            int n=8;
            Common.sleep(n);
        },Common.executor);

        f1.get();
        log.debug("{}------结束",Thread.currentThread().getName());
        //加个睡眠,不然看不到效果，
        Common.sleep(20);
    }

    /**
     * 异步任务，有返回值 参数是：Function
     * public <U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn)
     * public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn)
     * public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor)
     */
    @Test
    public  void thenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f1 = Common.getCompletionStage(2);
        //先执行f1，再执行下面
        f1.thenApply(n -> {//异步任务和f1是同一条线程
            Common.sleep(n+5);
            return n + 8;
        }).thenApplyAsync(n -> {//异步任务和f1 不是同一条线程
            Common.sleep(n);
            return n+10;
        },Common.executor);
        Integer result = f1.get();
        log.debug("{}------结束{}",Thread.currentThread().getName(),result);
        //加个睡眠,不然看不到效果，
        Common.sleep(20);
    }


    /**
     * 参数：Consumer
     * thenAccept和thenRun的区别？？
     * thenAccept()可以访问CompletableFuture的结果，但thenRun()不能访Future的结果
     * public CompletionStage<Void> thenAccept(Consumer<? super T> action);
     * public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);
     * public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action,Executor executor);
     */
    @Test
    public  void thenAccept() throws Exception{
        CompletableFuture<Integer> f1 = Common.getCompletionStage(3);
        //先执行f1，再执行下面
        f1.thenAccept(n -> {//异步任务和f1是同一条线程
            Common.sleep(n);
        })
        .thenAcceptAsync(n -> {//异步任务和f1 不是同一条线程
            Common.sleep(5);
        }
        ,Common.executor);
        f1.get();
        log.debug("{}------结束",Thread.currentThread().getName());
        //加个睡眠,不然看不到效果，
        Common.sleep(20);
    }

    /**
     * 异常回滚 参数是：BiFunction
     * thenApply 和 handle的区别？？
     * thenApply 只可以执行正常的任务，任务出现异常则不执行 thenApply 方法，但handle可以异常回滚
     *
     * public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn);
     * public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn);
     * public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn,Executor executor);
     */
    @Test
    public  void handle() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            int i=8/0;
            return "example:";
        }).handle((obj, throwable) -> {
            Common.sleep(5);
            if (throwable != null) {
                //TODO 异常回滚
                log.debug("{}-----线程串行执行handle 异常回滚",Thread.currentThread().getName());
                return null;
            } else {
                log.debug("{}-----线程串行执行handle返回值",Thread.currentThread().getName());
                return obj +" handle!";
            }
        });
        log.debug("{}------{}",Thread.currentThread().getName(),f1.get());
    }



    /**
     * 异常回滚 参数是：BiConsumer
     * public CompletableFuture<T> whenComplete(BiConsumer<? super T,? super Throwable> action)
     * public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T,? super Throwable> action)
     * public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T,? super Throwable> action, Executor executor)
     */
    @Test
    public   void whenComplete() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            int i=8/0;
            return  1;
        }).whenComplete((n, e) -> {
            Common.sleep(n);
            if (Objects.nonNull(e)) {
                log.debug("{}异步任务发生异常:{}",Thread.currentThread().getName(),e.getMessage());
            }
        }).whenCompleteAsync((n, e) -> {//异步回调方法和supplyAsync 不是同一条线程
            Common.sleep(n);
            if (Objects.nonNull(e)) {
                log.debug("{}异步任务发生异常:{}",Thread.currentThread().getName(),e.getMessage());
            }
        },Common.executor);
        Integer result = f1.get();
        log.debug("{}------同步运行有返回值{}",Thread.currentThread().getName(),result);
    }

}
