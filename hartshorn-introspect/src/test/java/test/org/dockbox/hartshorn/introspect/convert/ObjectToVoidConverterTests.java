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

import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToVoidConverter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectToVoidConverterTests {

    @Test
    void canConvertPrimitive() {
        ConditionalConverter converter = new ObjectToVoidConverter();
        assertThat(converter.canConvert(null, void.class)).isTrue();
    }

    @Test
    void canConvertPrimitiveWrapper() {
        ConditionalConverter converter = new ObjectToVoidConverter();
        assertThat(converter.canConvert(null, Void.class)).isTrue();
    }

    @Test
    void canConvertPrimitiveType() {
        ConditionalConverter converter = new ObjectToVoidConverter();
        assertThat(converter.canConvert(null, Void.TYPE)).isTrue();
    }

    @Test
    void voidIsAlwaysNull() {
        GenericConverter converter = new ObjectToVoidConverter();

        assertThat(converter.convert(null, Object.class, void.class)).isNull();
        assertThat(converter.convert("test", Object.class, void.class)).isNull();

        assertThat(converter.convert(null, Object.class, Void.class)).isNull();
        assertThat(converter.convert("test", Object.class, Void.class)).isNull();

        assertThat(converter.convert(null, Object.class, Void.TYPE)).isNull();
        assertThat(converter.convert("test", Object.class, Void.TYPE)).isNull();
    }
}