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

import org.dockbox.hartshorn.util.Tuple;
import org.junit.jupiter.api.Test;

import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class TupleTests {

    @Test
    void firstValueIsStored() {
        Entry<?, ?> tuple = Tuple.of(1, "two");
        assertThat(tuple.getKey()).isEqualTo(1);
        assertThat(tuple.getKey()).isInstanceOf(Integer.class);
    }

    @Test
    void secondValueIsStored() {
        Entry<?, ?> tuple = Tuple.of(1, "two");
        assertThat(tuple.getValue()).isEqualTo("two");
        assertThat(tuple.getValue()).isInstanceOf(String.class);
    }

    @Test
    void equalsUsesValues() {
        Entry<?, ?> tuple = Tuple.of(1, "two");
        Entry<?, ?> second = Tuple.of(1, "two");
        assertThat(second)
                .isNotSameAs(tuple)
                .isEqualTo(tuple);
    }

    @Test
    void tupleIsImmutable() {
        Entry<Integer, String> tuple = Tuple.of(1, "two");
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> tuple.setValue("three"));
    }
}
