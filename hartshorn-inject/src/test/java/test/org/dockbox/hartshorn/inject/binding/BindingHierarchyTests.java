/*
 * Copyright 2019-2025 the original author or authors.
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

package test.org.dockbox.hartshorn.inject.binding;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.binding.NativePrunableBindingHierarchy;
import org.dockbox.hartshorn.inject.provider.CompositeInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.PrototypeConstructorInstantiationStrategy;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.assertThat;

class BindingHierarchyTests {

    private HierarchicalBinder binder() {
        return new TestHierarchicalBinder();
    }

    @Test
    void iteratorIsSorted() {
        BindingHierarchy<Contract> hierarchy =
            new NativePrunableBindingHierarchy<>(ComponentKey.of(Contract.class));
        hierarchy.add(0, PrototypeConstructorInstantiationStrategy.forSingleton(
                ComponentKey.of(ImplementationA.class)
        ));
        hierarchy.add(1, PrototypeConstructorInstantiationStrategy.forSingleton(
                ComponentKey.of(ImplementationB.class)
        ));
        hierarchy.add(2, PrototypeConstructorInstantiationStrategy.forSingleton(
                ComponentKey.of(ImplementationC.class)
        ));

        int next = 2;
        for (Entry<Integer, InstantiationStrategy<Contract>> entry : hierarchy) {
            Integer priority = entry.getKey();
            assertThat(priority.intValue()).isEqualTo(next);
            next--;
        }
    }

    @Test
    void priorityBindingsAreRetainedAndAccessible() {
        ComponentKey<Contract> key = ComponentKey.of(Contract.class);
        HierarchicalBinder binder = this.binder();

        BindingHierarchy<Contract> secondHierarchy = new NativePrunableBindingHierarchy<>(key);
        secondHierarchy.add(2, PrototypeConstructorInstantiationStrategy.forSingleton(
                ComponentKey.of(ImplementationC.class)
        ));

        binder.hierarchy(key)
            .add(0,
                PrototypeConstructorInstantiationStrategy.forSingleton(ComponentKey.of(
                    ImplementationA.class)))
            .add(1,
                PrototypeConstructorInstantiationStrategy.forSingleton(ComponentKey.of(
                    ImplementationB.class)))
            .merge(secondHierarchy);

        BindingHierarchy<Contract> hierarchy = binder.hierarchy(key);
        assertThat(hierarchy).isNotNull();

        assertThat(hierarchy.size()).isEqualTo(3);

        Option<InstantiationStrategy<Contract>> priorityZero = hierarchy.get(0);
        assertThat(priorityZero)
                .asInstanceOf(InstanceOfAssertFactories.type(Option.class))
                .matches(Option::present)
                .extracting(Option::get)
                .asInstanceOf(InstanceOfAssertFactories.type(
                        PrototypeConstructorInstantiationStrategy.class
                ))
                .extracting(PrototypeConstructorInstantiationStrategy::type)
                .isSameAs(ImplementationA.class);

        Option<InstantiationStrategy<Contract>> priorityOne = hierarchy.get(1);
        assertThat(priorityOne)
                .asInstanceOf(InstanceOfAssertFactories.type(Option.class))
                .matches(Option::present)
                .extracting(Option::get)
                .asInstanceOf(InstanceOfAssertFactories.type(
                        PrototypeConstructorInstantiationStrategy.class
                ))
                .extracting(PrototypeConstructorInstantiationStrategy::type)
                .isSameAs(ImplementationB.class);

        Option<InstantiationStrategy<Contract>> priorityTwo = hierarchy.get(2);
        assertThat(priorityTwo)
                .asInstanceOf(InstanceOfAssertFactories.type(Option.class))
                .matches(Option::present)
                .extracting(Option::get)
                .asInstanceOf(InstanceOfAssertFactories.type(
                        PrototypeConstructorInstantiationStrategy.class
                ))
                .extracting(PrototypeConstructorInstantiationStrategy::type)
                .isSameAs(ImplementationC.class);
    }

    @Test
    void contextCreatesHierarchy() {
        HierarchicalBinder binder = this.binder();
        binder.bind(LocalContract.class).to(LocalObject.class);

        BindingHierarchy<LocalContract> hierarchy =
            binder.hierarchy(ComponentKey.of(LocalContract.class));
        assertThat(hierarchy).isNotNull();
        assertThat(hierarchy.size()).isOne();

        Option<InstantiationStrategy<LocalContract>> provider =
            hierarchy.get(Priority.DEFAULT_PRIORITY);
        assertThat(provider.present()).isTrue();

        InstantiationStrategy<LocalContract> contractStrategy = provider.get();
        if (contractStrategy instanceof CompositeInstantiationStrategy<LocalContract> composite) {
            // If the provider is composed, we need to get the actual provider from it
            contractStrategy = composite.provider();
        }

        assertThat(contractStrategy)
                .asInstanceOf(InstanceOfAssertFactories.type(
                        PrototypeConstructorInstantiationStrategy.class
                ))
                .extracting(PrototypeConstructorInstantiationStrategy::type)
                .isSameAs(LocalObject.class);
    }

    interface LocalContract {
    }

    static class LocalObject implements LocalContract {
    }

    private interface Contract {
    }

    private static class ImplementationA implements Contract {
    }

    private static class ImplementationB implements Contract {
    }

    private static class ImplementationC implements Contract {
    }

    @SuppressWarnings("unused")
    private @interface VersionQualifier {
        Version value();
    }

    private enum Version {
        V1,
        V2
    }
}
