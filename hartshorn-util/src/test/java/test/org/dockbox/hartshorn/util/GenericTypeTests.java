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

import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class GenericTypeTests {

    @Test
    void testGenericTypeOfSimpleTypeIsCorrect() {
        final GenericType<String> genericType = new GenericType<>() {};
        final Type type = genericType.type();
        Assertions.assertTrue(type instanceof Class<?>);
        Assertions.assertEquals(String.class, type);

        final Option<Class<String>> classOption = genericType.asClass();
        Assertions.assertTrue(classOption.present());
        Assertions.assertSame(String.class, classOption.get());
    }

    @Test
    void testGenericTypeOfParameterizedTypeIsCorrect() {
        final GenericType<List<String>> genericType = new GenericType<>() {};
        final Type type = genericType.type();
        Assertions.assertTrue(type instanceof ParameterizedType);

        final ParameterizedType parameterizedType = (ParameterizedType) type;
        final Type[] typeArguments = parameterizedType.getActualTypeArguments();
        Assertions.assertEquals(1, typeArguments.length);
        Assertions.assertEquals(String.class, typeArguments[0]);

        final Option<Class<List<String>>> classOption = genericType.asClass();
        // ParameterizedType should not yield a class
        Assertions.assertTrue(classOption.absent());
    }

    @Test
    void testWildcardTypeYieldsObject() {
        final GenericType<?> genericType = new GenericType<>() {};
        final Type type = genericType.type();
        Assertions.assertTrue(type instanceof Class<?>);
        Assertions.assertEquals(Object.class, type);

        final Option<? extends Class<?>> classOption = genericType.asClass();
        Assertions.assertTrue(classOption.present());
        Assertions.assertEquals(Object.class, classOption.get());
    }
}
