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

import org.dockbox.hartshorn.util.introspect.convert.support.OptionToOptionalConverter;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class OptionToOptionalConverterTests {
    @Test
    void testPresentOptionConvertsToPresentOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Option.of("test");
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testEmptyOptionConvertsToEmptyOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Option.empty();
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertFalse(optional.isPresent());
    }

    @Test
    void testSuccessPresentAttemptConvertsToPresentOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Attempt.of("test");
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testFailurePresentAttemptConvertsToPresentOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Attempt.of("test", new Exception());
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testFailureAbsentAttemptConvertsToEmptyOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Attempt.of(new Exception());
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertFalse(optional.isPresent());
    }

    @Test
    void testSuccessAbsentAttemptConvertsToEmptyOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Attempt.of((String) null);
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertFalse(optional.isPresent());
    }
}
