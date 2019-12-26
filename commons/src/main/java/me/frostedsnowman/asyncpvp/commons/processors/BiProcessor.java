package me.frostedsnowman.asyncpvp.commons.processors;

import java.util.function.Consumer;

@FunctionalInterface
public interface BiProcessor<T, U, V>  {

   void process(T t, U u, Consumer<V> v);
}
