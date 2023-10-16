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

import org.dockbox.hartshorn.util.introspect.convert.support.StringToCharacterConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringToCharacterConverterTests {

    @Test
    void testCanConvertLengthOne() {
        StringToCharacterConverter converter = new StringToCharacterConverter();
        Character character = converter.convert("a");
        Assertions.assertNotNull(character);
        Assertions.assertEquals('a', character);
    }

    @Test
    void testCanNotConvertLengthZero() {
        StringToCharacterConverter converter = new StringToCharacterConverter();
        Character character = converter.convert("");
        Assertions.assertNull(character);
    }

    @Test
    void testNullConvertsToNull() {
        StringToCharacterConverter converter = new StringToCharacterConverter();
        Character character = converter.convert(null);
        Assertions.assertNull(character);
    }
}
