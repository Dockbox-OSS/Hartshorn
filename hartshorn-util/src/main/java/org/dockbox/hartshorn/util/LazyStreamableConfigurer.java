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

import java.util.List;

public class LazyStreamableConfigurer<I, O> implements ContextualInitializer<I, List<O>> {

    private final StreamableConfigurer<I, O> configurer;
    private Customizer<StreamableConfigurer<I, O>> customizer = Customizer.useDefaults();

    public LazyStreamableConfigurer(StreamableConfigurer<I, O> configurer) {
        this.configurer = configurer;
    }

    public LazyStreamableConfigurer<I, O> customizer(Customizer<StreamableConfigurer<I, O>> customizer) {
        // Note order, existing customizer is applied first, so the new customizer can override it if needed
        this.customizer = this.customizer.compose(customizer);
        return this;
    }

    @Override
    public List<O> initialize(InitializerContext<? extends I> input) {
        this.customizer.configure(this.configurer);
        return this.configurer.initialize(input);
    }

    public static <I, O> LazyStreamableConfigurer<I, O> empty() {
        return new LazyStreamableConfigurer<>(StreamableConfigurer.empty());
    }

    @SafeVarargs
    public static <I, O> LazyStreamableConfigurer<I, O> of(O... objects) {
        return new LazyStreamableConfigurer<>(StreamableConfigurer.of(objects));
    }

    public static <I, O> LazyStreamableConfigurer<I, O> of(Iterable<? extends O> objects) {
        return new LazyStreamableConfigurer<>(StreamableConfigurer.of(objects));
    }
}
