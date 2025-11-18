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

package test.org.dockbox.hartshorn.inject.collection;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.collection.CollectionBindingHierarchy;
import org.dockbox.hartshorn.inject.collection.CollectionInstantiationStrategy;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class CollectionScopeTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Collection component registration creates valid hierarchy")
    void collectionComponentRegistrationCreatesValidHierarchy() {
        this.applicationContext.bind(String.class).collect(collector -> {
            collector.singleton("John Doe");
            collector.supplier(() -> "Jane Doe");
        });

        ComponentKey<ComponentCollection<String>> componentKey = ComponentKey.collect(String.class);
        BindingHierarchy<ComponentCollection<String>> hierarchy = this.applicationContext.hierarchy(componentKey);
        assertThat(hierarchy).isInstanceOf(CollectionBindingHierarchy.class);

        assertThat(hierarchy.size()).isOne();

        int highestPriority = hierarchy.highestPriority();
        Option<InstantiationStrategy<ComponentCollection<String>>> candidateProvider = hierarchy.get(highestPriority);
        assertThat(candidateProvider.present()).isTrue();

        InstantiationStrategy<ComponentCollection<String>> strategy = candidateProvider.get();
        assertThat(strategy).isInstanceOf(CollectionInstantiationStrategy.class);

        CollectionInstantiationStrategy<String> collectionProvider = (CollectionInstantiationStrategy<String>) strategy;
        Set<InstantiationStrategy<String>> strategies = collectionProvider.providers();
        assertThat(strategies).hasSize(2);
    }

    @Test
    @DisplayName("Collection components can be provided")
    void collectionComponentsCanBeProvided() {
        ComponentKey<String> nameKey = ComponentKey.of(String.class, "names");
        List<String> names = List.of("John", "Jane", "Joe");
        this.applicationContext.bind(nameKey)
                .collect(collector -> names.forEach(collector::singleton));

        ComponentKey<ComponentCollection<String>> collectionKey = nameKey.mutable().collector().build();
        ComponentCollection<String> nameCollection = this.applicationContext.get(collectionKey);

        assertThat(nameCollection).hasSize(3);
        assertThat(nameCollection).contains("John");
        assertThat(nameCollection).contains("Jane");
        assertThat(nameCollection).contains("Joe");
    }

    @Test
    @DisplayName("Collection components can be provided to a non-specific collection (List)")
    @TestComponents(ComponentWithCollectionDependencies.class)
    void componentInjectionWithoutExplicitCollection() {
        ComponentKey<String> nameKey = ComponentKey.of(String.class, "names");
        this.applicationContext.bind(nameKey).collect(collector -> {
            collector.singleton("Foo");
            collector.singleton("Bar");
        });

        ComponentWithCollectionDependencies component = this.applicationContext.get(ComponentWithCollectionDependencies.class);
        List<String> names = component.names();

        assertThat(names)
                .hasSize(2)
                .contains("Foo")
                .contains("Bar");
    }

    @Test
    @DisplayName("Collection components can be provided to a specific collection (Set -> TreeSet)")
    @TestComponents(ComponentWithCollectionDependencies.class)
    void componentInjectionWithExplicitCollection() {
        ComponentKey<Integer> ageKey = ComponentKey.of(Integer.class, "ages");
        Binder collect = this.applicationContext.bind(ageKey).collect(collector -> {
            collector.singleton(1);
            collector.singleton(2);
        });

        ComponentWithCollectionDependencies component = this.applicationContext.get(ComponentWithCollectionDependencies.class);
        Set<Integer> ages = component.ages();

        assertThat(ages).isNotNull();
        assertThat(ages).isInstanceOf(TreeSet.class);
        assertThat(ages)
                .hasSize(2)
                .contains(1)
                .contains(2);
    }

    @Test
    @DisplayName("Collection components can be obtained with a collection component key")
    @TestComponents(CompositeMembersConfiguration.class)
    void collectionsAreCollected() {
        ComponentKey<ComponentCollection<StaticComponent>> componentKey = ComponentKey.collect(StaticComponent.class);
        ComponentCollection<StaticComponent> collection = this.applicationContext.get(componentKey);
        // Even if no bindings are present, the collection should be created
        assertThat(collection).isNotNull();
        assertThat(collection).hasSize(0);

        String[] names = {
                CompositeMembersConfiguration.USER,
                CompositeMembersConfiguration.ADMIN,
                CompositeMembersConfiguration.GUEST
        };
        for(String name : names) {
            componentKey = componentKey.mutable().name(name).build();
            collection = this.applicationContext.get(componentKey);
            assertThat(collection).hasSize(1);
            assertThat(CollectionUtilities.first(collection).name()).isEqualTo(name);
        }
    }
}
