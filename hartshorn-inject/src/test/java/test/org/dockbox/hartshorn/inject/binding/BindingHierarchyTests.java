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

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.binding.NativePrunableBindingHierarchy;
import org.dockbox.hartshorn.inject.provider.CompositeInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.PrototypeConstructorInstantiationStrategy;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map.Entry;

public class BindingHierarchyTests {

    private HierarchicalBinder binder() {
        return new TestHierarchicalBinder();
    }

    @Test
    void testIteratorIsSorted() {
        BindingHierarchy<Contract> hierarchy =
            new NativePrunableBindingHierarchy<>(ComponentKey.of(Contract.class));
        hierarchy.add(0,
            PrototypeConstructorInstantiationStrategy.forSingleton(ComponentKey.of(ImplementationA.class)));
        hierarchy.add(1,
            PrototypeConstructorInstantiationStrategy.forSingleton(ComponentKey.of(ImplementationB.class)));
        hierarchy.add(2,
            PrototypeConstructorInstantiationStrategy.forSingleton(ComponentKey.of(ImplementationC.class)));

        int next = 2;
        for (Entry<Integer, InstantiationStrategy<Contract>> entry : hierarchy) {
            Integer priority = entry.getKey();
            Assertions.assertEquals(next, priority.intValue());
            next--;
        }
    }

    @Test
    void testPriorityBindingsAreRetainedAndAccessible() {
        ComponentKey<Contract> key = ComponentKey.of(Contract.class);
        HierarchicalBinder binder = this.binder();

        BindingHierarchy<Contract> secondHierarchy = new NativePrunableBindingHierarchy<>(key);
        secondHierarchy.add(2,
            PrototypeConstructorInstantiationStrategy.forSingleton(ComponentKey.of(ImplementationC.class)));

        binder.hierarchy(key)
            .add(0,
                PrototypeConstructorInstantiationStrategy.forSingleton(ComponentKey.of(
                    ImplementationA.class)))
            .add(1,
                PrototypeConstructorInstantiationStrategy.forSingleton(ComponentKey.of(
                    ImplementationB.class)))
            .merge(secondHierarchy);

        BindingHierarchy<Contract> hierarchy = binder.hierarchy(key);
        Assertions.assertNotNull(hierarchy);

        Assertions.assertEquals(3, hierarchy.size());

        Option<InstantiationStrategy<Contract>> priorityZero = hierarchy.get(0);
        Assertions.assertTrue(priorityZero.present());
        Assertions.assertTrue(priorityZero.get() instanceof PrototypeConstructorInstantiationStrategy);
        Assertions.assertSame(((PrototypeConstructorInstantiationStrategy<Contract>) priorityZero.get()).type(),
            ImplementationA.class);

        Option<InstantiationStrategy<Contract>> priorityOne = hierarchy.get(1);
        Assertions.assertTrue(priorityOne.present());
        Assertions.assertTrue(priorityOne.get() instanceof PrototypeConstructorInstantiationStrategy);
        Assertions.assertSame(((PrototypeConstructorInstantiationStrategy<Contract>) priorityOne.get()).type(),
            ImplementationB.class);

        Option<InstantiationStrategy<Contract>> priorityTwo = hierarchy.get(2);
        Assertions.assertTrue(priorityTwo.present());
        Assertions.assertTrue(priorityTwo.get() instanceof PrototypeConstructorInstantiationStrategy);
        Assertions.assertSame(((PrototypeConstructorInstantiationStrategy<Contract>) priorityTwo.get()).type(),
            ImplementationC.class);
    }

    @Test
    void testContextCreatesHierarchy() {
        HierarchicalBinder binder = this.binder();
        binder.bind(LocalContract.class).to(LocalObject.class);

        BindingHierarchy<LocalContract> hierarchy =
            binder.hierarchy(ComponentKey.of(LocalContract.class));
        Assertions.assertNotNull(hierarchy);
        Assertions.assertEquals(1, hierarchy.size());

        Option<InstantiationStrategy<LocalContract>> provider =
            hierarchy.get(Priority.DEFAULT_PRIORITY);
        Assertions.assertTrue(provider.present());

        InstantiationStrategy<LocalContract> contractStrategy = provider.get();
        if (contractStrategy instanceof CompositeInstantiationStrategy<LocalContract> composite) {
            // If the provider is composed, we need to get the actual provider from it
            contractStrategy = composite.provider();
        }

        Assertions.assertTrue(contractStrategy instanceof PrototypeConstructorInstantiationStrategy);
        Assertions.assertSame(((PrototypeConstructorInstantiationStrategy<LocalContract>) contractStrategy).type(),
            LocalObject.class);
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
