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
import org.dockbox.hartshorn.util.introspect.convert.support.OptionalToOptionConverter;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OptionalToOptionConverterTests {

    @Test
    void presentOptionalConvertsToPresentOption() {
        Converter<Optional<?>, Option<?>> converter = new OptionalToOptionConverter();
        Optional<String> option = Optional.of("test");
        Option<?> converted = converter.convert(option);
        assertThat(converted).isNotNull();
        assertThat(converted.present()).isTrue();
        assertThat(converted.get()).isEqualTo("test");
    }

    @Test
    void emptyOptionalConvertsToEmptyOption() {
        Converter<Optional<?>, Option<?>> converter = new OptionalToOptionConverter();
        Optional<String> option = Optional.empty();
        Option<?> converted = converter.convert(option);
        assertThat(converted).isNotNull();
        assertThat(converted.present()).isFalse();
    }
}