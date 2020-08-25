package com.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class CombineTest03 {


    public static Executor executor = Executors.newFixedThreadPool(10);
    List<Integer> userIds = Arrays.asList(
            1,2,3
    );

    /**
     * 模拟一批用户去，异步批量执行下订单操作，共4个步骤：下订单、支付、送积分、统计耗时
     * @throws Exception
     */
    @Test
    public  void orderLogic() throws Exception{
        long st = System.currentTimeMillis();
        Stream<CompletableFuture<Order>> futurePrices = userIds.stream()
                //步骤1：模拟下订单
                .map(userId -> CompletableFuture.supplyAsync(() ->{
                            Order order=new Order();
                            order.setUserId(userId);
                            String no=this.goOrder(userId);
                            order.setOrderNo(no);
                            return order;
                        },executor)
                )
                //步骤2：下单成功后，去模拟去支付
                .map(future -> future.thenApply(order->{ //thenApply异步任务和上面是同一条线程
                            String payno=this.goPay(order);
                            order.setPayNo(payno);
                            return order;
                        })
                )
                //步骤3：异步送积分，
                .map(future -> future.thenCompose(//thenCompose:上面完成后，将其结果作为参数传递给下面操作，不是同条线程
                        order -> CompletableFuture.supplyAsync(() -> {
                    this.present(order.getUserId());
                    return order;
                },executor)));
        //步骤4：统计耗时时间
        CompletableFuture[] futures = futurePrices.map(f -> f.thenAccept(s -> {//thenAccept:异步任务和上面是同一条线程
            //打印耗时时间
            String sout = String.format("%s done in %s mesc",s,(System.currentTimeMillis() - st));
            log.debug(Thread.currentThread().getName() +"-"+sout);
        })).toArray(size -> new CompletableFuture[size]);
        //allOf()工厂方法接受由CompletableFuture对象构成的数组，这里使用其等待所有的子线程执行完毕
        CompletableFuture.allOf(futures).join();
    }

    private String goOrder(int userId)  {
        int n = new Random().nextInt(3);
        try {
            TimeUnit.SECONDS.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String no="order"+userId+n;
        log.debug("{}下订单成功：userId={}---订单号={}", Thread.currentThread().getName(), userId,no);
        return no;
    }
    private String goPay(Order order)  {
        int n = new Random().nextInt(3);
        try {
            TimeUnit.SECONDS.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("{}支付成功：userId={}---订单号={}", Thread.currentThread().getName(),order.getUserId(),order.getOrderNo());
        return "pay"+n;
    }

    private void present(Integer userId)  {
        int n = new Random().nextInt(10);
        try {
            TimeUnit.SECONDS.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("{}送积分成功：userId={}---积分={}", Thread.currentThread().getName(), userId,n);
    }




}
