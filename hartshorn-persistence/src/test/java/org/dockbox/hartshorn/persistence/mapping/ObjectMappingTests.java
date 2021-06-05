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

package org.dockbox.hartshorn.persistence.mapping;

import org.dockbox.hartshorn.persistence.Element;
import org.dockbox.hartshorn.persistence.EntityElement;
import org.dockbox.hartshorn.persistence.MultiElement;
import org.dockbox.hartshorn.persistence.NestedElement;
import org.dockbox.hartshorn.persistence.PersistentElement;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ObjectMappingTests {

    private static Stream<Arguments> serialisationElements() {
        return Stream.of(
                Arguments.of(FileType.JSON, new PersistentElement(), "{\"name\":\"sample\"}"),
                Arguments.of(FileType.YAML, new PersistentElement(), "name: sample"),
                // Note that the element is not an explicit entity, so no alias is used for the root element in XML
                Arguments.of(FileType.XML, new PersistentElement(), "<PersistentElement><name>sample</name></PersistentElement>"),
                Arguments.of(FileType.TOML, new PersistentElement(), "name='sample'"),
                Arguments.of(FileType.PROPERTIES, new PersistentElement(), "name=sample"),

                Arguments.of(FileType.JSON, new EntityElement(), "{\"name\":\"sample\"}"),
                Arguments.of(FileType.YAML, new EntityElement(), "name: sample"),
                Arguments.of(FileType.XML, new EntityElement(), "<entity><name>sample</name></entity>"),
                Arguments.of(FileType.TOML, new EntityElement(), "name='sample'"),
                Arguments.of(FileType.PROPERTIES, new EntityElement(), "name=sample"),

                Arguments.of(FileType.JSON, new NestedElement(new EntityElement()), "{\"child\":{\"name\":\"sample\"}}"),
                Arguments.of(FileType.YAML, new NestedElement(new EntityElement()), "child:\n  name: sample"),
                Arguments.of(FileType.XML, new NestedElement(new EntityElement()), "<NestedElement><child><name>sample</name></child></NestedElement>"),
                Arguments.of(FileType.TOML, new NestedElement(new EntityElement()), "child.name='sample'"),
                Arguments.of(FileType.PROPERTIES, new NestedElement(new EntityElement()), "child.name=sample"),

                Arguments.of(FileType.JSON, new MultiElement(), "{\"name\":\"sample\",\"other\":\"sample\"}"),
                Arguments.of(FileType.YAML, new MultiElement(), "name: sample\nother: sample"),
                Arguments.of(FileType.XML, new MultiElement(), "<MultiElement><name>sample</name><other>sample</other></MultiElement>"),
                Arguments.of(FileType.TOML, new MultiElement(), "name='sample'\nother='sample'"),
                Arguments.of(FileType.PROPERTIES, new MultiElement(), "name=sample\nother=sample")
        );
    }

    @ParameterizedTest
    @MethodSource("serialisationElements")
    void testObjectSerialisation(FileType fileType, Element content, String expected) {
        final ObjectMapper mapper = new JacksonObjectMapper();
        mapper.setFileType(fileType);

        content.setName("sample");
        final Exceptional<String> result = mapper.write(content);

        Assertions.assertTrue(result.present());
        Assertions.assertEquals(HartshornUtils.strip(expected), HartshornUtils.strip(result.get()));
    }

    @ParameterizedTest
    @MethodSource("serialisationElements")
    void testObjectDeserialisation(FileType fileType, Element expected, String content) {
        final ObjectMapper mapper = new JacksonObjectMapper();
        mapper.setFileType(fileType);
        expected.setName("sample");

        final Exceptional<? extends Element> result = mapper.read(content, expected.getClass());

        if (result.absent()) System.out.println(result.error().getMessage());
        Assertions.assertTrue(result.present());
        Assertions.assertEquals(expected.getClass(), result.type());
        Assertions.assertEquals(expected, result.get());
    }
}
