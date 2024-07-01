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

package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToStringConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectToStringConverterTests {

    @Test
    void testNullCanBeConverted() {
        Object element = null;
        Converter<Object, String> converter = new ObjectToStringConverter();
        Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof String);
        Assertions.assertEquals("null", converted);
    }

    @Test
    void testStringElementCanBeConvertedAndNotEscaped() {
        String element = "test";
        Converter<Object, String> converter = new ObjectToStringConverter();
        Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof String);
        Assertions.assertEquals(element, converted);
    }

    @Test
    void testNonNullElementCanBeConverted() {
        Object element = new Object();
        Converter<Object, String> converter = new ObjectToStringConverter();
        Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof String);
        Assertions.assertEquals(element.toString(), converted);
    }

    @Test
    void testToStringIsUsedForObjects() {
        Object element = new TestClass("test");
        Converter<Object, String> converter = new ObjectToStringConverter();
        Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof String);
        Assertions.assertEquals("{toStringResult:test}", converted);
    }

    private record TestClass(String test) {

        @Override
        public String toString() {
            return "{toStringResult:" + this.test + "}";
        }
    }
}
