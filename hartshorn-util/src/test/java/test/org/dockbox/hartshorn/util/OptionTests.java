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

import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

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
    void testCallable() {
        Attempt<String, Exception> attempt = Attempt.<String>of(() -> "test");
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testCallableNull() {
        Attempt<String, Exception> attempt = Attempt.<String>of(() -> null);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testCallableException() {
        Attempt<String, Exception> attempt = Attempt.<String>of(() -> {
            throw new Exception();
        });
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertTrue(attempt.errorPresent());
        Assertions.assertFalse(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptSupplier() {
        Attempt<String, NullPointerException> attempt = Attempt.of(() -> "test", NullPointerException.class);
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testAttemptSupplierNull() {
        Attempt<String, NullPointerException> attempt = Attempt.of(() -> null, NullPointerException.class);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptSupplierException() {
        Attempt<String, NullPointerException> attempt = Attempt.of(() -> {
            throw new NullPointerException();
        }, NullPointerException.class);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertTrue(attempt.errorPresent());
        Assertions.assertFalse(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptSupplierExceptionOther() {
        IllegalStateException illegalStateException = new IllegalStateException();
        Assertions.assertThrows(IllegalArgumentException.class, () -> Attempt.of(() -> {
            throw illegalStateException;
        }, NullPointerException.class));
    }

    @Test
    void testAttemptEmpty() {
        Attempt<String, Exception> attempt = Attempt.empty();
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfNonNull() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testAttemptOfNull() {
        Attempt<String, Exception> attempt = Attempt.of((String) null);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfOptional() {
        Attempt<String, Exception> attempt = Attempt.of(Optional.of("test"));
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testAttemptOfOptionalEmpty() {
        Attempt<String, Exception> attempt = Attempt.of(Optional.empty());
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfNonNullException() {
        Attempt<String, IllegalArgumentException> attempt = Attempt.of(new IllegalArgumentException());
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertTrue(attempt.errorPresent());
        Assertions.assertFalse(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfNullException() {
        Attempt<String, IllegalArgumentException> attempt = Attempt.of((IllegalArgumentException) null);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfNonNullAndNonNullException() {
        Attempt<String, IllegalArgumentException> attempt = Attempt.of("test", new IllegalArgumentException());
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertTrue(attempt.errorPresent());
        Assertions.assertFalse(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testAttemptOfNonNullAndNullException() {
        Attempt<String, IllegalArgumentException> attempt = Attempt.of("test", null);
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testAttemptOfNullAndNonNullException() {
        Attempt<String, IllegalArgumentException> attempt = Attempt.of(null, new IllegalArgumentException());
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertTrue(attempt.errorPresent());
        Assertions.assertFalse(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfNullAndNullException() {
        Attempt<String, IllegalArgumentException> attempt = Attempt.of(null, (IllegalArgumentException) null);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
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

    @Test
    void testAttemptOptionToOptionWhenPresent() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Option<String> option = attempt.option();
        Assertions.assertTrue(option.present());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testAttemptOptionToOptionWhenAbsent() {
        Attempt<String, Exception> attempt = Attempt.empty();
        Option<String> option = attempt.option();
        Assertions.assertTrue(option.absent());
    }

    @Test
    void testAttemptOptionToOptionWhenFailed() {
        Attempt<String, Exception> attempt = Attempt.of(new Exception());
        Option<String> option = attempt.option();
        Assertions.assertTrue(option.absent());
    }

    @Test
    void testAttemptOptionToErrorOptionWhenPresent() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Option<Exception> errorOption = attempt.errorOption();
        Assertions.assertTrue(errorOption.absent());
    }

    @Test
    void testAttemptOptionToErrorOptionWhenAbsent() {
        Attempt<String, Exception> attempt = Attempt.empty();
        Option<Exception> errorOption = attempt.errorOption();
        Assertions.assertTrue(errorOption.absent());
    }

    @Test
    void testAttemptOptionToErrorOptionWhenFailed() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Option<Exception> errorOption = attempt.errorOption();
        Assertions.assertTrue(errorOption.present());
        Assertions.assertEquals(exception, errorOption.get());
    }

    @Test
    void testAttemptOptionToOptionalWhenPresent() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Optional<Exception> optional = attempt.errorOptional();
        Assertions.assertTrue(optional.isEmpty());
    }

    @Test
    void testAttemptOptionToOptionalWhenAbsent() {
        Attempt<String, Exception> attempt = Attempt.empty();
        Optional<Exception> optional = attempt.errorOptional();
        Assertions.assertTrue(optional.isEmpty());
    }

    @Test
    void testAttemptOptionToOptionalWhenFailed() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Optional<Exception> optional = attempt.errorOptional();
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(exception, optional.get());
    }

    @Test
    void testAttemptOptionToStreamWhenPresent() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Stream<Exception> stream = attempt.errorStream();
        Assertions.assertEquals(0, stream.count());
    }

    @Test
    void testAttemptOptionToStreamWhenAbsent() {
        Attempt<String, Exception> attempt = Attempt.empty();
        Stream<Exception> stream = attempt.errorStream();
        Assertions.assertEquals(0, stream.count());
    }

    @Test
    void testAttemptOptionToStreamWhenFailed() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertEquals(1, attempt.errorStream().count());
        Assertions.assertEquals(exception, attempt.errorStream().findFirst().orElse(null));
    }

    @Test
    void testPeekErrorWhenPresent() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        AtomicBoolean called = new AtomicBoolean(false);
        attempt.peekError(value -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testPeekErrorWhenAbsent() {
        Attempt<String, Exception> attempt = Attempt.empty();
        AtomicBoolean called = new AtomicBoolean(false);
        attempt.peekError(value -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testPeekErrorWhenFailed() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        AtomicBoolean called = new AtomicBoolean(false);
        attempt.peekError(value -> {
            Assertions.assertEquals(exception, value);
            called.set(true);
        });
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOnEmptyErrorWhenPresent() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        AtomicBoolean called = new AtomicBoolean(false);
        attempt.onEmptyError(() -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOnEmptyErrorWhenAbsent() {
        Attempt<String, Exception> attempt = Attempt.empty();
        AtomicBoolean called = new AtomicBoolean(false);
        attempt.onEmptyError(() -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOnEmptyErrorWhenFailed() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        AtomicBoolean called = new AtomicBoolean(false);
        attempt.onEmptyError(() -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testErrorOrNullWithoutError() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Assertions.assertNull(attempt.errorOrNull());
    }

    @Test
    void testErrorOrNullWithError() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertSame(exception, attempt.errorOrNull());
    }

    @Test
    void testErrorOrElseWithoutError() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Exception exception = new Exception();
        Assertions.assertSame(exception, attempt.errorOrElse(exception));
    }

    @Test
    void testErrorOrElseWithError() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertSame(exception, attempt.errorOrElse(new Exception()));
    }

    @Test
    void testErrorOrElseGetWithoutError() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Exception exception = new Exception();
        Assertions.assertSame(exception, attempt.errorOrElseGet(() -> exception));
    }

    @Test
    void testErrorOrElseGetWithError() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertSame(exception, attempt.errorOrElseGet(Exception::new));
    }

    @Test
    void testOrComputeErrorWithoutError() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Exception exception = new Exception();
        Assertions.assertSame(exception, attempt.orComputeError(() -> exception).error());
    }

    @Test
    void testOrComputeErrorWithError() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertSame(exception, attempt.orComputeError(Exception::new).error());
    }

    @Test
    void testMapErrorWithoutError() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Exception exception = new Exception();
        Assertions.assertNull(attempt.mapError(value -> exception).errorOrNull());
    }

    @Test
    void testMapErrorWithError() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Attempt<String, IllegalArgumentException> mappedOption = attempt.mapError(IllegalArgumentException::new);
        Assertions.assertNotNull(mappedOption.errorOrNull());
        Assertions.assertSame(exception, mappedOption.error().getCause());
    }

    @Test
    void testFlatMapErrorWithoutError() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Exception exception = new Exception();
        Assertions.assertNull(attempt.flatMapError(value -> Attempt.of(exception)).errorOrNull());
    }

    @Test
    void testFlatMapErrorWithError() {
        Exception exception = new Exception();
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Attempt<String, IllegalArgumentException> mappedOption = attempt.flatMapError(error -> {
            return Attempt.of(new IllegalArgumentException(error));
        });
        Assertions.assertNotNull(mappedOption.errorOrNull());
        Assertions.assertSame(exception, mappedOption.error().getCause());
    }

    @Test
    void testFilterErrorWithoutError() {
        Attempt<String, Exception> attempt = Attempt.of("test");
        Assertions.assertNull(attempt.filterError(value -> {
            Assertions.fail("Should not be called");
            return true;
        }).errorOrNull());
    }

    @Test
    void testFilterErrorWithError() {
        Exception exception = new Exception("test");
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertNotNull(attempt.filterError(error -> "test".equals(error.getMessage())).errorOrNull());
    }

    @Test
    void testFilterErrorWithErrorAndNotMatching() {
        Exception exception = new Exception("test");
        Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertNull(attempt.filterError(error -> "test2".equals(error.getMessage())).errorOrNull());
    }

    @Test
    void testFilterErrorWithEmpty() {
        Attempt<String, Exception> attempt = Attempt.empty();
        Assertions.assertNull(attempt.filterError(error -> {
            Assertions.fail("Should not be called");
            return true;
        }).errorOrNull());
    }

    @Test
    void testPeekErrorWithMatchingExactType() {
        Attempt<Object, NullPointerException> attempt = Attempt.of(new NullPointerException());
        AtomicBoolean called = new AtomicBoolean(false);
        attempt.peekError(NullPointerException.class, exception -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testPeekErrorWithNonExactType() {
        Attempt<Object, RuntimeException> attempt = Attempt.of(new NullPointerException());
        attempt.peekError(RuntimeException.class, exception -> Assertions.fail());
    }

    @Test
    void testPeekErrorWithNonMatchingType() {
        Attempt<Object, RuntimeException> attempt = Attempt.of(new NullPointerException());
        attempt.peekError(IllegalArgumentException.class, exception -> Assertions.fail());
    }

    @Test
    void testPeekErrorWithEmpty() {
        Attempt<Object, RuntimeException> attempt = Attempt.empty();
        attempt.peekError(RuntimeException.class, exception -> Assertions.fail());
    }
}
