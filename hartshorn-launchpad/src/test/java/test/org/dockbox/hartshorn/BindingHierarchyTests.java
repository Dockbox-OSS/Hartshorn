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

package test.org.dockbox.hartshorn;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.component.QualifierKey;
import org.dockbox.hartshorn.inject.provider.ComposedProvider;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.provider.Provider;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.NativePrunableBindingHierarchy;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Map.Entry;

import org.dockbox.hartshorn.inject.Inject;

@HartshornTest(includeBasePackages = false)
public class BindingHierarchyTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testToString() {
        BindingHierarchy<Contract> hierarchy = new NativePrunableBindingHierarchy<>(ComponentKey.of(Contract.class), this.applicationContext);
        hierarchy.add(0, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationA.class)));
        hierarchy.add(1, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationB.class)));
        hierarchy.add(2, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationC.class)));

        Assertions.assertEquals("Hierarchy<Contract>: 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC", hierarchy.toString());
    }

    @Test
    void testToStringNamed() {
        BindingHierarchy<Contract> hierarchy = new NativePrunableBindingHierarchy<>(ComponentKey.of(Contract.class, "sample"), this.applicationContext);
        hierarchy.add(0, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationA.class)));
        hierarchy.add(1, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationB.class)));
        hierarchy.add(2, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationC.class)));

        Assertions.assertEquals("Hierarchy<Contract> Named{value=sample}: 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC", hierarchy.toString());
    }

    @Test
    void testToStringMultipleQualifiers() {
        ComponentKey<Contract> key = ComponentKey.builder(Contract.class)
                .name("sample")
                .qualifier(QualifierKey.of(VersionQualifier.class, Map.of("value", Version.V2)))
                .build();

        BindingHierarchy<Contract> hierarchy = new NativePrunableBindingHierarchy<>(key, this.applicationContext);
        hierarchy.add(0, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationA.class)));
        hierarchy.add(1, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationB.class)));
        hierarchy.add(2, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationC.class)));

        String hierarchyString = hierarchy.toString();
        Assertions.assertTrue(hierarchyString.startsWith("Hierarchy<Contract> "));
        Assertions.assertTrue(hierarchyString.endsWith(": 0: ImplementationA -> 1: ImplementationB -> 2: ImplementationC"));

        // Check with contains,as order is not guaranteed
        Assertions.assertTrue(hierarchyString.contains("Named{value=sample}"));
        Assertions.assertTrue(hierarchyString.contains("VersionQualifier{value=V2}"));
    }

    @Test
    void testIteratorIsSorted() {
        BindingHierarchy<Contract> hierarchy = new NativePrunableBindingHierarchy<>(ComponentKey.of(Contract.class), this.applicationContext);
        hierarchy.add(0, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationA.class)));
        hierarchy.add(1, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationB.class)));
        hierarchy.add(2, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationC.class)));

        int next = 2;
        for (Entry<Integer, Provider<Contract>> entry : hierarchy) {
            Integer priority = entry.getKey();
            Assertions.assertEquals(next, priority.intValue());
            next--;
        }
    }

    @Test
    void testApplicationContextHierarchyControl() {
        ComponentKey<Contract> key = ComponentKey.of(Contract.class);

        BindingHierarchy<Contract> secondHierarchy = new NativePrunableBindingHierarchy<>(key, this.applicationContext);
        secondHierarchy.add(2, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationC.class)));

        this.applicationContext.hierarchy(key)
                .add(0, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationA.class)))
                .add(1, ContextDrivenProvider.forSingleton(ComponentKey.of(ImplementationB.class)))
                .merge(secondHierarchy);

        BindingHierarchy<Contract> hierarchy = this.applicationContext.hierarchy(key);
        Assertions.assertNotNull(hierarchy);

        Assertions.assertEquals(3, hierarchy.size());

        Option<Provider<Contract>> priorityZero = hierarchy.get(0);
        Assertions.assertTrue(priorityZero.present());
        Assertions.assertTrue(priorityZero.get() instanceof ContextDrivenProvider);
        Assertions.assertSame(((ContextDrivenProvider<Contract>) priorityZero.get()).type(), ImplementationA.class);

        Option<Provider<Contract>> priorityOne = hierarchy.get(1);
        Assertions.assertTrue(priorityOne.present());
        Assertions.assertTrue(priorityOne.get() instanceof ContextDrivenProvider);
        Assertions.assertSame(((ContextDrivenProvider<Contract>) priorityOne.get()).type(), ImplementationB.class);

        Option<Provider<Contract>> priorityTwo = hierarchy.get(2);
        Assertions.assertTrue(priorityTwo.present());
        Assertions.assertTrue(priorityTwo.get() instanceof ContextDrivenProvider);
        Assertions.assertSame(((ContextDrivenProvider<Contract>) priorityTwo.get()).type(), ImplementationC.class);
    }

    @Test
    void testContextCreatesHierarchy() {
        this.applicationContext.bind(LocalContract.class).to(LocalImpl.class);

        BindingHierarchy<LocalContract> hierarchy = this.applicationContext.hierarchy(ComponentKey.of(LocalContract.class));
        Assertions.assertNotNull(hierarchy);
        Assertions.assertEquals(1, hierarchy.size());

        Option<Provider<LocalContract>> provider = hierarchy.get(-1);
        Assertions.assertTrue(provider.present());

        Provider<LocalContract> contractProvider = provider.get();
        if (contractProvider instanceof ComposedProvider<LocalContract> composedProvider) {
            // If the provider is composed, we need to get the actual provider from it
            contractProvider = composedProvider.provider();
        }

        Assertions.assertTrue(contractProvider instanceof ContextDrivenProvider);
        Assertions.assertSame(((ContextDrivenProvider<LocalContract>) contractProvider).type(), LocalImpl.class);
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

    @SuppressWarnings("unused")
    private @interface VersionQualifier {
        Version value();
    }

    private enum Version {
        V1, V2
    }
}
