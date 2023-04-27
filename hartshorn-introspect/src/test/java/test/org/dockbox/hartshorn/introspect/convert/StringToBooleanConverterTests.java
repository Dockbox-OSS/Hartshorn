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

import org.dockbox.hartshorn.util.introspect.convert.support.StringToBooleanConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringToBooleanConverterTests {
    // TODO: Implement tests for StringToBooleanConverter


    @Test
    void testTrueCanConvert() {
        final StringToBooleanConverter converter = new StringToBooleanConverter();
        final Boolean converted = converter.convert("true");
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted);
    }

    @Test
    void testFalseCanConvert() {
        final StringToBooleanConverter converter = new StringToBooleanConverter();
        final Boolean converted = converter.convert("false");
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted);
    }

    @Test
    void testNonDefinedValuesConvertToFalse() {
        final StringToBooleanConverter converter = new StringToBooleanConverter();
        final Boolean converted = converter.convert("test");
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted);
    }
}
