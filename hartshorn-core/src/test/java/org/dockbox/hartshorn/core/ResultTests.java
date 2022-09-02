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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;

public class ResultTests {

    @Test
    void testCanCreateFromFilledOptional() {
        final Optional<String> optional = Optional.of("value");
        final Result<String> result = Result.of(optional);

        Assertions.assertTrue(result.present());
        Assertions.assertFalse(result.absent());
        Assertions.assertEquals("value", result.get());
    }

    @Test
    void testCanCreateFromEmptyOptional() {
        final Optional<String> optional = Optional.empty();
        final Result<String> result = Result.of(optional);

        Assertions.assertFalse(result.present());
        Assertions.assertTrue(result.absent());
    }

    @Test
    void testCanCreateFromNonNullCallable() {
        final Callable<String> callable = () -> "value";
        final Result<String> result = Result.of(callable);

        Assertions.assertTrue(result.present());
        Assertions.assertFalse(result.absent());
        Assertions.assertEquals("value", result.get());
    }

    @Test
    void testCanCreateFromNullCallable() {
        final Callable<String> callable = () -> null;
        final Result<String> result = Result.of(callable);

        Assertions.assertFalse(result.present());
        Assertions.assertTrue(result.absent());
    }

    @Test
    void testCanCreateFromThrowingCallable() {
        final Callable<String> callable = () -> {
            throw new ApplicationException("Error");
        };
        final Result<String> result = Result.of(callable);

        Assertions.assertFalse(result.present());
        Assertions.assertTrue(result.absent());
        Assertions.assertTrue(result.caught());
    }

    @Test
    void testCanCreateFromValue() {
        final Result<String> result = Result.of("value");

        Assertions.assertTrue(result.present());
        Assertions.assertFalse(result.absent());
        Assertions.assertEquals("value", result.get());
    }

    @Test
    void testCanCreateFromNull() {
        final Result<String> result = Result.of((String) null);

        Assertions.assertFalse(result.present());
        Assertions.assertTrue(result.absent());
    }

    @Test
    void testCanCreateFromException() {
        final Result<String> result = Result.of(new Exception());

        Assertions.assertFalse(result.present());
        Assertions.assertTrue(result.absent());
        Assertions.assertTrue(result.caught());
    }

    @Test
    void testCannotCreateFromNullException() {
        Assertions.assertThrows(NullPointerException.class, () -> Result.of((Throwable) null));
    }

    @Test
    void testCanCreateFromValueAndException() {
        final Result<String> result = Result.of("value", new Exception());

        Assertions.assertTrue(result.present());
        Assertions.assertFalse(result.absent());
        Assertions.assertTrue(result.caught());
    }

    @Test
    void testCanCreateFromNullValueAndException() {
        final Result<String> result = Result.of(null, new Exception());

        Assertions.assertFalse(result.present());
        Assertions.assertTrue(result.absent());
        Assertions.assertTrue(result.caught());
    }

    @Test
    void testCanCreateFromNullValueAndNullException() {
        final Result<String> result = Result.of(null, null);

        Assertions.assertFalse(result.present());
        Assertions.assertTrue(result.absent());
        Assertions.assertFalse(result.caught());
    }

    @Test
    void testCanCreateFromValueAndNullException() {
        final Result<String> result = Result.of("value", null);

        Assertions.assertTrue(result.present());
        Assertions.assertFalse(result.absent());
        Assertions.assertFalse(result.caught());
    }

    @Test
    void testNoneContainsNothing() {
        final Result<String> result = Result.empty();

        Assertions.assertFalse(result.present());
        Assertions.assertTrue(result.absent());
        Assertions.assertFalse(result.caught());
    }

    @Test
    void testGetThrowsExceptionIfAbsent() {
        Assertions.assertThrows(NoSuchElementException.class, () -> Result.empty().get());
    }

    @Test
    void testGetReturnsValueIfPresent() {
        final Result<String> result = Result.of("value");

        Assertions.assertTrue(result.present());
        Assertions.assertFalse(result.absent());
        Assertions.assertFalse(result.caught());
        Assertions.assertEquals("value", result.get());
    }

    @Test
    void testGetWithSupplierReturnsDefaultValueIfAbsent() {
        final Result<String> result = Result.empty();
        final String value = result.get(() -> "other");

        Assertions.assertNotNull(value);
        Assertions.assertEquals("other", value);
    }

