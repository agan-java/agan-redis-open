package com.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
@Slf4j
public class Common {
    public static Executor executor = Executors.newFixedThreadPool(10);

    public static void sleep(int n)  {
        try {
            TimeUnit.SECONDS.sleep(n);
            log.debug("{}-----sleep:{}",Thread.currentThread().getName(),n);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static CompletableFuture<Integer> getCompletionStage(int n)  {
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(n);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            log.debug("{}-----sleep:{}",Thread.currentThread().getName(),n);
            return n;
        },executor);
        return completableFuture;
    }
}
