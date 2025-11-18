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

import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.PrimitiveWrapperConverter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveWrapperConverterTests {

    @Test
    void intToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert(1, int.class, Integer.class);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Integer.class);
        assertThat(converted).isEqualTo(1);
    }

    @Test
    void longToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert(1L, long.class, Long.class);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Long.class);
        assertThat(converted).isEqualTo(1L);
    }

    @Test
    void doubleToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert(1.0D, double.class, Double.class);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Double.class);
        assertThat(converted).isEqualTo(1.0D);
    }

    @Test
    void floatToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert(1.0F, float.class, Float.class);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Float.class);
        assertThat(converted).isEqualTo(1.0F);
    }

    @Test
    void shortToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert((short) 1, short.class, Short.class);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Short.class);
        assertThat(converted).isEqualTo((short) 1);
    }

    @Test
    void byteToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert((byte) 1, byte.class, Byte.class);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Byte.class);
        assertThat(converted).isEqualTo((byte) 1);
    }

    @Test
    void booleanToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert(true, boolean.class, Boolean.class);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Boolean.class);
        assertThat(converted).isEqualTo(true);
    }

    @Test
    void charToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert('a', char.class, Character.class);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Character.class);
        assertThat(converted).isEqualTo('a');
    }

    @Test
    void wrapperToIntCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        assertThat(converter.canConvert(Integer.valueOf(1), int.class)).isTrue();

        int converted = (int) converter.convert(Integer.valueOf(1), Integer.class, int.class);
        assertThat(converted).isOne();
    }

    @Test
    void wrapperToLongCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        assertThat(converter.canConvert(Long.valueOf(1L), long.class)).isTrue();

        long converted = (long) converter.convert(Long.valueOf(1L), Long.class, long.class);
        assertThat(converted).isOne();
    }

    @Test
    void wrapperToDoubleCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        assertThat(converter.canConvert(Double.valueOf(1.0D), double.class)).isTrue();

        double converted = (double) converter.convert(Double.valueOf(1.0D), Double.class, double.class);
        assertThat(converted).isOne();
    }

    @Test
    void wrapperToFloatCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        assertThat(converter.canConvert(Float.valueOf(1.0F), float.class)).isTrue();

        float converted = (float) converter.convert(Float.valueOf(1.0F), Float.class, float.class);
        assertThat(converted).isOne();
    }

    @Test
    void wrapperToShortCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        assertThat(converter.canConvert(Short.valueOf((short) 1), short.class)).isTrue();

        short converted = (short) converter.convert(Short.valueOf((short) 1), Short.class, short.class);
        assertThat(converted).isOne();
    }

    @Test
    void wrapperToByteCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        assertThat(converter.canConvert(Byte.valueOf((byte) 1), byte.class)).isTrue();

        byte converted = (byte) converter.convert(Byte.valueOf((byte) 1), Byte.class, byte.class);
        assertThat(converted).isOne();
    }

    @Test
    void wrapperToBooleanCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        assertThat(converter.canConvert(Boolean.TRUE, boolean.class)).isTrue();

        boolean converted = (boolean) converter.convert(Boolean.TRUE, Boolean.class, boolean.class);
        assertThat(converted).isTrue();
    }

    @Test
    void wrapperToCharCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        assertThat(converter.canConvert(Character.valueOf('a'), char.class)).isTrue();

        char converted = (char) converter.convert(Character.valueOf('a'), Character.class, char.class);
        assertThat(converted).isEqualTo('a');
    }
}
