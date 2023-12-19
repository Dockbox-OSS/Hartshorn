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

import java.util.Optional;

import org.dockbox.hartshorn.util.introspect.convert.support.OptionToOptionalConverter;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptionToOptionalConverterTests {

    @Test
    void testPresentOptionConvertsToPresentOptional() {
        OptionToOptionalConverter converter = new OptionToOptionalConverter();
        Option<String> option = Option.of("test");
        Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testEmptyOptionConvertsToEmptyOptional() {
        OptionToOptionalConverter converter = new OptionToOptionalConverter();
        Option<String> option = Option.empty();
        Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertFalse(optional.isPresent());
    }
}
