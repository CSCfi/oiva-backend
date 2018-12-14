package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.util.ExecutorContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AsyncService {

    private static final Map<String, ExecutorContext> executorContexts = new HashMap<>();

    public ExecutorContext create(final String name, final String initialState) {
        return create(name, 1, initialState);
    }

    public ExecutorContext create(final String name, final int threads, final String initialState) {
        final ExecutorContext existingContext = executorContexts.get(name);
        if(null != existingContext) return existingContext;
        else {
            final ExecutorContext newContext = threads > 1 ? new ExecutorContext(threads, initialState) : new ExecutorContext(initialState);
            executorContexts.put(name, newContext);
            return newContext;
        }
    }

    public Optional<ExecutorContext> getContext(final String name) {
        return Optional.ofNullable(executorContexts.get(name));
    }

    public void addState(final String name, final String state) {
        getContext(name).ifPresent(context -> context.addState(state));
    }

    public Optional<List<String>> getStates(final String name) {
        final Optional<ExecutorContext> context = getContext(name);
        return context.isPresent() ? Optional.ofNullable(context.get().reversedStates()) : Optional.empty();
    }

    public void terminate(final String name) {
        getContext(name).ifPresent(context -> {
            final ExecutorService executor = context.executor();
            if(null != executor) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                } finally {
                    executorContexts.remove(name);
                }
            }
        });
    }
}