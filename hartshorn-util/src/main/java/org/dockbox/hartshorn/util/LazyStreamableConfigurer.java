package org.dockbox.hartshorn.util;

import java.util.List;

public class LazyStreamableConfigurer<I, O> implements LazyInitializer<I, List<O>> {

    private final StreamableConfigurer<I, O> configurer;
    private Customizer<StreamableConfigurer<I, O>> customizer = Customizer.useDefaults();

    public LazyStreamableConfigurer(final StreamableConfigurer<I, O> configurer) {
        this.configurer = configurer;
    }

    public LazyStreamableConfigurer<I, O> customizer(final Customizer<StreamableConfigurer<I, O>> customizer) {
        this.customizer = customizer;
        return this;
    }

    @Override
    public List<O> initialize(final I input) {
        this.customizer.configure(this.configurer);
        return this.configurer.initialize(input);
    }

    public static <I, O> LazyStreamableConfigurer<I, O> empty() {
        return new LazyStreamableConfigurer<>(StreamableConfigurer.empty());
    }

    @SafeVarargs
    public static <I, O> LazyStreamableConfigurer<I, O> of(final O... objects) {
        return new LazyStreamableConfigurer<>(StreamableConfigurer.of(objects));
    }

    public static <I, O> LazyStreamableConfigurer<I, O> of(final Iterable<? extends O> objects) {
        return new LazyStreamableConfigurer<>(StreamableConfigurer.of(objects));
    }
}
