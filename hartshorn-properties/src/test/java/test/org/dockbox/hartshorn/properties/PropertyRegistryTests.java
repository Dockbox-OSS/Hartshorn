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

package test.org.dockbox.hartshorn.properties;

import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.ListProperty;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.properties.ObjectProperty;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.SingleConfiguredProperty;
import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyRegistryTests {

    void assertWithRegistry(Collection<ConfiguredProperty> properties, Consumer<PropertyRegistry> registryConsumer) {
        PropertyRegistry registry = new MapPropertyRegistry();
        registry.registerAll(properties);
        registryConsumer.accept(registry);
    }

    @Test
    @DisplayName("Test object access with object on root level of registry")
    void nonNestedObjectAccess() {
        this.assertObjectAccess("sample");
    }

    @Test
    @DisplayName("Test object access with object nested in another object")
    void nestedInObjectAccess() {
        this.assertObjectAccess("sample.nested");
    }

    @Test
    @DisplayName("Test object access with object nested in another list")
    void nestedInListAccess() {
        this.assertObjectAccess("sample.nested[0]");
    }

    void assertObjectAccess(String objectKey) {
        this.assertWithRegistry(List.of(
                new SingleConfiguredProperty(objectKey + ".one", "one"),
                new SingleConfiguredProperty(objectKey + ".two", "two")
        ), registry -> {
            Option<ObjectProperty> object = registry.object(objectKey);
            assertThat(object.present()).isTrue();
            object.peek(obj -> {
                assertThat(obj.name()).isEqualTo(objectKey);
                Option<ValueProperty> one = obj.get("one");
                assertThat(one.present()).isTrue();
                assertThat(one.get().value().get()).isEqualTo("one");
                assertThat(one.get().name()).isEqualTo(objectKey + ".one");

                Option<ValueProperty> two = obj.get("two");
                assertThat(two.present()).isTrue();
                assertThat(two.get().value().get()).isEqualTo("two");
                assertThat(two.get().name()).isEqualTo(objectKey + ".two");
            });
        });
    }

    @Test
    @DisplayName("Test list access with list on root level of registry")
    void nonNestedListAccess() {
        this.testListAccess("sample");
    }

    @Test
    @DisplayName("Test list access with list nested in another object")
    void nestedInObjectListAccess() {
        this.testListAccess("sample.nested");
    }

    @Test
    @DisplayName("Test list access with list nested in another list")
    void nestedInListListAccess() {
        this.testListAccess("sample.nested[0]");
    }

    void testListAccess(String listKey) {
        this.assertWithRegistry(List.of(
                new SingleConfiguredProperty(listKey + "[0]", "one"),
                new SingleConfiguredProperty(listKey + "[1]", "two")
        ), registry -> {
            Option<ListProperty> list = registry.list(listKey);
            assertThat(list.present()).isTrue();
            list.peek(l -> {
                assertThat(l.name()).isEqualTo(listKey);
                Option<ValueProperty> one = l.get(0);
                assertThat(one.present()).isTrue();
                assertThat(one.get().value().get()).isEqualTo("one");
                assertThat(one.get().name()).isEqualTo(listKey + "[0]");

                Option<ValueProperty> two = l.get(1);
                assertThat(two.present()).isTrue();
                assertThat(two.get().value().get()).isEqualTo("two");
                assertThat(two.get().name()).isEqualTo(listKey + "[1]");
            });
        });
    }

    @Test
    @DisplayName("Test value access with value on root level of registry")
    void nonNestedValueAccess() {
        this.testValueAccess("sample");
    }

    @Test
    @DisplayName("Test value access with value nested in another object")
    void nestedInObjectValueAccess() {
        this.testValueAccess("sample.nested");
    }

    @Test
    @DisplayName("Test value access with value nested in another list")
    void nestedInListValueAccess() {
        this.testValueAccess("sample.nested[0]");
    }

    void testValueAccess(String valueKey) {
        this.assertWithRegistry(List.of(
                new SingleConfiguredProperty(valueKey, "value")
        ), registry -> {
            Option<ValueProperty> value = registry.get(valueKey);
            assertThat(value.present()).isTrue();
            value.peek(v -> {
                assertThat(v.name()).isEqualTo(valueKey);
                assertThat(v.value().get()).isEqualTo("value");
            });
        });
    }

    @Test
    @DisplayName("Test access to deeply nested value with step-by-step access")
    void complexAccess() {
        this.assertWithRegistry(
                List.of(new SingleConfiguredProperty("sample[0][1][0].property.sample[1].value", "value")),
                registry -> {
                    // sample
                    Option<ListProperty> sample = registry.list("sample");
                    assertThat(sample.present()).isTrue();

                    // sample[0]
                    Option<ListProperty> sampleIndex0 = sample.get().list(0);
                    assertThat(sampleIndex0.present()).isTrue();

                    // sample[0][1]
                    Option<ListProperty> sampleIndex0Index1 = sampleIndex0.get().list(1);
                    assertThat(sampleIndex0Index1.present()).isTrue();

                    // sample[0][1][0]
                    Option<ObjectProperty> sampleIndex0Index1Index0 = sampleIndex0Index1.get().object(0);
                    assertThat(sampleIndex0Index1Index0.present()).isTrue();

                    // sample[0][1][0].property
                    Option<ObjectProperty> sampleIndex0Index1Index0Property = sampleIndex0Index1Index0.get().object("property");
                    assertThat(sampleIndex0Index1Index0Property.present()).isTrue();

                    // sample[0][1][0].property.sample
                    Option<ListProperty> sampleIndex0Index1Index0PropertySample = sampleIndex0Index1Index0Property.get().list("sample");
                    assertThat(sampleIndex0Index1Index0PropertySample.present()).isTrue();

                    // sample[0][1][0].property.sample[1]
                    Option<ObjectProperty> sampleIndex0Index1Index0PropertySampleIndex1 = sampleIndex0Index1Index0PropertySample.get().object(1);
                    assertThat(sampleIndex0Index1Index0PropertySampleIndex1.present()).isTrue();

                    // sample[0][1][0].property.sample[1].value
                    Option<ValueProperty> sampleIndex0Index1Index0PropertySampleIndex1Value = sampleIndex0Index1Index0PropertySampleIndex1.get().get("value");
                    assertThat(sampleIndex0Index1Index0PropertySampleIndex1Value.present()).isTrue();
                    assertThat(sampleIndex0Index1Index0PropertySampleIndex1Value.get().value().get()).isEqualTo("value");
                });
    }
}
