package org.dockbox.hartshorn.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class StreamableConfigurer<I, T> {

    private final List<LazyInitializer<I, T>> objects = new CopyOnWriteArrayList<>();

    protected StreamableConfigurer() {
    }

    public static <I, T> StreamableConfigurer<I, T> empty() {
        return new StreamableConfigurer<>();
    }

    @SafeVarargs
    public static <I, O> StreamableConfigurer<I, O> of(final O... objects) {
        final StreamableConfigurer<I, O> configurer = StreamableConfigurer.empty();
        return configurer.addAll(objects);
    }

    public static <I, O> StreamableConfigurer<I, O> of(final Iterable<? extends O> objects) {
        final StreamableConfigurer<I, O> configurer = StreamableConfigurer.empty();
        return configurer.addAll(objects);
    }

    public StreamableConfigurer<I, T> add(final T resolver) {
        this.objects.add(LazyInitializer.of(resolver));
        return this;
    }

    public StreamableConfigurer<I, T> add(final Initializer<T> resolver) {
        this.objects.add(LazyInitializer.of(resolver));
        return this;
    }

    public StreamableConfigurer<I, T> add(final LazyInitializer<I, T> resolver) {
        this.objects.add(resolver);
        return this;
    }

    @SafeVarargs
    public final StreamableConfigurer<I, T> addAll(final T... resolvers) {
        for (final T resolver : resolvers) {
            this.add(resolver);
        }
        return this;
    }

    @SafeVarargs
    public final StreamableConfigurer<I, T> addAll(final Initializer<T>... resolvers) {
        for (final Initializer<T> resolver : resolvers) {
            this.add(resolver);
        }
        return this;
    }

    @SafeVarargs
    public final StreamableConfigurer<I, T> addAll(final LazyInitializer<I, T>... resolvers) {
        for (final LazyInitializer<I, T> resolver : resolvers) {
            this.add(resolver);
        }
        return this;
    }

    public StreamableConfigurer<I, T> addAll(final Iterable<? extends T> resolvers) {
        for (final T resolver : resolvers) {
            this.add(resolver);
        }
        return this;
    }

    public StreamableConfigurer<I, T> remove(final LazyInitializer<I, T> resolver) {
        this.objects.remove(resolver);
        return this;
    }

    public StreamableConfigurer<I, T> clear() {
        this.objects.clear();
        return this;
    }

    public Stream<LazyInitializer<I, T>> stream() {
        return this.objects.stream();
    }

    public List<T> initialize(final I input) {
        return this.stream()
                .map(resolver -> resolver.initialize(input))
                .toList();
    }
}
