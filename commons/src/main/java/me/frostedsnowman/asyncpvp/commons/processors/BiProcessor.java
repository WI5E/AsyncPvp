package me.frostedsnowman.asyncpvp.commons.processors;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BiProcessor<T, U, V> extends BiFunction<T, U, V> {

    V process(T t, U u);

    @Override
    default V apply(T t, U u) {
        return process(t, u);
    }
}
