package me.frostedsnowman.asyncpvp.commons.objects;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class Consumers {

    private Consumers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @Nonnull
    public static <T> T morph(@Nonnull T in, @Nonnull Consumer<T> consumer) {
        Objects.requireNonNull(in, "in");
        Objects.requireNonNull(consumer, "consumer");
        consumer.accept(in);
        return in;
    }
}
