package com.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CombineTest02 {



    @Test
    public  void thenCombine() throws Exception {
        log.debug("---------------------------------------thenCombine----------");
        CompletableFuture<Integer> f1 = Common.getCompletionStage(6);
        CompletableFuture<Integer> f2 = Common.getCompletionStage(5);

        CompletableFuture<String> result = f1.thenCombine(f2, (n1,n2)-> {
            return n1+" "+n2;
        });
        log.debug("{}---result-------{}", Thread.currentThread().getName(),result.get());
    }


    @Test
    public  void  thenAcceptBoth() throws Exception {
        CompletableFuture<Integer> f1 = Common.getCompletionStage(6);
        CompletableFuture<Integer> f2 = Common.getCompletionStage(5);

        f1.thenAcceptBoth(f2, (n1,n2)-> {
            log.debug("{}---result-------{}", DateUtil.getNowTime_EN(),n1+n2);
        });
        f1.get();
        //加个睡眠,不然看不到效果，
        Common.sleep(10);
    }

    @Test
    public void applyToEither() throws Exception {
        log.debug("---------------------------------------applyToEither----------");
        CompletableFuture<Integer> f1 = Common.getCompletionStage(6);
        CompletableFuture<Integer> f2 = Common.getCompletionStage(5);

        CompletableFuture<Integer> result = f1.applyToEither(f2, (obj)-> {
            return obj*2;
        });
        log.debug("{}---result-------{}", DateUtil.getNowTime_EN(),result.get());
    }
    @Test
    public void acceptEither() throws Exception {
        CompletableFuture<Integer> f1 = Common.getCompletionStage(10);
        CompletableFuture<Integer> f2 = Common.getCompletionStage(5);

        f1.acceptEither(f2, (obj)-> {
            log.debug("{}---result-------{}", DateUtil.getNowTime_EN(),obj*2);
        });
        f1.get();
    }

    @Test
    public void runAfterEither() throws Exception {
        CompletableFuture<Integer> f1 = Common.getCompletionStage(2);
        CompletableFuture<Integer> f2 = Common.getCompletionStage(5);
        f1.runAfterEither(f2, ()-> {
            log.debug("{}---上面有一个已经完成了。-------", DateUtil.getNowTime_EN());
        });
        //加个睡眠,不然看不到效果，
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public  void thenCompose() throws Exception {
        CompletableFuture<Integer> f1 = Common.getCompletionStage(2);
        //异步去执行第二个任务
        f1.thenCompose(obj -> Common.getCompletionStage(obj+3));

        log.debug("thenCompose result : "+f1.get());
        //加个睡眠,不然等不回来第二个任务，
        TimeUnit.SECONDS.sleep(10);
    }


    @Test
    public void allOf() throws Exception {
        CompletableFuture<Integer> f1 = Common.getCompletionStage(2);
        CompletableFuture<Integer> f2 = Common.getCompletionStage(3);
        CompletableFuture<Integer> f3 = Common.getCompletionStage(4);
        CompletableFuture<Integer> f4 = Common.getCompletionStage(5);

        CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2,f3,f4);
        all.join();
        log.debug("{}---任务均已完成。-------", DateUtil.getNowTime_EN());
    }



}
