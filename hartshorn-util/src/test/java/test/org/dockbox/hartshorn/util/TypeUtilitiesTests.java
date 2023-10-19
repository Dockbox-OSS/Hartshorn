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

package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.TypeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class TypeUtilitiesTests {

    public static Stream<Arguments> primitiveStrings() {
        return Stream.of(
                Arguments.of(Boolean.class, "true", true),
                Arguments.of(Byte.class, "0", (byte) 0),
                Arguments.of(Character.class, "\u0000", '\u0000'),
                Arguments.of(Double.class, "1.0d", 1.0d),
                Arguments.of(Float.class, "1.0f", 1.0f),
                Arguments.of(Integer.class, "1", 1),
                Arguments.of(Long.class, "0", 0L),
                Arguments.of(Short.class, "0", (short) 0)
        );
    }

    @ParameterizedTest
    @MethodSource("primitiveStrings")
    void testPrimitivesFromString(Class<?> primitive, String value, Object real) {
        Object out = TypeUtils.toPrimitive(primitive, value);
        Assertions.assertEquals(real, out);
    }
}