    @Test
    void testPresentConsumerActivatesIfValueIsPresent() {
        final Result<String> result = Result.of("value");

        final boolean[] activated = { false };
        result.present(value -> {
            Assertions.assertEquals("value", value);
            activated[0] = true;
        });

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testPresentConsumerDoesNotActivateIfAbsent() {
        final Result<String> result = Result.empty();

        final boolean[] activated = { false };
        result.present(value -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testAbsentConsumerActivatesIfValueIsAbsent() {
        final Result<String> result = Result.empty();

        final boolean[] activated = { false };
        result.absent(() -> activated[0] = true);

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testAbsentConsumerDoesNotActivateIfValueIsPresent() {
        final Result<String> result = Result.of("value");

        final boolean[] activated = { false };
        result.absent(() -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testOrNullReturnsNullIfAbsent() {
        final Result<String> result = Result.empty();
        Assertions.assertNull(result.orNull());
    }

    @Test
    void testOrNullReturnsValueIfPresent() {
        final Result<String> result = Result.of("value");

        Assertions.assertNotNull(result.orNull());
        Assertions.assertEquals("value", result.orNull());
    }

    @Test
    void testOrReturnsValueIfPresent() {
        final Result<String> result = Result.of("value");
        Assertions.assertEquals("value", result.or("other"));
    }

    @Test
    void testOrReturnsOtherIfAbsent() {
        final Result<String> result = Result.empty();
        Assertions.assertEquals("other", result.or("other"));
    }

    @Test
    void testOrReturnsThrowableIfPresent() {
        final Result<String> result = Result.of(new Exception("error"));
        Assertions.assertEquals("error", result.or(new Exception("other")).getMessage());
    }

    @Test
    void testOrReturnsOtherIfThrowableAbsent() {
        final Result<String> result = Result.empty();
        Assertions.assertEquals("other", result.or(new Exception("other")).getMessage());
    }

    @Test
    void testThenDoesNotApplyIfValueAbsent() {
        Result<String> result = Result.empty();
        result = result.flatMap(value -> Result.of("other"));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.present());
    }

    @Test
    void testThenAppliesIfValuePresent() {
        Result<String> result = Result.of("value");
        result = result.flatMap(value -> Result.of("other"));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals("other", result.get());
    }

    @Test
    void testThenWithThrowableDoesNotApplyIfValueAbsent() {
        Result<String> result = Result.of(new Exception());
        result = result.flatMap((value, err) -> Result.of("other"));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.present());
    }

    @Test
    void testFlatMapWithThrowableAppliesIfValuePresent() {
        Result<String> result = Result.of("value", new Exception("error"));
        result = result.flatMap((value, err) -> {
            Assertions.assertEquals("error", err.getMessage());
            return Result.of("other");
        });

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals("other", result.get());
    }

    @Test
    void testOrElseWithSupplierReturnsValueAndExceptionIfPresent() {
        Result<String> result = Result.of("value", new Exception());
        result = result.orElse(() -> "other");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.present());
        Assertions.assertTrue(result.caught());
        Assertions.assertEquals("value", result.discardError().get());
    }

    @Test
    void testFilterReturnsEmptyIfFilterDoesNotMatch() {
        Result<String> result = Result.of("value");
        result = result.filter(value -> value.startsWith("c"));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.present());
    }

    @Test
    void testFilterReturnsValueIfFilterMatches() {
        Result<String> result = Result.of("value");
        result = result.filter(value -> value.startsWith("v"));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.present());
    }

    @Test
    void testMapReturnsValueIfPresent() {
        Result<String> result = Result.of("value");
        result = result.map(value -> value.toUpperCase(Locale.ROOT));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals("VALUE", result.get());
    }

    @Test
    void testCaughtConsumerAppliesIfThrowablePresent() {
        final Result<String> result = Result.of(new Exception("error"));

        final boolean[] activated = { false };
        result.caught(err -> {
            Assertions.assertEquals("error", err.getMessage());
            activated[0] = true;
        });

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testCaughtConsumerDoesNotApplyIfThrowableAbsent() {
        final Result<String> result = Result.empty();

        final boolean[] activated = { false };
        result.caught(err -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testCauseReturnsValueIfPresent() throws Exception {
        final Result<String> result = Result.of("value");
        final String value = result.orThrow(Exception::new);

        Assertions.assertNotNull(value);
        Assertions.assertEquals("value", value);
    }

    @Test
    void testCauseThrowsExceptionIfValueAbsent() {
        Assertions.assertThrows(Exception.class, () -> Result.empty().orThrow(Exception::new));
    }

    @Test
    void testEmptyRunnableAppliesIfThrowableAbsent() {
        final Result<String> result = Result.empty();

        final boolean[] activated = { false };
        result.ifErrorAbsent(() -> activated[0] = true);

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testEmptyRunnableDoesNotApplyIfThrowablePresent() {
        final Result<String> result = Result.of(new Exception());

        final boolean[] activated = { false };
        result.ifErrorAbsent(() -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testRethrowThrowsWrappedException() {
        final Result<String> result = Result.of(new Exception("error"));

        try {
            result.rethrowUnchecked();
            Assertions.fail();
        }
        catch (final Throwable t) {
            Assertions.assertTrue(t instanceof Exception);
            Assertions.assertEquals("error", t.getMessage());
        }
    }

    @Test
    void testTypeReturnsValueTypeIfPresent() {
        final Result<String> result = Result.of("value");

        Assertions.assertNotNull(result.type());
        Assertions.assertEquals(String.class, result.type());
    }

    @Test
    void testTypeReturnsNullIfValueAbsent() {
        final Result<String> result = Result.empty();
        Assertions.assertNull(result.type());
    }

    @Test
    void testEqualReturnsTrueIfValueIsEqual() {
        final Result<String> result = Result.of("value");
        Assertions.assertTrue(result.equal("value"));
    }

    @Test
    void testEqualReturnsFalseIfValueIsAbsent() {
        final Result<String> result = Result.empty();
        Assertions.assertFalse(result.equal("value"));
    }

    @Test
    void testEqualReturnsFalseIfValueIsDifferent() {
        final Result<String> result = Result.of("value");
        Assertions.assertFalse(result.equal("other"));
    }
}
