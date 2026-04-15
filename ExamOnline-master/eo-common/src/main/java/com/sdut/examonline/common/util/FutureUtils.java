package com.sdut.examonline.common.util;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author Chen
 * @since 2022/4/9 22:29
 */
public class FutureUtils {

    private static final ScheduledThreadPoolExecutor TIMEOUT_THREAD = new ScheduledThreadPoolExecutor(1,
            runnable -> {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                thread.setName("TIMEOUT_THREAD");
                return thread;
            });

    public static <T> CompletableFuture<T> timeout(Executor executor, Supplier<T> supplier, long timeout, TimeUnit unit) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                future.complete(supplier.get());
            } catch (Throwable ex) {
                future.completeExceptionally(ex);
            }
        });
        TIMEOUT_THREAD.schedule(() -> {
            future.completeExceptionally(new TimeoutException("task execution timeout"));
        }, timeout, unit);
        return future;
    }

}
