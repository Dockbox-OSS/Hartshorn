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

package test.org.dockbox.hartshorn.components.contextual;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.provider.Provider;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.collection.CollectionBindingHierarchy;
import org.dockbox.hartshorn.inject.collection.CollectionProvider;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.annotations.Inject;
import test.org.dockbox.hartshorn.components.ComponentWithCollectionDependencies;

@HartshornTest(includeBasePackages = false)
public class CollectionScopeTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Collection component registration creates valid hierarchy")
    void testCollectionComponentRegistrationCreatesValidHierarchy() {
        this.applicationContext.bind(String.class).collect(collector -> {
            collector.singleton("John Doe");
            collector.supplier(() -> "Jane Doe");
        });

        ComponentKey<ComponentCollection<String>> componentKey = ComponentKey.collect(String.class);
        BindingHierarchy<ComponentCollection<String>> hierarchy = this.applicationContext.hierarchy(componentKey);
        Assertions.assertTrue(hierarchy instanceof CollectionBindingHierarchy<String>);

        Assertions.assertEquals(1, hierarchy.size());

        int highestPriority = hierarchy.highestPriority();
        Option<Provider<ComponentCollection<String>>> candidateProvider = hierarchy.get(highestPriority);
        Assertions.assertTrue(candidateProvider.present());

        Provider<ComponentCollection<String>> provider = candidateProvider.get();
        Assertions.assertTrue(provider instanceof CollectionProvider<String>);

        CollectionProvider<String> collectionProvider = (CollectionProvider<String>) provider;
        Set<Provider<String>> providers = collectionProvider.providers();
        Assertions.assertEquals(2, providers.size());
    }

    @Test
    @DisplayName("Collection components can be provided")
    void testCollectionComponentsCanBeProvided() {
        ComponentKey<String> nameKey = ComponentKey.of(String.class, "names");
        List<String> names = List.of("John", "Jane", "Joe");
        this.applicationContext.bind(nameKey)
                .collect(collector -> names.forEach(collector::singleton));

        ComponentKey<ComponentCollection<String>> collectionKey = nameKey.mutable().collector().build();
        ComponentCollection<String> nameCollection = this.applicationContext.get(collectionKey);

        Assertions.assertEquals(3, nameCollection.size());
        Assertions.assertTrue(nameCollection.contains("John"));
        Assertions.assertTrue(nameCollection.contains("Jane"));
        Assertions.assertTrue(nameCollection.contains("Joe"));
    }

    @Test
    @DisplayName("Collection components can be provided to a non-specific collection (List)")
    @TestComponents(components = ComponentWithCollectionDependencies.class)
    void testComponentInjectionWithoutExplicitCollection() {
        ComponentKey<String> nameKey = ComponentKey.of(String.class, "names");
        this.applicationContext.bind(nameKey).collect(collector -> {
            collector.singleton("Foo");
            collector.singleton("Bar");
        });

        ComponentWithCollectionDependencies component = this.applicationContext.get(ComponentWithCollectionDependencies.class);
        List<String> names = component.names();

        Assertions.assertNotNull(names);
        Assertions.assertEquals(2, names.size());
        Assertions.assertTrue(names.contains("Foo"));
        Assertions.assertTrue(names.contains("Bar"));
    }

    @Test
    @DisplayName("Collection components can be provided to a specific collection (Set -> TreeSet)")
    @TestComponents(components = ComponentWithCollectionDependencies.class)
    void testComponentInjectionWithExplicitCollection() {
        ComponentKey<Integer> ageKey = ComponentKey.of(Integer.class, "ages");
        this.applicationContext.bind(ageKey).collect(collector -> {
            collector.singleton(1);
            collector.singleton(2);
        });

        ComponentWithCollectionDependencies component = this.applicationContext.get(ComponentWithCollectionDependencies.class);
        Set<Integer> ages = component.ages();

        Assertions.assertNotNull(ages);
        Assertions.assertTrue(ages instanceof TreeSet<Integer>);
        Assertions.assertEquals(2, ages.size());
        Assertions.assertTrue(ages.contains(1));
        Assertions.assertTrue(ages.contains(2));
    }

    @InjectTest
    @DisplayName("Collection components can be obtained with a collection component key")
    @TestComponents(components = StaticComponentConfiguration.class)
    void testCollectionsAreCollected() {
        ComponentKey<ComponentCollection<StaticComponent>> componentKey = ComponentKey.collect(StaticComponent.class);
        ComponentCollection<StaticComponent> collection = this.applicationContext.get(componentKey);
        // Even if no bindings are present, the collection should be created
        Assertions.assertNotNull(collection);
        Assertions.assertEquals(0, collection.size());

        String[] names = {
                StaticComponentConfiguration.USER,
                StaticComponentConfiguration.ADMIN,
                StaticComponentConfiguration.GUEST
        };
        for(String name : names) {
            componentKey = componentKey.mutable().name(name).build();
            collection = this.applicationContext.get(componentKey);
            Assertions.assertEquals(1, collection.size());
            Assertions.assertEquals(name, CollectionUtilities.first(collection).name());
        }
    }
}
