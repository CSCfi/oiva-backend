package fi.minedu.oiva.backend.core.util;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Context wrapper for ExecutorServices
 */
public class ExecutorContext {

    private final ExecutorService executor;
    private List<String> states;

    public ExecutorContext(final String initialState) {
        this(Executors.newSingleThreadExecutor(), initialState);
    }

    public ExecutorContext(final int threads, final String initialState) {
        this(Executors.newFixedThreadPool(threads), initialState);
    }

    public ExecutorContext(final ExecutorService executor, final String initialState) {
        this.executor = executor;
        this.states = new ArrayList<>();
        addState(initialState);
    }

    public ExecutorService executor() {
        return this.executor;
    }

    public ExecutorContext execute(final Runnable task) {
        executor().execute(task);
        return this;
    }

    public void addState(final String state) {
        this.states.add(state);
    }

    public List<String> reversedStates() {
        return Lists.reverse(this.states);
    }
}
