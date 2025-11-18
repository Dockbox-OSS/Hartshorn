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

import org.dockbox.hartshorn.util.introspect.convert.support.StringToUUIDConverter;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StringToUUIDConverterTests {

    @Test
    void validUUIDFormatCanConvert() {
        StringToUUIDConverter converter = new StringToUUIDConverter();
        UUID uuid = converter.convert("123e4567-e89b-12d3-a456-426655440000");
        assertThat(uuid)
                .hasToString("123e4567-e89b-12d3-a456-426655440000");
    }

    @Test
    void shortUUIDCanConvert() {
        StringToUUIDConverter converter = new StringToUUIDConverter();
        UUID uuid = converter.convert("1-2-3-4-5");
        assertThat(uuid)
                .hasToString("00000001-0002-0003-0004-000000000005");
    }

    @Test
    void invalidUUIDFormatCannotConvert() {
        StringToUUIDConverter converter = new StringToUUIDConverter();
        UUID uuid = converter.convert("-123e4567-e89b-12d3-a456-426655440000");
        assertThat(uuid).isNull();
    }

    @Test
    void nullIsNotParsed() {
        StringToUUIDConverter converter = new StringToUUIDConverter();
        UUID uuid = converter.convert(null);
        assertThat(uuid).isNull();
    }

}
