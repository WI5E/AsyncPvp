package me.frostedsnowman.asyncpvp.commons.processors;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface Processor<T, U> {

    void process(T t, Consumer<U> u);
}
