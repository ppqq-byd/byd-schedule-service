package com.ora.blockchain.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class ThreadPool {

    private static ExecutorService executorService = new ThreadPoolExecutor(1, 16, 1, TimeUnit.SECONDS, new LinkedBlockingDeque());

    public static <T> List<T> addTask(List<Callable<T>> tasks) {
        List<Future<T>> futureList = null;
        List<T> results = null;
        try {
            futureList = executorService.invokeAll(tasks);
            results = new ArrayList<>();
            for (Future<T> future : futureList) {
                results.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return results;
    }
}
