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

package org.dockbox.hartshorn.data.mapping;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.data.Address;
import org.dockbox.hartshorn.data.ComponentWithUserValue;
import org.dockbox.hartshorn.data.Element;
import org.dockbox.hartshorn.data.EntityElement;
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.MultiElement;
import org.dockbox.hartshorn.data.NestedElement;
import org.dockbox.hartshorn.data.PersistentElement;
import org.dockbox.hartshorn.data.User;
import org.dockbox.hartshorn.data.annotations.UseConfigurations;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.data.jackson.JacksonObjectMapper;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import javax.inject.Inject;

@HartshornTest
@UsePersistence
@UseConfigurations
public class ObjectMappingTests {

    @Test
    void testPropertyHolder() {
        final ObjectMapper mapper = this.applicationContext.get(ObjectMapper.class);
        final PropertyHolder propertyHolder = this.applicationContext.get(PropertyHolder.class);

        propertyHolder.set("user.name", "John Doe");

        final Result<User> user = propertyHolder.get("user", User.class);
        Assertions.assertTrue(user.present());
        Assertions.assertEquals("John Doe", user.get().name());

        propertyHolder.set("user.address", new Address("Darwin City", "Darwin Street", 12));

        final Result<Address> address = propertyHolder.get("user.address", Address.class);
        Assertions.assertTrue(address.present());
        Assertions.assertEquals("Darwin City", address.get().city());
        Assertions.assertEquals("Darwin Street", address.get().street());
        Assertions.assertEquals(12, address.get().number());

        propertyHolder.set("user.address.street", "Darwin Lane");

        final Result<Address> address2 = propertyHolder.get("user.address", Address.class);
        Assertions.assertTrue(address2.present());
        Assertions.assertEquals("Darwin City", address2.get().city());
        Assertions.assertEquals("Darwin Lane", address2.get().street());
        Assertions.assertEquals(12, address2.get().number());
    }

    @Test
    void testValueComponents() {
        final PropertyHolder propertyHolder = this.applicationContext.get(PropertyHolder.class);
        propertyHolder.set("user.name", "John Doe");
        propertyHolder.set("user.address.city", "Darwin City");
        propertyHolder.set("user.address.street", "Darwin Lane");
        propertyHolder.set("user.address.number", 12);

        final ComponentWithUserValue component = this.applicationContext.get(ComponentWithUserValue.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.user());

        Assertions.assertEquals("John Doe", component.user().name());
        Assertions.assertEquals("Darwin City", component.user().address().city());
        Assertions.assertEquals("Darwin Lane", component.user().address().street());
        Assertions.assertEquals(12, component.user().address().number());
    }

    @Inject
    private ApplicationContext applicationContext;

    private static Stream<Arguments> serializationElements() {
        return Stream.of(
                Arguments.of(FileFormats.JSON, new PersistentElement(), "{\"name\":\"sample\"}"),
                Arguments.of(FileFormats.YAML, new PersistentElement(), "name: sample"),
                // Note that the element is not an explicit entity, so no alias is used for the root element in XML
                Arguments.of(FileFormats.XML, new PersistentElement(), "<PersistentElement><name>sample</name></PersistentElement>"),
                Arguments.of(FileFormats.TOML, new PersistentElement(), "name='sample'"),
                Arguments.of(FileFormats.PROPERTIES, new PersistentElement(), "name=sample"),

                Arguments.of(FileFormats.JSON, new EntityElement(), "{\"name\":\"sample\"}"),
                Arguments.of(FileFormats.YAML, new EntityElement(), "name: sample"),
                Arguments.of(FileFormats.XML, new EntityElement(), "<entity><name>sample</name></entity>"),
                Arguments.of(FileFormats.TOML, new EntityElement(), "name='sample'"),
                Arguments.of(FileFormats.PROPERTIES, new EntityElement(), "name=sample"),

                Arguments.of(FileFormats.JSON, new NestedElement(new EntityElement()), "{\"child\":{\"name\":\"sample\"}}"),
                Arguments.of(FileFormats.YAML, new NestedElement(new EntityElement()), "child:\n  name: sample"),
                Arguments.of(FileFormats.XML, new NestedElement(new EntityElement()), "<NestedElement><child><name>sample</name></child></NestedElement>"),
                Arguments.of(FileFormats.TOML, new NestedElement(new EntityElement()), "child.name='sample'"),
                Arguments.of(FileFormats.PROPERTIES, new NestedElement(new EntityElement()), "child.name=sample"),

                Arguments.of(FileFormats.JSON, new MultiElement(), "{\"name\":\"sample\",\"other\":\"sample\"}"),
                Arguments.of(FileFormats.YAML, new MultiElement(), "name: sample\nother: sample"),
                Arguments.of(FileFormats.XML, new MultiElement(), "<MultiElement><name>sample</name><other>sample</other></MultiElement>"),
                Arguments.of(FileFormats.TOML, new MultiElement(), "name='sample'\nother='sample'"),
                Arguments.of(FileFormats.PROPERTIES, new MultiElement(), "name=sample\nother=sample")
        );
    }

    @ParameterizedTest
    @MethodSource("serializationElements")
    void testObjectSerialization(final FileFormat fileFormat, final Element content, final String expected) {
        final ObjectMapper mapper = this.applicationContext.get(JacksonObjectMapper.class);
        mapper.fileType(fileFormat);

        content.name("sample");
        final Result<String> result = mapper.write(content);

        Assertions.assertTrue(result.present());
        Assertions.assertEquals(expected.replaceAll("[ \n]+", ""), result.get().replaceAll("[ \n\r]+", ""));
    }

    @ParameterizedTest
    @MethodSource("serializationElements")
    void testObjectDeserialization(final FileFormat fileFormat, final Element expected, final String content) {
        final ObjectMapper mapper = this.applicationContext.get(JacksonObjectMapper.class);
        mapper.fileType(fileFormat);
        expected.name("sample");

        final Result<? extends Element> result = mapper.read(content, expected.getClass());

        if (result.absent()) throw new RuntimeException(result.error().getMessage());
        Assertions.assertTrue(result.present());
        Assertions.assertEquals(expected.getClass(), result.type());
        Assertions.assertEquals(expected, result.get());
    }
}
