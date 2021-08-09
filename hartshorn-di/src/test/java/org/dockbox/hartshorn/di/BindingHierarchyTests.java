package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.binding.BindingHierarchy;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.binding.Provider;
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
        hierarchy.add(0, new StaticProvider<>(ImplementationA.class));
        hierarchy.add(1, new StaticProvider<>(ImplementationB.class));
        hierarchy.add(2, new StaticProvider<>(ImplementationC.class));

        Assertions.assertEquals("Hierarchy[Contract]: 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC", hierarchy.toString());
    }

    @Test
    void testToStringNamed() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class, Bindings.named("sample")));
        hierarchy.add(0, new StaticProvider<>(ImplementationA.class));
        hierarchy.add(1, new StaticProvider<>(ImplementationB.class));
        hierarchy.add(2, new StaticProvider<>(ImplementationC.class));

        Assertions.assertEquals("Hierarchy[Contract::sample]: 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC", hierarchy.toString());
    }

    @Test
    void testIteratorIsSorted() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class));
        hierarchy.add(0, new StaticProvider<>(ImplementationA.class));
        hierarchy.add(1, new StaticProvider<>(ImplementationB.class));
        hierarchy.add(2, new StaticProvider<>(ImplementationC.class));

        int next = 0;
        for (final Entry<Integer, Provider<Contract>> entry : hierarchy) {
            final Integer priority = entry.getKey();
            Assertions.assertEquals(next, priority.intValue());
            next++;
        }
    }

    @Test
    void testApplicationContextHierarchyControl() {
        final BindingHierarchy<Contract> hierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class));
        hierarchy.add(0, new StaticProvider<>(ImplementationA.class));
        hierarchy.add(1, new StaticProvider<>(ImplementationB.class));

        final BindingHierarchy<Contract> secondHierarchy = new NativeBindingHierarchy<>(Key.of(Contract.class));
        secondHierarchy.add(2, new StaticProvider<>(ImplementationC.class));

        Hartshorn.context().add(hierarchy);
        Hartshorn.context().merge(secondHierarchy);

        final Exceptional<BindingHierarchy<Contract>> stored = Hartshorn.context().hierarchy(Key.of(Contract.class));
        Assertions.assertTrue(stored.present());

        final BindingHierarchy<Contract> storedHierarchy = stored.get();
        Assertions.assertEquals(3, storedHierarchy.size());

        final Exceptional<Provider<Contract>> priorityZero = storedHierarchy.get(0);
        Assertions.assertTrue(priorityZero.present());
        Assertions.assertTrue(priorityZero.get() instanceof StaticProvider);
        Assertions.assertEquals(((StaticProvider<Contract>) priorityZero.get()).target(), ImplementationA.class);

        final Exceptional<Provider<Contract>> priorityOne = storedHierarchy.get(1);
        Assertions.assertTrue(priorityOne.present());
        Assertions.assertTrue(priorityOne.get() instanceof StaticProvider);
        Assertions.assertEquals(((StaticProvider<Contract>) priorityOne.get()).target(), ImplementationB.class);

        final Exceptional<Provider<Contract>> priorityTwo = storedHierarchy.get(2);
        Assertions.assertTrue(priorityTwo.present());
        Assertions.assertTrue(priorityTwo.get() instanceof StaticProvider);
        Assertions.assertEquals(((StaticProvider<Contract>) priorityTwo.get()).target(), ImplementationC.class);
    }

    @Test
    void testContextCreatesHierarchy() {
        interface LocalContract {}
        class LocalImpl implements LocalContract {}

        Hartshorn.context().bind(Key.of(LocalContract.class), LocalImpl.class);

        final Exceptional<BindingHierarchy<LocalContract>> hierarchy = Hartshorn.context().hierarchy(Key.of(LocalContract.class));
        Assertions.assertTrue(hierarchy.present());
        final BindingHierarchy<LocalContract> storedHierarchy = hierarchy.get();
        Assertions.assertEquals(1, storedHierarchy.size());

        final Exceptional<Provider<LocalContract>> provider = storedHierarchy.get(-1);
        Assertions.assertTrue(provider.present());
        Assertions.assertTrue(provider.get() instanceof StaticProvider);
        Assertions.assertEquals(((StaticProvider<LocalContract>) provider.get()).target(), LocalImpl.class);
    }

    private interface Contract {}
    private static class ImplementationA implements Contract {}
    private static class ImplementationB implements Contract {}
    private static class ImplementationC implements Contract {}
}
