/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    public static <I, O> StreamableConfigurer<I, O> of(O... objects) {
        StreamableConfigurer<I, O> configurer = StreamableConfigurer.empty();
        return configurer.addAll(objects);
    }

    public static <I, O> StreamableConfigurer<I, O> of(Iterable<? extends O> objects) {
        StreamableConfigurer<I, O> configurer = StreamableConfigurer.empty();
        return configurer.addAll(objects);
    }

    public StreamableConfigurer<I, T> add(T resolver) {
        this.objects.add(ContextualInitializer.of(resolver));
        return this;
    }

    public StreamableConfigurer<I, T> add(Initializer<T> resolver) {
        this.objects.add(ContextualInitializer.of(resolver));
        return this;
    }

    public StreamableConfigurer<I, T> add(ContextualInitializer<I, T> resolver) {
        this.objects.add(resolver);
        return this;
    }

    @SafeVarargs
    public final StreamableConfigurer<I, T> addAll(T... resolvers) {
        for (T resolver : resolvers) {
            this.add(resolver);
        }
        return this;
    }

    @SafeVarargs
    public final StreamableConfigurer<I, T> addAll(Initializer<T>... resolvers) {
        for (Initializer<T> resolver : resolvers) {
            this.add(resolver);
        }
        return this;
    }

    @SafeVarargs
    public final StreamableConfigurer<I, T> addAll(ContextualInitializer<I, T>... resolvers) {
        for (ContextualInitializer<I, T> resolver : resolvers) {
            this.add(resolver);
        }
        return this;
    }

    public StreamableConfigurer<I, T> addAll(Iterable<? extends T> resolvers) {
        for (T resolver : resolvers) {
            this.add(resolver);
        }
        return this;
    }

    public StreamableConfigurer<I, T> remove(ContextualInitializer<I, T> resolver) {
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

    public List<T> initialize(SingleElementContext<? extends I> input) {
        return this.stream()
                .map(resolver -> resolver.initialize(input))
                .toList();
    }
}
