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

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.dockbox.hartshorn.util.option.FailableOption;
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
    void testCallable() {
        FailableOption<String, Exception> option = FailableOption.<String>of(() -> "test");
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testCallableNull() {
        FailableOption<String, Exception> option = FailableOption.<String>of(() -> null);
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testCallableException() {
        FailableOption<String, Exception> option = FailableOption.<String>of(() -> {
            throw new Exception();
        });
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertTrue(option.errorPresent());
        Assertions.assertFalse(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testFailableSupplier() {
        FailableOption<String, NullPointerException> option = FailableOption.of(() -> "test", NullPointerException.class);
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testFailableSupplierNull() {
        FailableOption<String, NullPointerException> option = FailableOption.of(() -> null, NullPointerException.class);
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testFailableSupplierException() {
        FailableOption<String, NullPointerException> option = FailableOption.of(() -> {
            throw new NullPointerException();
        }, NullPointerException.class);
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertTrue(option.errorPresent());
        Assertions.assertFalse(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testFailableSupplierExceptionOther() {
        final IllegalArgumentException illegalArgumentException = new IllegalArgumentException();
        final RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> FailableOption.of(() -> {
            throw illegalArgumentException;
        }, NullPointerException.class));
        Assertions.assertEquals(illegalArgumentException, exception.getCause());
    }

    @Test
    void testFailableEmpty() {
        FailableOption<String, Exception> option = FailableOption.empty();
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testFailableOfNonNull() {
        FailableOption<String, Exception> option = FailableOption.of("test");
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testFailableOfNull() {
        FailableOption<String, Exception> option = FailableOption.of((String) null);
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testFailableOfOptional() {
        FailableOption<String, Exception> option = FailableOption.of(Optional.of("test"));
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testFailableOfOptionalEmpty() {
        FailableOption<String, Exception> option = FailableOption.of(Optional.empty());
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testFailableOfNonNullException() {
        FailableOption<String, IllegalArgumentException> option = FailableOption.of(new IllegalArgumentException());
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertTrue(option.errorPresent());
        Assertions.assertFalse(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testFailableOfNullException() {
        FailableOption<String, IllegalArgumentException> option = FailableOption.of((IllegalArgumentException) null);
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testFailableOfNonNullAndNonNullException() {
        FailableOption<String, IllegalArgumentException> option = FailableOption.of("test", new IllegalArgumentException());
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertTrue(option.errorPresent());
        Assertions.assertFalse(option.errorAbsent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testFailableOfNonNullAndNullException() {
        FailableOption<String, IllegalArgumentException> option = FailableOption.of("test", null);
        Assertions.assertTrue(option.present());
        Assertions.assertFalse(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testFailableOfNullAndNonNullException() {
        FailableOption<String, IllegalArgumentException> option = FailableOption.of(null, new IllegalArgumentException());
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertTrue(option.errorPresent());
        Assertions.assertFalse(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testFailableOfNullAndNullException() {
        FailableOption<String, IllegalArgumentException> option = FailableOption.of(null, (IllegalArgumentException) null);
        Assertions.assertFalse(option.present());
        Assertions.assertTrue(option.absent());
        Assertions.assertFalse(option.errorPresent());
        Assertions.assertTrue(option.errorAbsent());
        Assertions.assertThrows(NoSuchElementException.class, option::get);
    }

    @Test
    void testPeekWhenPresent() {
        final Option<String> option = Option.of("test");
        AtomicBoolean called = new AtomicBoolean(false);
        option.peek(value -> {
            Assertions.assertEquals("test", value);
            called.set(true);
        });
        Assertions.assertTrue(called.get());
    }

    @Test
    void testPeekWhenAbsent() {
        final Option<String> option = Option.empty();
        AtomicBoolean called = new AtomicBoolean(false);
        option.peek(value -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testOnEmptyWhenPresent() {
        final Option<String> option = Option.of("test");
        AtomicBoolean called = new AtomicBoolean(false);
        option.onEmpty(() -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testOnEmptyWhenAbsent() {
        final Option<String> option = Option.empty();
        AtomicBoolean called = new AtomicBoolean(false);
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
    void testFailableOptionToOptionWhenPresent() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        final Option<String> option = failableOption.option();
        Assertions.assertTrue(option.present());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testFailableOptionToOptionWhenAbsent() {
        final FailableOption<String, Exception> failableOption = FailableOption.empty();
        final Option<String> option = failableOption.option();
        Assertions.assertTrue(option.absent());
    }

    @Test
    void testFailableOptionToOptionWhenFailed() {
        final FailableOption<String, Exception> failableOption = FailableOption.of(new Exception());
        final Option<String> option = failableOption.option();
        Assertions.assertTrue(option.absent());
    }

    @Test
    void testFailableOptionToErrorOptionWhenPresent() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        final Option<Exception> errorOption = failableOption.errorOption();
        Assertions.assertTrue(errorOption.absent());
    }

    @Test
    void testFailableOptionToErrorOptionWhenAbsent() {
        final FailableOption<String, Exception> failableOption = FailableOption.empty();
        final Option<Exception> errorOption = failableOption.errorOption();
        Assertions.assertTrue(errorOption.absent());
    }

    @Test
    void testFailableOptionToErrorOptionWhenFailed() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        final Option<Exception> errorOption = failableOption.errorOption();
        Assertions.assertTrue(errorOption.present());
        Assertions.assertEquals(exception, errorOption.get());
    }

    @Test
    void testFailableOptionToOptionalWhenPresent() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        final Optional<Exception> optional = failableOption.errorOptional();
        Assertions.assertTrue(optional.isEmpty());
    }

    @Test
    void testFailableOptionToOptionalWhenAbsent() {
        final FailableOption<String, Exception> failableOption = FailableOption.empty();
        final Optional<Exception> optional = failableOption.errorOptional();
        Assertions.assertTrue(optional.isEmpty());
    }

    @Test
    void testFailableOptionToOptionalWhenFailed() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        final Optional<Exception> optional = failableOption.errorOptional();
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(exception, optional.get());
    }

    @Test
    void testFailableOptionToStreamWhenPresent() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        final Stream<Exception> stream = failableOption.errorStream();
        Assertions.assertEquals(0, stream.count());
    }

    @Test
    void testFailableOptionToStreamWhenAbsent() {
        final FailableOption<String, Exception> failableOption = FailableOption.empty();
        final Stream<Exception> stream = failableOption.errorStream();
        Assertions.assertEquals(0, stream.count());
    }

    @Test
    void testFailableOptionToStreamWhenFailed() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        Assertions.assertEquals(1, failableOption.errorStream().count());
        Assertions.assertEquals(exception, failableOption.errorStream().findFirst().orElse(null));
    }

    @Test
    void testPeekErrorWhenPresent() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        AtomicBoolean called = new AtomicBoolean(false);
        failableOption.peekError(value -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testPeekErrorWhenAbsent() {
        final FailableOption<String, Exception> failableOption = FailableOption.empty();
        AtomicBoolean called = new AtomicBoolean(false);
        failableOption.peekError(value -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testPeekErrorWhenFailed() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        AtomicBoolean called = new AtomicBoolean(false);
        failableOption.peekError(value -> {
            Assertions.assertEquals(exception, value);
            called.set(true);
        });
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOnEmptyErrorWhenPresent() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        AtomicBoolean called = new AtomicBoolean(false);
        failableOption.onEmptyError(() -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOnEmptyErrorWhenAbsent() {
        final FailableOption<String, Exception> failableOption = FailableOption.empty();
        AtomicBoolean called = new AtomicBoolean(false);
        failableOption.onEmptyError(() -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testOnEmptyErrorWhenFailed() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        AtomicBoolean called = new AtomicBoolean(false);
        failableOption.onEmptyError(() -> {
            Assertions.fail("Should not be called");
            called.set(true);
        });
        Assertions.assertFalse(called.get());
    }

    @Test
    void testErrorOrNullWithoutError() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        Assertions.assertNull(failableOption.errorOrNull());
    }

    @Test
    void testErrorOrNullWithError() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        Assertions.assertSame(exception, failableOption.errorOrNull());
    }

    @Test
    void testErrorOrElseWithoutError() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        final Exception exception = new Exception();
        Assertions.assertSame(exception, failableOption.errorOrElse(exception));
    }

    @Test
    void testErrorOrElseWithError() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        Assertions.assertSame(exception, failableOption.errorOrElse(new Exception()));
    }

    @Test
    void testErrorOrElseGetWithoutError() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        final Exception exception = new Exception();
        Assertions.assertSame(exception, failableOption.errorOrElseGet(() -> exception));
    }

    @Test
    void testErrorOrElseGetWithError() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        Assertions.assertSame(exception, failableOption.errorOrElseGet(Exception::new));
    }

    @Test
    void testOrComputeErrorWithoutError() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        final Exception exception = new Exception();
        Assertions.assertSame(exception, failableOption.orComputeError(() -> exception).error());
    }

    @Test
    void testOrComputeErrorWithError() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        Assertions.assertSame(exception, failableOption.orComputeError(Exception::new).error());
    }

    @Test
    void testMapErrorWithoutError() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        final Exception exception = new Exception();
        Assertions.assertNull(failableOption.mapError(value -> exception).errorOrNull());
    }

    @Test
    void testMapErrorWithError() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        final FailableOption<String, IllegalArgumentException> mappedOption = failableOption.mapError(IllegalArgumentException::new);
        Assertions.assertNotNull(mappedOption.errorOrNull());
        Assertions.assertSame(exception, mappedOption.error().getCause());
    }

    @Test
    void testFlatMapErrorWithoutError() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        final Exception exception = new Exception();
        Assertions.assertNull(failableOption.flatMapError(value -> FailableOption.of(exception)).errorOrNull());
    }

    @Test
    void testFlatMapErrorWithError() {
        final Exception exception = new Exception();
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        final FailableOption<String, IllegalArgumentException> mappedOption = failableOption.flatMapError(error -> {
            return FailableOption.of(new IllegalArgumentException(error));
        });
        Assertions.assertNotNull(mappedOption.errorOrNull());
        Assertions.assertSame(exception, mappedOption.error().getCause());
    }

    @Test
    void testFilterErrorWithoutError() {
        final FailableOption<String, Exception> failableOption = FailableOption.of("test");
        Assertions.assertNull(failableOption.filterError(value -> {
            Assertions.fail("Should not be called");
            return true;
        }).errorOrNull());
    }

    @Test
    void testFilterErrorWithError() {
        final Exception exception = new Exception("test");
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        Assertions.assertNotNull(failableOption.filterError(error -> "test".equals(error.getMessage())).errorOrNull());
    }

    @Test
    void testFilterErrorWithErrorAndNotMatching() {
        final Exception exception = new Exception("test");
        final FailableOption<String, Exception> failableOption = FailableOption.of(exception);
        Assertions.assertNull(failableOption.filterError(error -> "test2".equals(error.getMessage())).errorOrNull());
    }

    @Test
    void testFilterErrorWithEmpty() {
        final FailableOption<String, Exception> failableOption = FailableOption.empty();
        Assertions.assertNull(failableOption.filterError(error -> {
            Assertions.fail("Should not be called");
            return true;
        }).errorOrNull());
    }
}
