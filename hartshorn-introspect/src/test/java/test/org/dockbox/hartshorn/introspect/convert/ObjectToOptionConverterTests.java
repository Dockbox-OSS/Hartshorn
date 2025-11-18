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
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToOptionConverter;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("rawtypes")
class ObjectToOptionConverterTests {

    @Test
    void nonNullElementConvertsToPresentOption() {
        String element = "test";
        Converter<Object, Option<?>> converter = new ObjectToOptionConverter();
        Object converted = converter.convert(element);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Option.class);
        assertThat(((Option) converted).present()).isTrue();
        assertThat(((Option) converted).get()).isEqualTo(element);
    }

    @Test
    void nullElementConvertsToEmptyOption() {
        Object element = null;
        Converter<Object, Option<?>> converter = new ObjectToOptionConverter();
        Object converted = converter.convert(element);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Option.class);
        assertThat(((Option) converted).present()).isFalse();
    }
}