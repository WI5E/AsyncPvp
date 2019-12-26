package me.frostedsnowman.asyncpvp.commons.processors;

import java.util.function.Function;

@FunctionalInterface
public interface Processor<T, U> extends Function<T, U> {

    U process(T t);

    @Override
    default U apply(T t) {
        return process(t);
    }
}
