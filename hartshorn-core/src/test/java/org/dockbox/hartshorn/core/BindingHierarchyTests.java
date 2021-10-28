/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.binding.NativeBindingHierarchy;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.binding.BindingHierarchy;
import org.dockbox.hartshorn.core.binding.Bindings;
import org.dockbox.hartshorn.core.binding.ContextDrivenProvider;
import org.dockbox.hartshorn.core.binding.Provider;
import org.dockbox.hartshorn.core.binding.Providers;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map.Entry;

public class BindingHierarchyTests extends ApplicationAwareTest {

    @Test
    void testToString() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class), this.context());
        hierarchy.add(0, Providers.of(ImplementationA.class));
        hierarchy.add(1, Providers.of(ImplementationB.class));
        hierarchy.add(2, Providers.of(ImplementationC.class));

        Assertions.assertEquals("Hierarchy[Contract]: 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC", hierarchy.toString());
    }

    @Test
    void testToStringNamed() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class, Bindings.named("sample")), this.context());
        hierarchy.add(0, Providers.of(ImplementationA.class));
        hierarchy.add(1, Providers.of(ImplementationB.class));
        hierarchy.add(2, Providers.of(ImplementationC.class));

        Assertions.assertEquals("Hierarchy[Contract::sample]: 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC", hierarchy.toString());
    }

    @Test
    void testIteratorIsSorted() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class), this.context());
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

        final BindingHierarchy<Contract> secondHierarchy = new NativeBindingHierarchy<>(key, this.context());
        secondHierarchy.add(2, Providers.of(ImplementationC.class));

        this.context().hierarchy(key)
                .add(0, Providers.of(ImplementationA.class))
                .add(1, Providers.of(ImplementationB.class))
                .merge(secondHierarchy);

        final BindingHierarchy<Contract> hierarchy = this.context().hierarchy(key);
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
        this.context().bind(Key.of(LocalContract.class), LocalImpl.class);

        final BindingHierarchy<LocalContract> hierarchy = this.context().hierarchy(Key.of(LocalContract.class));
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
