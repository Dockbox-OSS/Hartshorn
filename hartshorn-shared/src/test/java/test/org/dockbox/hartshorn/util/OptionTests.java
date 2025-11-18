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

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.dockbox.hartshorn.util.option.Option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

class OptionTests {

    @Test
    void ofNonNull() {
        Option<String> option = Option.of("test");
        assertThat(option.present()).isTrue();
        assertThat(option.absent()).isFalse();
        assertThat(option.get()).isEqualTo("test");
    }

    @Test
    void ofNull() {
        Option<String> option = Option.of((String) null);
        assertThat(option.present()).isFalse();
        assertThat(option.absent()).isTrue();
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(option::get);
    }

    @Test
    void ofOptional() {
        Option<String> option = Option.of(Optional.of("test"));
        assertThat(option.present()).isTrue();
        assertThat(option.absent()).isFalse();
        assertThat(option.get()).isEqualTo("test");
    }

    @Test
    void ofOptionalEmpty() {
        Option<String> option = Option.of(Optional.empty());
        assertThat(option.present()).isFalse();
        assertThat(option.absent()).isTrue();
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(option::get);
    }

    @Test
    void ofSupplier() {
        Option<String> option = Option.of(() -> "test");
        assertThat(option.present()).isTrue();
        assertThat(option.absent()).isFalse();
        assertThat(option.get()).isEqualTo("test");
    }

    @Test
    void ofSupplierNull() {
        Option<String> option = Option.of(() -> null);
        assertThat(option.present()).isFalse();
        assertThat(option.absent()).isTrue();
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(option::get);
    }

    @Test
    void empty() {
        Option<String> option = Option.empty();
        assertThat(option.present()).isFalse();
        assertThat(option.absent()).isTrue();
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(option::get);
    }

    @Test
    void peekWhenPresent() {
        Option<String> option = Option.of("test");
        AtomicBoolean called = new AtomicBoolean(false);
        option.peek(value -> {
            assertThat(value).isEqualTo("test");
            called.set(true);
        });
        assertThat(called.get()).isTrue();
    }

    @Test
    void peekWhenAbsent() {
        Option<String> option = Option.empty();
        AtomicBoolean called = new AtomicBoolean(false);
        option.peek(value -> {
            fail("Should not be called");
            called.set(true);
        });
        assertThat(called.get()).isFalse();
    }

    @Test
    void onEmptyWhenPresent() {
        Option<String> option = Option.of("test");
        AtomicBoolean called = new AtomicBoolean(false);
        option.onEmpty(() -> {
            fail("Should not be called");
            called.set(true);
        });
        assertThat(called.get()).isFalse();
    }

    @Test
    void onEmptyWhenAbsent() {
        Option<String> option = Option.empty();
        AtomicBoolean called = new AtomicBoolean(false);
        option.onEmpty(() -> called.set(true));
        assertThat(called.get()).isTrue();
    }

    @Test
    void orNullWhenPresent() {
        Option<String> option = Option.of("test");
        assertThat(option.orNull()).isEqualTo("test");
    }

    @Test
    void orNullWhenAbsent() {
        Option<String> option = Option.empty();
        assertThat(option.orNull()).isNull();
    }

    @Test
    void orElseWhenPresent() {
        Option<String> option = Option.of("test");
        assertThat(option.orElse("test2")).isEqualTo("test");
    }

    @Test
    void orElseWhenAbsent() {
        Option<String> option = Option.empty();
        assertThat(option.orElse("test2")).isEqualTo("test2");
    }

    @Test
    void orElseGetWhenPresent() {
        Option<String> option = Option.of("test");
        assertThat(option.orElseGet(() -> "test2")).isEqualTo("test");
    }

    @Test
    void orElseGetWhenAbsent() {
        Option<String> option = Option.empty();
        assertThat(option.orElseGet(() -> "test2")).isEqualTo("test2");
    }

    @Test
    void orElseThrowWhenPresent() {
        Option<String> option = Option.of("test");
        assertThat(option.orElseThrow(IllegalArgumentException::new)).isEqualTo("test");
    }

    @Test
    void orElseThrowWhenAbsent() {
        Option<String> option = Option.empty();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> option.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    void orComputeWhenPresent() {
        Option<String> option = Option.of("test");
        assertThat(option.orCompute(() -> "test2").get()).isEqualTo("test");
    }

    @Test
    void orComputeWhenAbsent() {
        Option<String> option = Option.empty();
        assertThat(option.orCompute(() -> "test2").get()).isEqualTo("test2");
    }

    @Test
    void toOptionalWhenPresent() {
        Option<String> option = Option.of("test");
        Optional<String> optional = option.optional();
        assertThat(optional)
                .isPresent()
                .hasValue("test");
    }

    @Test
    void toOptionalWhenAbsent() {
        Option<String> option = Option.empty();
        Optional<String> optional = option.optional();
        assertThat(optional).isEmpty();
    }

    @Test
    void toStreamWhenPresent() {
        Option<String> option = Option.of("test");
        assertThat(option.stream().count()).isOne();
        Optional<String> optional = option.stream().findFirst();
        assertThat(optional)
                .isPresent()
                .hasValue("test");
    }

    @Test
    void toStreamWhenAbsent() {
        Option<String> option = Option.empty();
        Stream<String> stream = option.stream();
        assertThat(stream.count()).isZero();
    }

    @Test
    void mapWhenPresent() {
        Option<String> option = Option.of("test");
        Option<Integer> mapped = option.map(String::length);
        assertThat(mapped.present()).isTrue();
        assertThat(mapped.get()).isEqualTo(4);
    }

    @Test
    void mapWhenAbsent() {
        Option<String> option = Option.empty();
        Option<Integer> mapped = option.map(String::length);
        assertThat(mapped.absent()).isTrue();
    }

    @Test
    void flatMapWhenPresent() {
        Option<String> option = Option.of("test");
        Option<Integer> mapped = option.flatMap(value -> Option.of(value.length()));
        assertThat(mapped.present()).isTrue();
        assertThat(mapped.get()).isEqualTo(4);
    }

    @Test
    void flatMapWhenAbsent() {
        Option<String> option = Option.empty();
        Option<Integer> mapped = option.flatMap(value -> Option.of(value.length()));
        assertThat(mapped.absent()).isTrue();
    }

    @Test
    void filterWhenPresentAndMatches() {
        Option<String> option = Option.of("test");
        Option<String> filtered = option.filter(value -> value.length() == 4);
        assertThat(filtered.present()).isTrue();
        assertThat(filtered.get()).isEqualTo("test");
    }

    @Test
    void filterWhenPresentAndDoesNotMatch() {
        Option<String> option = Option.of("test");
        Option<String> filtered = option.filter(value -> value.length() == 5);
        assertThat(filtered.absent()).isTrue();
    }

    @Test
    void filterWhenAbsent() {
        Option<String> option = Option.empty();
        Option<String> filtered = option.filter(value -> {
            fail("Should not be called");
            return true;
        });
        assertThat(filtered.absent()).isTrue();
    }

    @Test
    void containsWhenPresentAndMatches() {
        Option<String> option = Option.of("test");
        assertThat(option.contains("test")).isTrue();
    }

    @Test
    void containsWhenPresentAndDoesNotMatch() {
        Option<String> option = Option.of("test");
        assertThat(option.contains("test2")).isFalse();
    }

    @Test
    void containsWhenAbsent() {
        Option<String> option = Option.empty();
        assertThat(option.contains("test")).isFalse();
    }
}
