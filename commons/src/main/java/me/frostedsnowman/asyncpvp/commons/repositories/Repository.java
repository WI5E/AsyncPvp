package me.frostedsnowman.asyncpvp.commons.repositories;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Repository<T, U> {

    private final Map<T, U> map;

    private Repository(Supplier<Map<T, U>> map) {
        this.map = Objects.requireNonNull(map.get(), "map");
    }

    @Nonnull
    public static <T, U> Repository<T, U> of(@Nonnull Supplier<Map<T, U>> map) {
        Objects.requireNonNull(map, "map");
        return new Repository<T, U>(map);
    }

    @Nullable
    public U getNullable(T t) {
        return this.map.get(t);
    }

    @Nonnull
    public Optional<U> get(@Nonnull T t) {
        Objects.requireNonNull(t, "t");
        return Optional.ofNullable(this.getNullable(t));
    }

    @Nullable
    public U getOrPutNullable(@Nonnull T t, @Nonnull U u) {
        Objects.requireNonNull(t, "t");
        Objects.requireNonNull(u, "u");
        return this.map.putIfAbsent(t, u);
    }

    @Nonnull
    public Optional<U> getOrPut(@Nonnull T t, @Nonnull U u) {
        Objects.requireNonNull(t, "t");
        Objects.requireNonNull(u, "u");
        return Optional.ofNullable(this.getOrPutNullable(t, u));
    }

    @Nullable
    public U removeNullable(@Nonnull T t) {
        Objects.requireNonNull(t, "t");
        return this.map.remove(t);
    }

    @Nonnull
    public Optional<U> remove(@Nonnull T t) {
        Objects.requireNonNull(t, "t");
        return Optional.ofNullable(this.removeNullable(t));
    }

    public boolean containsKey(@Nonnull T t) {
        Objects.requireNonNull(t, "t");
        return this.map.containsKey(t);
    }

    public boolean containsValue(@Nonnull U u) {
        Objects.requireNonNull(u, "u");
        return this.map.containsValue(u);
    }

    public void ifPresent(@Nonnull T t, @Nonnull Consumer<U> consumer) {
        Objects.requireNonNull(t, "t");
        Objects.requireNonNull(consumer, "consumer");
        this.get(t).ifPresent(consumer);
    }

    @Nonnull
    public Set<T> keys() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    @Nonnull
    public Collection<U> values() {
        return Collections.unmodifiableCollection(this.map.values());
    }
}
