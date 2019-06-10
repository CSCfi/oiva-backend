package fi.minedu.oiva.backend.core.util;

import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class AsyncUtil {
    public static <T> CompletableFuture<T> async(final Supplier<T> supplier) {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Executor executor = ForkJoinPool.commonPool();
        return supplyAsync(supplier, new DelegatingSecurityContextExecutor(executor, securityContext));
    }
}
