/*
 * Copyright 2019-2022 the original author or authors.
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

package test.org.dockbox.hartshorn.core;

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
        final Option<String> option = Option.of("test");
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testOfNull() {
        final Option<String> option = Option.of((String) null);
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testOfOptional() {
        final Option<String> option = Option.of(Optional.of("test"));
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testOfOptionalEmpty() {
        final Option<String> option = Option.of(Optional.empty());
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testOfSupplier() {
        final Option<String> option = Option.of(() -> "test");
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testOfSupplierNull() {
        final Option<String> option = Option.of(() -> null);
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testEmpty() {
        final Option<String> option = Option.empty();
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testCallable() {
        final Attempt<String, Exception> attempt = Attempt.<String>of(() -> "test");
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testCallableNull() {
        final Attempt<String, Exception> attempt = Attempt.<String>of(() -> null);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testCallableException() {
        final Attempt<String, Exception> attempt = Attempt.<String>of(() -> {
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
        final Attempt<String, NullPointerException> attempt = Attempt.of(() -> "test", NullPointerException.class);
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testAttemptSupplierNull() {
        final Attempt<String, NullPointerException> attempt = Attempt.of(() -> null, NullPointerException.class);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptSupplierException() {
        final Attempt<String, NullPointerException> attempt = Attempt.of(() -> {
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
        final IllegalArgumentException illegalArgumentException = new IllegalArgumentException();
        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> Attempt.of(() -> {
            throw illegalArgumentException;
        }, NullPointerException.class));
        Assertions.assertSame(illegalArgumentException, exception);
    }

    @Test
    void testAttemptEmpty() {
        final Attempt<String, Exception> attempt = Attempt.empty();
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfNonNull() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testAttemptOfNull() {
        final Attempt<String, Exception> attempt = Attempt.of((String) null);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfOptional() {
        final Attempt<String, Exception> attempt = Attempt.of(Optional.of("test"));
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testAttemptOfOptionalEmpty() {
        final Attempt<String, Exception> attempt = Attempt.of(Optional.empty());
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfNonNullException() {
        final Attempt<String, IllegalArgumentException> attempt = Attempt.of(new IllegalArgumentException());
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertTrue(attempt.errorPresent());
        Assertions.assertFalse(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfNullException() {
        final Attempt<String, IllegalArgumentException> attempt = Attempt.of((IllegalArgumentException) null);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfNonNullAndNonNullException() {
        final Attempt<String, IllegalArgumentException> attempt = Attempt.of("test", new IllegalArgumentException());
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertTrue(attempt.errorPresent());
        Assertions.assertFalse(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testAttemptOfNonNullAndNullException() {
        final Attempt<String, IllegalArgumentException> attempt = Attempt.of("test", null);
        Assertions.assertTrue(attempt.present());
        Assertions.assertFalse(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertEquals("test", attempt.get());
    }

    @Test
    void testAttemptOfNullAndNonNullException() {
        final Attempt<String, IllegalArgumentException> attempt = Attempt.of(null, new IllegalArgumentException());
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertTrue(attempt.errorPresent());
        Assertions.assertFalse(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testAttemptOfNullAndNullException() {
        final Attempt<String, IllegalArgumentException> attempt = Attempt.of(null, (IllegalArgumentException) null);
        Assertions.assertFalse(attempt.present());
        Assertions.assertTrue(attempt.absent());
        Assertions.assertFalse(attempt.errorPresent());
        Assertions.assertTrue(attempt.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, attempt::get);
    }

    @Test
    void testPeekWhenPresent() {
        final Option<String> option = Option.of("test");
        final AtomicBoolean called = new AtomicBoolean(false);
        option.peek(value -> {
            Assertions.assertEquals("test", value);
            called.set(true);
        });
        Assertions.assertTrue(called.get());
    }

    @Test
    void testPeekWhenAbsent() {
        final Option<String> option = Option.empty();
        final AtomicBoolean called = new AtomicBoolean(false);
        option.peek(value -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testOnEmptyWhenPresent() {
        final Option<String> option = Option.of("test");
        final AtomicBoolean called = new AtomicBoolean(false);
        option.onEmpty(() -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testOnEmptyWhenAbsent() {
        final Option<String> option = Option.empty();
        final AtomicBoolean called = new AtomicBoolean(false);
        option.onEmpty(() -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOrNullWhenPresent() {
        final Option<String> option = Option.of("test");
        Assertions.assertEquals("test", option.orNull());
    }

    @Test
    void testOrNullWhenAbsent() {
        final Option<String> option = Option.empty();
        Assertions.assertNull(option.orNull());
    }

    @Test
    void testOrElseWhenPresent() {
        final Option<String> option = Option.of("test");
        Assertions.assertEquals("test", option.orElse("test2"));
    }

    @Test
    void testOrElseWhenAbsent() {
        final Option<String> option = Option.empty();
        Assertions.assertEquals("test2", option.orElse("test2"));
    }

    @Test
    void testOrElseGetWhenPresent() {
        final Option<String> option = Option.of("test");
        Assertions.assertEquals("test", option.orElseGet(() -> "test2"));
    }

    @Test
    void testOrElseGetWhenAbsent() {
        final Option<String> option = Option.empty();
        Assertions.assertEquals("test2", option.orElseGet(() -> "test2"));
    }

    @Test
    void testOrElseThrowWhenPresent() {
        final Option<String> option = Option.of("test");
        Assertions.assertEquals("test", option.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    void testOrElseThrowWhenAbsent() {
        final Option<String> option = Option.empty();
        Assertions.assertThrows(IllegalArgumentException.class, () -> option.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    void testOrComputeWhenPresent() {
        final Option<String> option = Option.of("test");
        Assertions.assertEquals("test", option.orCompute(() -> "test2").get());
    }

    @Test
    void testOrComputeWhenAbsent() {
        final Option<String> option = Option.empty();
        Assertions.assertEquals("test2", option.orCompute(() -> "test2").get());
    }

    @Test
    void testToOptionalWhenPresent() {
        final Option<String> option = Option.of("test");
        final Optional<String> optional = option.optional();
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testToOptionalWhenAbsent() {
        final Option<String> option = Option.empty();
        final Optional<String> optional = option.optional();
        Assertions.assertFalse(optional.isPresent());
    }

    @Test
    void testToStreamWhenPresent() {
        final Option<String> option = Option.of("test");
        Assertions.assertEquals(1, option.stream().count());
        final Optional<String> optional = option.stream().findFirst();
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testToStreamWhenAbsent() {
        final Option<String> option = Option.empty();
        final Stream<String> stream = option.stream();
        Assertions.assertEquals(0, stream.count());
    }

    @Test
    void testMapWhenPresent() {
        final Option<String> option = Option.of("test");
        final Option<Integer> mapped = option.map(String::length);
        Assertions.assertTrue(mapped.present());
        Assertions.assertEquals(4, mapped.get());
    }

    @Test
    void testMapWhenAbsent() {
        final Option<String> option = Option.empty();
        final Option<Integer> mapped = option.map(String::length);
        Assertions.assertTrue(mapped.absent());
    }

    @Test
    void testFlatMapWhenPresent() {
        final Option<String> option = Option.of("test");
        final Option<Integer> mapped = option.flatMap(value -> Option.of(value.length()));
        Assertions.assertTrue(mapped.present());
        Assertions.assertEquals(4, mapped.get());
    }

    @Test
    void testFlatMapWhenAbsent() {
        final Option<String> option = Option.empty();
        final Option<Integer> mapped = option.flatMap(value -> Option.of(value.length()));
        Assertions.assertTrue(mapped.absent());
    }

    @Test
    void testFilterWhenPresentAndMatches() {
        final Option<String> option = Option.of("test");
        final Option<String> filtered = option.filter(value -> value.length() == 4);
        Assertions.assertTrue(filtered.present());
        Assertions.assertEquals("test", filtered.get());
    }

    @Test
    void testFilterWhenPresentAndDoesNotMatch() {
        final Option<String> option = Option.of("test");
        final Option<String> filtered = option.filter(value -> value.length() == 5);
        Assertions.assertTrue(filtered.absent());
    }

    @Test
    void testFilterWhenAbsent() {
        final Option<String> option = Option.empty();
        final Option<String> filtered = option.filter(value -> {
            Assertions.fail("Should not be called");
            return true;
        });
        Assertions.assertTrue(filtered.absent());
    }

    @Test
    void testContainsWhenPresentAndMatches() {
        final Option<String> option = Option.of("test");
        Assertions.assertTrue(option.contains("test"));
    }

    @Test
    void testContainsWhenPresentAndDoesNotMatch() {
        final Option<String> option = Option.of("test");
        Assertions.assertFalse(option.contains("test2"));
    }

    @Test
    void testContainsWhenAbsent() {
        final Option<String> option = Option.empty();
        Assertions.assertFalse(option.contains("test"));
    }

    @Test
    void testAttemptOptionToOptionWhenPresent() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final Option<String> option = attempt.option();
        Assertions.assertTrue(option.present());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testAttemptOptionToOptionWhenAbsent() {
        final Attempt<String, Exception> attempt = Attempt.empty();
        final Option<String> option = attempt.option();
        Assertions.assertTrue(option.absent());
    }

    @Test
    void testAttemptOptionToOptionWhenFailed() {
        final Attempt<String, Exception> attempt = Attempt.of(new Exception());
        final Option<String> option = attempt.option();
        Assertions.assertTrue(option.absent());
    }

    @Test
    void testAttemptOptionToErrorOptionWhenPresent() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final Option<Exception> errorOption = attempt.errorOption();
        Assertions.assertTrue(errorOption.absent());
    }

    @Test
    void testAttemptOptionToErrorOptionWhenAbsent() {
        final Attempt<String, Exception> attempt = Attempt.empty();
        final Option<Exception> errorOption = attempt.errorOption();
        Assertions.assertTrue(errorOption.absent());
    }

    @Test
    void testAttemptOptionToErrorOptionWhenFailed() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        final Option<Exception> errorOption = attempt.errorOption();
        Assertions.assertTrue(errorOption.present());
        Assertions.assertEquals(exception, errorOption.get());
    }

    @Test
    void testAttemptOptionToOptionalWhenPresent() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final Optional<Exception> optional = attempt.errorOptional();
        Assertions.assertTrue(optional.isEmpty());
    }

    @Test
    void testAttemptOptionToOptionalWhenAbsent() {
        final Attempt<String, Exception> attempt = Attempt.empty();
        final Optional<Exception> optional = attempt.errorOptional();
        Assertions.assertTrue(optional.isEmpty());
    }

    @Test
    void testAttemptOptionToOptionalWhenFailed() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        final Optional<Exception> optional = attempt.errorOptional();
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(exception, optional.get());
    }

    @Test
    void testAttemptOptionToStreamWhenPresent() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final Stream<Exception> stream = attempt.errorStream();
        Assertions.assertEquals(0, stream.count());
    }

    @Test
    void testAttemptOptionToStreamWhenAbsent() {
        final Attempt<String, Exception> attempt = Attempt.empty();
        final Stream<Exception> stream = attempt.errorStream();
        Assertions.assertEquals(0, stream.count());
    }

    @Test
    void testAttemptOptionToStreamWhenFailed() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertEquals(1, attempt.errorStream().count());
        Assertions.assertEquals(exception, attempt.errorStream().findFirst().orElse(null));
    }

    @Test
    void testPeekErrorWhenPresent() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final AtomicBoolean called = new AtomicBoolean(false);
        attempt.peekError(value -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testPeekErrorWhenAbsent() {
        final Attempt<String, Exception> attempt = Attempt.empty();
        final AtomicBoolean called = new AtomicBoolean(false);
        attempt.peekError(value -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testPeekErrorWhenFailed() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        final AtomicBoolean called = new AtomicBoolean(false);
        attempt.peekError(value -> {
            Assertions.assertEquals(exception, value);
            called.set(true);
        });
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOnEmptyErrorWhenPresent() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final AtomicBoolean called = new AtomicBoolean(false);
        attempt.onEmptyError(() -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOnEmptyErrorWhenAbsent() {
        final Attempt<String, Exception> attempt = Attempt.empty();
        final AtomicBoolean called = new AtomicBoolean(false);
        attempt.onEmptyError(() -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOnEmptyErrorWhenFailed() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        final AtomicBoolean called = new AtomicBoolean(false);
        attempt.onEmptyError(() -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testErrorOrNullWithoutError() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        Assertions.assertNull(attempt.errorOrNull());
    }

    @Test
    void testErrorOrNullWithError() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertSame(exception, attempt.errorOrNull());
    }

    @Test
    void testErrorOrElseWithoutError() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final Exception exception = new Exception();
        Assertions.assertSame(exception, attempt.errorOrElse(exception));
    }

    @Test
    void testErrorOrElseWithError() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertSame(exception, attempt.errorOrElse(new Exception()));
    }

    @Test
    void testErrorOrElseGetWithoutError() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final Exception exception = new Exception();
        Assertions.assertSame(exception, attempt.errorOrElseGet(() -> exception));
    }

    @Test
    void testErrorOrElseGetWithError() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertSame(exception, attempt.errorOrElseGet(Exception::new));
    }

    @Test
    void testOrComputeErrorWithoutError() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final Exception exception = new Exception();
        Assertions.assertSame(exception, attempt.orComputeError(() -> exception).error());
    }

    @Test
    void testOrComputeErrorWithError() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertSame(exception, attempt.orComputeError(Exception::new).error());
    }

    @Test
    void testMapErrorWithoutError() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final Exception exception = new Exception();
        Assertions.assertNull(attempt.mapError(value -> exception).errorOrNull());
    }

    @Test
    void testMapErrorWithError() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        final Attempt<String, IllegalArgumentException> mappedOption = attempt.mapError(IllegalArgumentException::new);
        Assertions.assertNotNull(mappedOption.errorOrNull());
        Assertions.assertSame(exception, mappedOption.error().getCause());
    }

    @Test
    void testFlatMapErrorWithoutError() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        final Exception exception = new Exception();
        Assertions.assertNull(attempt.flatMapError(value -> Attempt.of(exception)).errorOrNull());
    }

    @Test
    void testFlatMapErrorWithError() {
        final Exception exception = new Exception();
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        final Attempt<String, IllegalArgumentException> mappedOption = attempt.flatMapError(error -> {
            return Attempt.of(new IllegalArgumentException(error));
        });
        Assertions.assertNotNull(mappedOption.errorOrNull());
        Assertions.assertSame(exception, mappedOption.error().getCause());
    }

    @Test
    void testFilterErrorWithoutError() {
        final Attempt<String, Exception> attempt = Attempt.of("test");
        Assertions.assertNull(attempt.filterError(value -> {
            Assertions.fail("Should not be called");
            return true;
        }).errorOrNull());
    }

    @Test
    void testFilterErrorWithError() {
        final Exception exception = new Exception("test");
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertNotNull(attempt.filterError(error -> "test".equals(error.getMessage())).errorOrNull());
    }

    @Test
    void testFilterErrorWithErrorAndNotMatching() {
        final Exception exception = new Exception("test");
        final Attempt<String, Exception> attempt = Attempt.of(exception);
        Assertions.assertNull(attempt.filterError(error -> "test2".equals(error.getMessage())).errorOrNull());
    }

    @Test
    void testFilterErrorWithEmpty() {
        final Attempt<String, Exception> attempt = Attempt.empty();
        Assertions.assertNull(attempt.filterError(error -> {
            Assertions.fail("Should not be called");
            return true;
        }).errorOrNull());
    }

    @Test
    void testPeekErrorWithMatchingExactType() {
        final Attempt<Object, NullPointerException> attempt = Attempt.of(new NullPointerException());
        final AtomicBoolean called = new AtomicBoolean(false);
        attempt.peekError(NullPointerException.class, e -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testPeekErrorWithNonExactType() {
        final Attempt<Object, RuntimeException> attempt = Attempt.of(new NullPointerException());
        attempt.peekError(RuntimeException.class, e -> Assertions.fail());
    }

    @Test
    void testPeekErrorWithNonMatchingType() {
        final Attempt<Object, RuntimeException> attempt = Attempt.of(new NullPointerException());
        attempt.peekError(IllegalArgumentException.class, e -> Assertions.fail());
    }

    @Test
    void testPeekErrorWithEmpty() {
        final Attempt<Object, RuntimeException> attempt = Attempt.empty();
        attempt.peekError(RuntimeException.class, e -> Assertions.fail());
    }
}
