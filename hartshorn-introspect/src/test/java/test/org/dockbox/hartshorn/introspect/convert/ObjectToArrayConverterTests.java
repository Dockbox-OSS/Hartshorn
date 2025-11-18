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

package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToArrayConverter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectToArrayConverterTests {

    @Test
    void nonNullElementCanBeConverted() {
        String element = "test";
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, Object.class, Object[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof Object[])).isTrue();
        assertThat(((Object[]) converted).length).isOne();
        assertThat(((Object[]) converted)[0]).isEqualTo(element);
    }

    @Test
    void elementTypeIsRetained() {
        String element = "test";
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, String.class, String[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof String[])).isTrue();
        assertThat(((String[]) converted).length).isOne();
        assertThat(((String[]) converted)[0]).isEqualTo(element);
    }

    @Test
    void nullElementCanBeConverted() {
        Object element = null;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, Object.class, Object[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof Object[])).isTrue();
        assertThat(((Object[]) converted).length).isOne();
        assertThat(((Object[]) converted)[0]).isNull();
    }

    @Test
    void primitiveIntCanBeConverted() {
        int element = 1;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, int.class, int[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof int[])).isTrue();
        assertThat(((int[]) converted).length).isOne();
        assertThat(((int[]) converted)[0]).isEqualTo(element);
    }

    @Test
    void primitiveLongCanBeConverted() {
        long element = 1L;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, long.class, long[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof long[])).isTrue();
        assertThat(((long[]) converted).length).isOne();
        assertThat(((long[]) converted)[0]).isEqualTo(element);
    }

    @Test
    void primitiveShortCanBeConverted() {
        short element = 1;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, short.class, short[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof short[])).isTrue();
        assertThat(((short[]) converted).length).isOne();
        assertThat(((short[]) converted)[0]).isEqualTo(element);
    }

    @Test
    void primitiveByteCanBeConverted() {
        byte element = 1;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, byte.class, byte[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof byte[])).isTrue();
        assertThat(((byte[]) converted).length).isOne();
        assertThat(((byte[]) converted)[0]).isEqualTo(element);
    }

    @Test
    void primitiveFloatCanBeConverted() {
        float element = 1.0F;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, float.class, float[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof float[])).isTrue();
        assertThat(((float[]) converted).length).isOne();
        assertThat(((float[]) converted)[0]).isEqualTo(element);
    }

    @Test
    void primitiveDoubleCanBeConverted() {
        double element = 1.0D;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, double.class, double[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof double[])).isTrue();
        assertThat(((double[]) converted).length).isOne();
        assertThat(((double[]) converted)[0]).isEqualTo(element);
    }

    @Test
    void primitiveCharCanBeConverted() {
        char element = 'a';
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, char.class, char[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof char[])).isTrue();
        assertThat(((char[]) converted).length).isOne();
        assertThat(((char[]) converted)[0]).isEqualTo(element);
    }

    @Test
    void primitiveBooleanCanBeConverted() {
        boolean element = true;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, boolean.class, boolean[].class);
        assertThat(converted).isNotNull();
        assertThat((converted instanceof boolean[])).isTrue();
        assertThat(((boolean[]) converted).length).isOne();
        assertThat(((boolean[]) converted)[0]).isEqualTo(element);
    }
}
