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

import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToVoidConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectToVoidConverterTests {

    @Test
    void testCanConvertPrimitive() {
        final ConditionalConverter converter = new ObjectToVoidConverter();
        Assertions.assertTrue(converter.canConvert(null, void.class));
    }

    @Test
    void testCanConvertPrimitiveWrapper() {
        final ConditionalConverter converter = new ObjectToVoidConverter();
        Assertions.assertTrue(converter.canConvert(null, Void.class));
    }

    @Test
    void testCanConvertPrimitiveType() {
        final ConditionalConverter converter = new ObjectToVoidConverter();
        Assertions.assertTrue(converter.canConvert(null, Void.TYPE));
    }

    @Test
    void testVoidIsAlwaysNull() {
        final GenericConverter converter = new ObjectToVoidConverter();

        Assertions.assertNull(converter.convert(null, Object.class, void.class));
        Assertions.assertNull(converter.convert("test", Object.class, void.class));

        Assertions.assertNull(converter.convert(null, Object.class, Void.class));
        Assertions.assertNull(converter.convert("test", Object.class, Void.class));

        Assertions.assertNull(converter.convert(null, Object.class, Void.TYPE));
        Assertions.assertNull(converter.convert("test", Object.class, Void.TYPE));
    }
}