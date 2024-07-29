/*
 * Copyright 2019-2024 the original author or authors.
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

package test.org.dockbox.hartshorn.inject;

import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.provider.selection.ExactPriorityProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.provider.selection.HighestPriorityProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.provider.selection.MaximumPriorityProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.provider.selection.MinimumPriorityProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.selection.ProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.provider.SingletonInstantiationStrategy;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.NativePrunableBindingHierarchy;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class InstantiationStrategySelectionStrategyTests {

    private static final String PRIORITY_DEFAULT_VALUE = "default";
    private static final String PRIORITY_ZERO_VALUE = "zero";
    private static final String PRIORITY_ONE_VALUE = "one";
    private static final String PRIORITY_TWO_VALUE = "two";

    public static Stream<Arguments> strategies() {
        return Stream.of(
                Arguments.of(new MaximumPriorityProviderSelectionStrategy(0)),
                Arguments.of(new ExactPriorityProviderSelectionStrategy(0)),
                Arguments.of(new MinimumPriorityProviderSelectionStrategy(0)),
                Arguments.of(new HighestPriorityProviderSelectionStrategy())
        );
    }

    private BindingHierarchy<?> createHierarchy() {
        BindingHierarchy<String> hierarchy = createEmptyHierarchy();
        hierarchy.add(-1, new SingletonInstantiationStrategy<>(PRIORITY_DEFAULT_VALUE));
        hierarchy.add(0, new SingletonInstantiationStrategy<>(PRIORITY_ZERO_VALUE));
        hierarchy.add(1, new SingletonInstantiationStrategy<>(PRIORITY_ONE_VALUE));
        hierarchy.add(2, new SingletonInstantiationStrategy<>(PRIORITY_TWO_VALUE));
        return hierarchy;
    }

    @NonNull
    private static BindingHierarchy<String> createEmptyHierarchy() {
        ComponentKey<String> key = ComponentKey.of(String.class);
        return new NativePrunableBindingHierarchy<>(key);
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void testStrategySelectsNullIfEmpty() {
        BindingHierarchy<String> hierarchy = createEmptyHierarchy();
        ProviderSelectionStrategy strategy = new MaximumPriorityProviderSelectionStrategy(0);
        InstantiationStrategy<?> provider = strategy.selectProvider(hierarchy);
        Assertions.assertNull(provider);
    }

    @Test
    void testMaximumPriorityStrategySelectsMaximumExclusive() {
        ProviderSelectionStrategy strategy = new MaximumPriorityProviderSelectionStrategy(1);
        this.assertValueWithStrategy(PRIORITY_ZERO_VALUE, strategy);
    }

    @Test
    void testMaximumPriorityStrategySelectsNullIfOutOfRange() {
        ProviderSelectionStrategy strategy = new MaximumPriorityProviderSelectionStrategy(-2);
        this.assertValueWithStrategy(null, strategy);
    }

    @Test
    void testExactPriorityStrategySelectsExact() {
        ProviderSelectionStrategy strategy = new ExactPriorityProviderSelectionStrategy(0);
        this.assertValueWithStrategy(PRIORITY_ZERO_VALUE, strategy);
    }

    @Test
    void testExactPriorityStrategySelectsNullIfOutOfRange() {
        ProviderSelectionStrategy strategy = new ExactPriorityProviderSelectionStrategy(-2);
        this.assertValueWithStrategy(null, strategy);
    }

    @Test
    void testMinimumPriorityStrategySelectsMinimumInclusive() {
        ProviderSelectionStrategy strategy = new MinimumPriorityProviderSelectionStrategy(1);
        this.assertValueWithStrategy(PRIORITY_ONE_VALUE, strategy);
    }

    @Test
    void testMinimumPriorityStrategySelectsNullIfOutOfRange() {
        ProviderSelectionStrategy strategy = new MinimumPriorityProviderSelectionStrategy(3);
        this.assertValueWithStrategy(null, strategy);
    }

    @Test
    void testHighestPriorityStrategySelectsHighest() {
        ProviderSelectionStrategy strategy = new HighestPriorityProviderSelectionStrategy();
        this.assertValueWithStrategy(PRIORITY_TWO_VALUE, strategy);
    }

    protected void assertValueWithStrategy(String expected, ProviderSelectionStrategy strategy) {
        BindingHierarchy<?> hierarchy = this.createHierarchy();

        InstantiationStrategy<?> provider = strategy.selectProvider(hierarchy);
        if (expected == null) {
            Assertions.assertNull(provider);
            return;
        }
        else {
            Assertions.assertNotNull(provider);

            // Don't need to provide application, all bindings are contextless singletons
            Option<? extends ObjectContainer<?>> value = Assertions.assertDoesNotThrow(() -> provider.provide(null, ComponentRequestContext.createForComponent()));
            Assertions.assertTrue(value.present());

            ObjectContainer<?> container = value.get();
            Object instance = container.instance();
            Assertions.assertEquals(expected, instance);
        }
    }
}
