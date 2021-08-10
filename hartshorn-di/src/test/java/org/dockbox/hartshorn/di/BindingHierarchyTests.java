package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.binding.BindingHierarchy;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.binding.Provider;
import org.dockbox.hartshorn.di.binding.Providers;
import org.dockbox.hartshorn.di.binding.StaticProvider;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map.Entry;

@ExtendWith(HartshornRunner.class)
public class BindingHierarchyTests {

    @Test
    void testToString() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class));
        hierarchy.add(0, Providers.of(ImplementationA.class));
        hierarchy.add(1, Providers.of(ImplementationB.class));
        hierarchy.add(2, Providers.of(ImplementationC.class));

        Assertions.assertEquals("Hierarchy[Contract]: 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC", hierarchy.toString());
    }

    @Test
    void testToStringNamed() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class, Bindings.named("sample")));
        hierarchy.add(0, Providers.of(ImplementationA.class));
        hierarchy.add(1, Providers.of(ImplementationB.class));
        hierarchy.add(2, Providers.of(ImplementationC.class));

        Assertions.assertEquals("Hierarchy[Contract::sample]: 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC", hierarchy.toString());
    }

    @Test
    void testIteratorIsSorted() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class));
        hierarchy.add(0, Providers.of(ImplementationA.class));
        hierarchy.add(1, Providers.of(ImplementationB.class));
        hierarchy.add(2, Providers.of(ImplementationC.class));

        int next = 0;
        for (final Entry<Integer, Provider<Contract>> entry : hierarchy) {
            final Integer priority = entry.getKey();
            Assertions.assertEquals(next, priority.intValue());
            next++;
        }
    }

    @Test
    void testApplicationContextHierarchyControl() {
        final Key<Contract> key = Key.of(Contract.class);

        final BindingHierarchy<Contract> secondHierarchy = new NativeBindingHierarchy<>(key);
        secondHierarchy.add(2, Providers.of(ImplementationC.class));

        Hartshorn.context().hierarchy(key)
                .add(0, Providers.of(ImplementationA.class))
                .add(1, Providers.of(ImplementationB.class))
                .merge(secondHierarchy);

        final BindingHierarchy<Contract> hierarchy = Hartshorn.context().hierarchy(key);
        Assertions.assertNotNull(hierarchy);

        Assertions.assertEquals(3, hierarchy.size());

        final Exceptional<Provider<Contract>> priorityZero = hierarchy.get(0);
        Assertions.assertTrue(priorityZero.present());
        Assertions.assertTrue(priorityZero.get() instanceof StaticProvider);
        Assertions.assertEquals(((StaticProvider<Contract>) priorityZero.get()).target(), ImplementationA.class);

        final Exceptional<Provider<Contract>> priorityOne = hierarchy.get(1);
        Assertions.assertTrue(priorityOne.present());
        Assertions.assertTrue(priorityOne.get() instanceof StaticProvider);
        Assertions.assertEquals(((StaticProvider<Contract>) priorityOne.get()).target(), ImplementationB.class);

        final Exceptional<Provider<Contract>> priorityTwo = hierarchy.get(2);
        Assertions.assertTrue(priorityTwo.present());
        Assertions.assertTrue(priorityTwo.get() instanceof StaticProvider);
        Assertions.assertEquals(((StaticProvider<Contract>) priorityTwo.get()).target(), ImplementationC.class);
    }

    @Test
    void testContextCreatesHierarchy() {
        interface LocalContract {}
        class LocalImpl implements LocalContract {}

        Hartshorn.context().bind(Key.of(LocalContract.class), LocalImpl.class);

        final BindingHierarchy<LocalContract> hierarchy = Hartshorn.context().hierarchy(Key.of(LocalContract.class));
        Assertions.assertNotNull(hierarchy);
        Assertions.assertEquals(1, hierarchy.size());

        final Exceptional<Provider<LocalContract>> provider = hierarchy.get(-1);
        Assertions.assertTrue(provider.present());
        Assertions.assertTrue(provider.get() instanceof StaticProvider);
        Assertions.assertEquals(((StaticProvider<LocalContract>) provider.get()).target(), LocalImpl.class);
    }

    private interface Contract {}
    private static class ImplementationA implements Contract {}
    private static class ImplementationB implements Contract {}
    private static class ImplementationC implements Contract {}
}
