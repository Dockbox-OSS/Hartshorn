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

import java.util.Set;

import org.dockbox.hartshorn.launchpad.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.annotations.Inject;

@HartshornIntegrationTest(includeBasePackages = false)
class ComponentCollectionTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void priority() {
        Set<String> strings = Set.of("Hello", "World", "!");
        this.applicationContext.bind(String.class).collect(collector -> {
            for(String string : strings) {
                collector.singleton(string);
            }
        });

        this.applicationContext.bind(String.class).singleton("Hello world!");

        ComponentCollection<String> collection = this.applicationContext.get(ComponentKey.collect(String.class));
        assertThat(collection).hasSize(3);
        assertThat(collection).containsAll(strings);

        String hello = this.applicationContext.get(String.class);
        assertThat(hello).isEqualTo("Hello world!");
    }

    @Test
    @TestComponents(CollectionConfiguration.class)
    void configurationLoadsWithDependencies() {
        ComponentCollection<String> collection = this.applicationContext.get(ComponentKey.collect(String.class));
        assertThat(collection).hasSize(2);
        assertThat(collection).contains("Hello");
        assertThat(collection).contains("World");
    }
}
