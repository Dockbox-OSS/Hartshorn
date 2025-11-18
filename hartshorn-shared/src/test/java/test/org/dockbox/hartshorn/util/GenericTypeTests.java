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

package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.types.GenericType;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class GenericTypeTests {

    @Test
    void genericTypeOfSimpleTypeIsCorrect() {
        GenericType<String> genericType = new GenericType<>() {};
        Type type = genericType.type();
        assertThat(type).isInstanceOf(Class.class);
        assertThat(type).isEqualTo(String.class);

        Option<Class<String>> classOption = genericType.asClass();
        assertThat(classOption.present()).isTrue();
        assertThat(classOption.get()).isSameAs(String.class);
    }

    @Test
    void genericTypeOfParameterizedTypeIsCorrect() {
        GenericType<List<String>> genericType = new GenericType<>() {};
        Type type = genericType.type();
        assertThat(type).isInstanceOf(ParameterizedType.class);

        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        assertThat(typeArguments.length).isOne();
        assertThat(typeArguments[0]).isEqualTo(String.class);

        Option<Class<List<String>>> classOption = genericType.asClass();
        // ParameterizedType should not yield a class
        assertThat(classOption.absent()).isTrue();
    }

    @Test
    void wildcardTypeYieldsObject() {
        GenericType<?> genericType = new GenericType<>() {};
        Type type = genericType.type();
        assertThat(type).isInstanceOf(Class.class);
        assertThat(type).isEqualTo(Object.class);

        Option<? extends Class<?>> classOption = genericType.asClass();
        assertThat(classOption.present()).isTrue();
        assertThat(classOption.get()).isEqualTo(Object.class);
    }

    @Test
    void rawGenericTypeFails() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new GenericType() {
        });
    }
}
