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

package test.org.dockbox.hartshorn.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptionTests {

    @Test
    void testOfNonNull() {
        Option<String> option = Option.of("test");
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testOfNull() {
        Option<String> option = Option.of((String) null);
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testOfOptional() {
        Option<String> option = Option.of(Optional.of("test"));
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testOfOptionalEmpty() {
        Option<String> option = Option.of(Optional.empty());
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testOfSupplier() {
        Option<String> option = Option.of(() -> "test");
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testOfSupplierNull() {
        Option<String> option = Option.of(() -> null);
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testEmpty() {
        Option<String> option = Option.empty();
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testPeekWhenPresent() {
        Option<String> option = Option.of("test");
        AtomicBoolean called = new AtomicBoolean(false);
        option.peek(value -> {
            Assertions.assertEquals("test", value);
            called.set(true);
        });
        Assertions.assertTrue(called.get());
    }

    @Test
    void testPeekWhenAbsent() {
        Option<String> option = Option.empty();
        AtomicBoolean called = new AtomicBoolean(false);
        option.peek(value -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testOnEmptyWhenPresent() {
        Option<String> option = Option.of("test");
        AtomicBoolean called = new AtomicBoolean(false);
        option.onEmpty(() -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testOnEmptyWhenAbsent() {
        Option<String> option = Option.empty();
        AtomicBoolean called = new AtomicBoolean(false);
        option.onEmpty(() -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOrNullWhenPresent() {
        Option<String> option = Option.of("test");
        Assertions.assertEquals("test", option.orNull());
    }

    @Test
    void testOrNullWhenAbsent() {
        Option<String> option = Option.empty();
        Assertions.assertNull(option.orNull());
    }

    @Test
    void testOrElseWhenPresent() {
        Option<String> option = Option.of("test");
        Assertions.assertEquals("test", option.orElse("test2"));
    }

    @Test
    void testOrElseWhenAbsent() {
        Option<String> option = Option.empty();
        Assertions.assertEquals("test2", option.orElse("test2"));
    }

    @Test
    void testOrElseGetWhenPresent() {
        Option<String> option = Option.of("test");
        Assertions.assertEquals("test", option.orElseGet(() -> "test2"));
    }

    @Test
    void testOrElseGetWhenAbsent() {
        Option<String> option = Option.empty();
        Assertions.assertEquals("test2", option.orElseGet(() -> "test2"));
    }

    @Test
    void testOrElseThrowWhenPresent() {
        Option<String> option = Option.of("test");
        Assertions.assertEquals("test", option.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    void testOrElseThrowWhenAbsent() {
        Option<String> option = Option.empty();
        Assertions.assertThrows(IllegalArgumentException.class, () -> option.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    void testOrComputeWhenPresent() {
        Option<String> option = Option.of("test");
        Assertions.assertEquals("test", option.orCompute(() -> "test2").get());
    }

    @Test
    void testOrComputeWhenAbsent() {
        Option<String> option = Option.empty();
        Assertions.assertEquals("test2", option.orCompute(() -> "test2").get());
    }

    @Test
    void testToOptionalWhenPresent() {
        Option<String> option = Option.of("test");
        Optional<String> optional = option.optional();
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testToOptionalWhenAbsent() {
        Option<String> option = Option.empty();
        Optional<String> optional = option.optional();
        Assertions.assertFalse(optional.isPresent());
    }

    @Test
    void testToStreamWhenPresent() {
        Option<String> option = Option.of("test");
        Assertions.assertEquals(1, option.stream().count());
        Optional<String> optional = option.stream().findFirst();
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testToStreamWhenAbsent() {
        Option<String> option = Option.empty();
        Stream<String> stream = option.stream();
        Assertions.assertEquals(0, stream.count());
    }

    @Test
    void testMapWhenPresent() {
        Option<String> option = Option.of("test");
        Option<Integer> mapped = option.map(String::length);
        Assertions.assertTrue(mapped.present());
        Assertions.assertEquals(4, mapped.get());
    }

    @Test
    void testMapWhenAbsent() {
        Option<String> option = Option.empty();
        Option<Integer> mapped = option.map(String::length);
        Assertions.assertTrue(mapped.absent());
    }

    @Test
    void testFlatMapWhenPresent() {
        Option<String> option = Option.of("test");
        Option<Integer> mapped = option.flatMap(value -> Option.of(value.length()));
        Assertions.assertTrue(mapped.present());
        Assertions.assertEquals(4, mapped.get());
    }

    @Test
    void testFlatMapWhenAbsent() {
        Option<String> option = Option.empty();
        Option<Integer> mapped = option.flatMap(value -> Option.of(value.length()));
        Assertions.assertTrue(mapped.absent());
    }

    @Test
    void testFilterWhenPresentAndMatches() {
        Option<String> option = Option.of("test");
        Option<String> filtered = option.filter(value -> value.length() == 4);
        Assertions.assertTrue(filtered.present());
        Assertions.assertEquals("test", filtered.get());
    }

    @Test
    void testFilterWhenPresentAndDoesNotMatch() {
        Option<String> option = Option.of("test");
        Option<String> filtered = option.filter(value -> value.length() == 5);
        Assertions.assertTrue(filtered.absent());
    }

    @Test
    void testFilterWhenAbsent() {
        Option<String> option = Option.empty();
        Option<String> filtered = option.filter(value -> {
            Assertions.fail("Should not be called");
            return true;
        });
        Assertions.assertTrue(filtered.absent());
    }

    @Test
    void testContainsWhenPresentAndMatches() {
        Option<String> option = Option.of("test");
        Assertions.assertTrue(option.contains("test"));
    }

    @Test
    void testContainsWhenPresentAndDoesNotMatch() {
        Option<String> option = Option.of("test");
        Assertions.assertFalse(option.contains("test2"));
    }

    @Test
    void testContainsWhenAbsent() {
        Option<String> option = Option.empty();
        Assertions.assertFalse(option.contains("test"));
    }
}
