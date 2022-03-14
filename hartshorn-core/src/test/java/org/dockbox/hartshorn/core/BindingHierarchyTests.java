/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.binding.BindingHierarchy;
import org.dockbox.hartshorn.core.binding.ContextDrivenProvider;
import org.dockbox.hartshorn.core.binding.NativeBindingHierarchy;
import org.dockbox.hartshorn.core.binding.Provider;
import org.dockbox.hartshorn.core.binding.Providers;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map.Entry;

import javax.inject.Inject;

@HartshornTest
public class BindingHierarchyTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testToString() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class), this.applicationContext);
        hierarchy.add(0, Providers.of(ImplementationA.class));
        hierarchy.add(1, Providers.of(ImplementationB.class));
        hierarchy.add(2, Providers.of(ImplementationC.class));

        Assertions.assertEquals("Hierarchy[Contract]: 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC", hierarchy.toString());
    }

    @Test
    void testToStringNamed() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class, "sample"), this.applicationContext);
        hierarchy.add(0, Providers.of(ImplementationA.class));
        hierarchy.add(1, Providers.of(ImplementationB.class));
        hierarchy.add(2, Providers.of(ImplementationC.class));

        Assertions.assertEquals("Hierarchy[Contract::sample]: 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC", hierarchy.toString());
    }

    @Test
    void testIteratorIsSorted() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class), this.applicationContext);
        hierarchy.add(0, Providers.of(ImplementationA.class));
        hierarchy.add(1, Providers.of(ImplementationB.class));
        hierarchy.add(2, Providers.of(ImplementationC.class));

        int next = 2;
        for (final Entry<Integer, Provider<Contract>> entry : hierarchy) {
            final Integer priority = entry.getKey();
            Assertions.assertEquals(next, priority.intValue());
            next--;
        }
    }

    @Test
    void testApplicationContextHierarchyControl() {
        final Key<Contract> key = Key.of(Contract.class);

        final BindingHierarchy<Contract> secondHierarchy = new NativeBindingHierarchy<>(key, this.applicationContext);
        secondHierarchy.add(2, Providers.of(ImplementationC.class));

        this.applicationContext.hierarchy(key)
                .add(0, Providers.of(ImplementationA.class))
                .add(1, Providers.of(ImplementationB.class))
                .merge(secondHierarchy);

        final BindingHierarchy<Contract> hierarchy = this.applicationContext.hierarchy(key);
        Assertions.assertNotNull(hierarchy);

        Assertions.assertEquals(3, hierarchy.size());

        final Exceptional<Provider<Contract>> priorityZero = hierarchy.get(0);
        Assertions.assertTrue(priorityZero.present());
        Assertions.assertTrue(priorityZero.get() instanceof ContextDrivenProvider);
        Assertions.assertEquals(((ContextDrivenProvider<Contract>) priorityZero.get()).context().type(), ImplementationA.class);

        final Exceptional<Provider<Contract>> priorityOne = hierarchy.get(1);
        Assertions.assertTrue(priorityOne.present());
        Assertions.assertTrue(priorityOne.get() instanceof ContextDrivenProvider);
        Assertions.assertEquals(((ContextDrivenProvider<Contract>) priorityOne.get()).context().type(), ImplementationB.class);

        final Exceptional<Provider<Contract>> priorityTwo = hierarchy.get(2);
        Assertions.assertTrue(priorityTwo.present());
        Assertions.assertTrue(priorityTwo.get() instanceof ContextDrivenProvider);
        Assertions.assertEquals(((ContextDrivenProvider<Contract>) priorityTwo.get()).context().type(), ImplementationC.class);
    }

    @Test
    void testContextCreatesHierarchy() {
        this.applicationContext.bind(Key.of(LocalContract.class), LocalImpl.class);

        final BindingHierarchy<LocalContract> hierarchy = this.applicationContext.hierarchy(Key.of(LocalContract.class));
        Assertions.assertNotNull(hierarchy);
        Assertions.assertEquals(1, hierarchy.size());

        final Exceptional<Provider<LocalContract>> provider = hierarchy.get(-1);
        Assertions.assertTrue(provider.present());
        Assertions.assertTrue(provider.get() instanceof ContextDrivenProvider);
        Assertions.assertEquals(((ContextDrivenProvider<LocalContract>) provider.get()).context().type(), LocalImpl.class);
    }

    interface LocalContract {
    }
    static class LocalImpl implements LocalContract {
    }

    private interface Contract {
    }

    private static class ImplementationA implements Contract {
    }

    private static class ImplementationB implements Contract {
    }

    private static class ImplementationC implements Contract {
    }
}
