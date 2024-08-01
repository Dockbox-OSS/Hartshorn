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

package test.org.dockbox.hartshorn.collection;

import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.Inject;

@HartshornTest(includeBasePackages = false)
public class ComponentCollectionTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testPriority() {
        Set<String> strings = Set.of("Hello", "World", "!");
        this.applicationContext.bind(String.class).collect(collector -> {
            for(String string : strings) {
                collector.singleton(string);
            }
        });

        this.applicationContext.bind(String.class).singleton("Hello world!");

        ComponentCollection<String> collection = this.applicationContext.get(ComponentKey.collect(String.class));
        Assertions.assertEquals(3, collection.size());
        Assertions.assertTrue(collection.containsAll(strings));

        String hello = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello world!", hello);
    }

    @Test
    @TestComponents(components = CollectionConfiguration.class)
    void testConfigurationLoadsWithDependencies() {
        ComponentCollection<String> collection = this.applicationContext.get(ComponentKey.collect(String.class));
        Assertions.assertEquals(2, collection.size());
        Assertions.assertTrue(collection.contains("Hello"));
        Assertions.assertTrue(collection.contains("World"));
    }
}
