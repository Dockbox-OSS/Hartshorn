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

import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.Null;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class ConvertibleTypePairTests {

    @Test
    void nullTargetTypeIsRejected() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> ConvertibleTypePair.of(String.class, null));
    }

    @Test
    void nonNullTypesAreAccepted() {
        assertThatCode(() -> ConvertibleTypePair.of(String.class, String.class)).doesNotThrowAnyException();
    }

    @Test
    void nullSourceTypeIsNullableType() {
        // Separate test, as annotations cannot supply null values
        this.sourceTypeIsNullableType(null);
    }

    @ParameterizedTest
    @ValueSource(classes = {void.class, Void.class})
    void sourceTypeIsNullableType(Class<?> type) {
        ConvertibleTypePair typePair = ConvertibleTypePair.of(type, String.class);
        assertThat(typePair.sourceType()).isSameAs(Null.TYPE);
    }
}
