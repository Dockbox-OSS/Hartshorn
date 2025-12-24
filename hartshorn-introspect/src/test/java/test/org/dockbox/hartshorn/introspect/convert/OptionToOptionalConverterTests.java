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

import org.dockbox.hartshorn.util.introspect.convert.support.OptionToOptionalConverter;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OptionToOptionalConverterTests {

    @Test
    void presentOptionConvertsToPresentOptional() {
        OptionToOptionalConverter converter = new OptionToOptionalConverter();
        Option<String> option = Option.of("test");
        Optional<String> optional = (Optional<String>) converter.convert(option);
        assertThat(optional).hasValue("test");
    }

    @Test
    void emptyOptionConvertsToEmptyOptional() {
        OptionToOptionalConverter converter = new OptionToOptionalConverter();
        Option<String> option = Option.empty();
        Optional<?> optional = converter.convert(option);
        assertThat(optional).isEmpty();
    }
}
