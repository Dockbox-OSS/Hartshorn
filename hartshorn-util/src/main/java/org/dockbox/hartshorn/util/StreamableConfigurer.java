package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.context.DefaultContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class StreamableConfigurer<I, T> extends DefaultContext implements Configurer {

    private final List<ContextualInitializer<I, T>> objects = new CopyOnWriteArrayList<>();

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
        this.objects.add(ContextualInitializer.of(resolver));
        return this;
    }

    public StreamableConfigurer<I, T> add(final Initializer<T> resolver) {
        this.objects.add(ContextualInitializer.of(resolver));
        return this;
    }

    public StreamableConfigurer<I, T> add(final ContextualInitializer<I, T> resolver) {
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
    public final StreamableConfigurer<I, T> addAll(final ContextualInitializer<I, T>... resolvers) {
        for (final ContextualInitializer<I, T> resolver : resolvers) {
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

    public StreamableConfigurer<I, T> remove(final ContextualInitializer<I, T> resolver) {
        this.objects.remove(resolver);
        return this;
    }

    public StreamableConfigurer<I, T> clear() {
        this.objects.clear();
        return this;
    }

    public Stream<ContextualInitializer<I, T>> stream() {
        return this.objects.stream();
    }

    public List<T> initialize(final I input) {
        return this.stream()
                .map(resolver -> resolver.initialize(input))
                .toList();
    }
}
