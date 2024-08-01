/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.config.mapping;

import java.util.stream.Stream;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.FileFormats;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.config.ObjectMappingException;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import test.org.dockbox.hartshorn.config.Element;
import test.org.dockbox.hartshorn.config.EntityElement;
import test.org.dockbox.hartshorn.config.MultiElement;
import test.org.dockbox.hartshorn.config.NestedElement;
import test.org.dockbox.hartshorn.config.PersistentElement;

@HartshornTest(includeBasePackages = false)
@UseConfigurations
public abstract class ObjectMappingTests {

    protected abstract ObjectMapper objectMapper();

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
    void testObjectSerialization(FileFormat fileFormat, Element content, String expected) throws ObjectMappingException {
        ObjectMapper mapper = this.objectMapper();
        mapper.fileType(fileFormat);

        content.name("sample");
        String result = mapper.write(content);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(StringUtilities.strip(expected), StringUtilities.strip(result));
    }

    @ParameterizedTest
    @MethodSource("serializationElements")
    void testObjectDeserialization(FileFormat fileFormat, Element expected, String content) throws ObjectMappingException {
        ObjectMapper mapper = this.objectMapper();
        mapper.fileType(fileFormat);
        expected.name("sample");

        Option<? extends Element> result = mapper.read(content, expected.getClass());

        Assertions.assertTrue(result.present());
        Assertions.assertSame(expected.getClass(), result.get().getClass());
        Assertions.assertEquals(expected, result.get());
    }
}
