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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;

public class ExceptionalTests {

    @Test
    void testCanCreateFromFilledOptional() {
        final Optional<String> optional = Optional.of("value");
        final Exceptional<String> exceptional = Exceptional.of(optional);

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertEquals("value", exceptional.get());
    }

    @Test
    void testCanCreateFromEmptyOptional() {
        final Optional<String> optional = Optional.empty();
        final Exceptional<String> exceptional = Exceptional.of(optional);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
    }

    @Test
    void testCanCreateFromNonNullCallable() {
        final Callable<String> callable = () -> "value";
        final Exceptional<String> exceptional = Exceptional.of(callable);

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertEquals("value", exceptional.get());
    }

    @Test
    void testCanCreateFromNullCallable() {
        final Callable<String> callable = () -> null;
        final Exceptional<String> exceptional = Exceptional.of(callable);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
    }

    @Test
    void testCanCreateFromThrowingCallable() {
        final Callable<String> callable = () -> {
            throw new ApplicationException("Error");
        };
        final Exceptional<String> exceptional = Exceptional.of(callable);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
        Assertions.assertTrue(exceptional.caught());
    }

    @Test
    void testCanCreateFromValue() {
        final Exceptional<String> exceptional = Exceptional.of("value");

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertEquals("value", exceptional.get());
    }

    @Test
    void testCanCreateFromNull() {
        final Exceptional<String> exceptional = Exceptional.of((String) null);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
    }

    @Test
    void testCanCreateFromException() {
        final Exceptional<String> exceptional = Exceptional.of(new Exception());

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
        Assertions.assertTrue(exceptional.caught());
    }

    @Test
    void testCannotCreateFromNullException() {
        Assertions.assertThrows(NullPointerException.class, () -> Exceptional.of((Throwable) null));
    }

    @Test
    void testCanCreateFromValueAndException() {
        final Exceptional<String> exceptional = Exceptional.of("value", new Exception());

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertTrue(exceptional.caught());
    }

    @Test
    void testCanCreateFromNullValueAndException() {
        final Exceptional<String> exceptional = Exceptional.of(null, new Exception());

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
        Assertions.assertTrue(exceptional.caught());
    }

    @Test
    void testCanCreateFromNullValueAndNullException() {
        final Exceptional<String> exceptional = Exceptional.of(null, null);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
        Assertions.assertFalse(exceptional.caught());
    }

    @Test
    void testCanCreateFromValueAndNullException() {
        final Exceptional<String> exceptional = Exceptional.of("value", null);

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertFalse(exceptional.caught());
    }

    @Test
    void testCanCreateConditionalTrue() {
        final Exceptional<String> exceptional = Exceptional.of(() -> true, () -> "value", Exception::new);

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertFalse(exceptional.caught());
    }

    @Test
    void testCanCreateConditionalFalse() {
        final Exceptional<String> exceptional = Exceptional.of(() -> false, () -> "value", Exception::new);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
        Assertions.assertTrue(exceptional.caught());
    }

    @Test
    void testNoneContainsNothing() {
        final Exceptional<String> exceptional = Exceptional.empty();

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
        Assertions.assertFalse(exceptional.caught());
    }

    @Test
    void testGetThrowsExceptionIfAbsent() {
        Assertions.assertThrows(NoSuchElementException.class, () -> Exceptional.empty().get());
    }

    @Test
    void testGetReturnsValueIfPresent() {
        final Exceptional<String> exceptional = Exceptional.of("value");

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertFalse(exceptional.caught());
        Assertions.assertEquals("value", exceptional.get());
    }

    @Test
    void testGetWithSupplierReturnsDefaultValueIfAbsent() {
        final Exceptional<String> exceptional = Exceptional.empty();
        final String value = exceptional.get(() -> "other");

        Assertions.assertNotNull(value);
        Assertions.assertEquals("other", value);
    }

    @Test
    void testPresentConsumerActivatesIfValueIsPresent() {
        final Exceptional<String> exceptional = Exceptional.of("value");

        final boolean[] activated = { false };
        exceptional.present(value -> {
            Assertions.assertEquals("value", value);
            activated[0] = true;
        });

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testPresentConsumerDoesNotActivateIfAbsent() {
        final Exceptional<String> exceptional = Exceptional.empty();

        final boolean[] activated = { false };
        exceptional.present(value -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testAbsentConsumerActivatesIfValueIsAbsent() {
        final Exceptional<String> exceptional = Exceptional.empty();

        final boolean[] activated = { false };
        exceptional.absent(() -> activated[0] = true);

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testAbsentConsumerDoesNotActivateIfValueIsPresent() {
        final Exceptional<String> exceptional = Exceptional.of("value");

        final boolean[] activated = { false };
        exceptional.absent(() -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testOrNullReturnsNullIfAbsent() {
        final Exceptional<String> exceptional = Exceptional.empty();
        Assertions.assertNull(exceptional.orNull());
    }

    @Test
    void testOrNullReturnsValueIfPresent() {
        final Exceptional<String> exceptional = Exceptional.of("value");

        Assertions.assertNotNull(exceptional.orNull());
        Assertions.assertEquals("value", exceptional.orNull());
    }

    @Test
    void testOrReturnsValueIfPresent() {
        final Exceptional<String> exceptional = Exceptional.of("value");
        Assertions.assertEquals("value", exceptional.or("other"));
    }

    @Test
    void testOrReturnsOtherIfAbsent() {
        final Exceptional<String> exceptional = Exceptional.empty();
        Assertions.assertEquals("other", exceptional.or("other"));
    }

    @Test
    void testOrReturnsThrowableIfPresent() {
        final Exceptional<String> exceptional = Exceptional.of(new Exception("error"));
        Assertions.assertEquals("error", exceptional.or(new Exception("other")).getMessage());
    }

    @Test
    void testOrReturnsOtherIfThrowableAbsent() {
        final Exceptional<String> exceptional = Exceptional.empty();
        Assertions.assertEquals("other", exceptional.or(new Exception("other")).getMessage());
    }

    @Test
    void testThenDoesNotApplyIfValueAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();
        exceptional = exceptional.flatMap(value -> Exceptional.of("other"));

        Assertions.assertNotNull(exceptional);
        Assertions.assertFalse(exceptional.present());
    }

    @Test
    void testThenAppliesIfValuePresent() {
        Exceptional<String> exceptional = Exceptional.of("value");
        exceptional = exceptional.flatMap(value -> Exceptional.of("other"));

        Assertions.assertNotNull(exceptional);
        Assertions.assertTrue(exceptional.present());
        Assertions.assertEquals("other", exceptional.get());
    }

    @Test
    void testThenWithThrowableDoesNotApplyIfValueAbsent() {
        Exceptional<String> exceptional = Exceptional.of(new Exception());
        exceptional = exceptional.flatMap((value, err) -> Exceptional.of("other"));

        Assertions.assertNotNull(exceptional);
        Assertions.assertFalse(exceptional.present());
    }

    @Test
    void testThenWithThrowableAppliesIfValuePresent() {
        Exceptional<String> exceptional = Exceptional.of("value", new Exception("error"));
        exceptional = exceptional.flatMap((value, err) -> {
            Assertions.assertEquals("error", err.getMessage());
            return Exceptional.of("other");
        });

        Assertions.assertNotNull(exceptional);
        Assertions.assertTrue(exceptional.present());
        Assertions.assertEquals("other", exceptional.get());
    }

    @Test
    void testThenWithSupplierReturnsValueAndExceptionIfPresent() {
        Exceptional<String> exceptional = Exceptional.of("value", new Exception());
        exceptional = exceptional.orElse(() -> "other");

        Assertions.assertNotNull(exceptional);
        Assertions.assertTrue(exceptional.present());
        Assertions.assertTrue(exceptional.caught());
        Assertions.assertEquals("value", exceptional.get());
    }

    @Test
    void testFilterReturnsEmptyIfFilterDoesNotMatch() {
        Exceptional<String> exceptional = Exceptional.of("value");
        exceptional = exceptional.filter(value -> value.startsWith("c"));

        Assertions.assertNotNull(exceptional);
        Assertions.assertFalse(exceptional.present());
    }

    @Test
    void testFilterReturnsValueIfFilterMatches() {
        Exceptional<String> exceptional = Exceptional.of("value");
        exceptional = exceptional.filter(value -> value.startsWith("v"));

        Assertions.assertNotNull(exceptional);
        Assertions.assertTrue(exceptional.present());
    }

    @Test
    void testMapReturnsValueIfPresent() {
        Exceptional<String> exceptional = Exceptional.of("value");
        exceptional = exceptional.map(value -> value.toUpperCase(Locale.ROOT));

        Assertions.assertNotNull(exceptional);
        Assertions.assertTrue(exceptional.present());
        Assertions.assertEquals("VALUE", exceptional.get());
    }

    @Test
    void testCaughtConsumerAppliesIfThrowablePresent() {
        final Exceptional<String> exceptional = Exceptional.of(new Exception("error"));

        final boolean[] activated = { false };
        exceptional.caught(err -> {
            Assertions.assertEquals("error", err.getMessage());
            activated[0] = true;
        });

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testCaughtConsumerDoesNotApplyIfThrowableAbsent() {
        final Exceptional<String> exceptional = Exceptional.empty();

        final boolean[] activated = { false };
        exceptional.caught(err -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testCauseReturnsValueIfPresent() throws Exception {
        final Exceptional<String> exceptional = Exceptional.of("value");
        final String value = exceptional.orThrow(Exception::new);

        Assertions.assertNotNull(value);
        Assertions.assertEquals("value", value);
    }

    @Test
    void testCauseThrowsExceptionIfValueAbsent() {
        Assertions.assertThrows(Exception.class, () -> Exceptional.empty().orThrow(Exception::new));
    }

    @Test
    void testEmptyRunnableAppliesIfThrowableAbsent() {
        final Exceptional<String> exceptional = Exceptional.empty();

        final boolean[] activated = { false };
        exceptional.ifErrorAbsent(() -> activated[0] = true);

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testEmptyRunnableDoesNotApplyIfThrowablePresent() {
        final Exceptional<String> exceptional = Exceptional.of(new Exception());

        final boolean[] activated = { false };
        exceptional.ifErrorAbsent(() -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testRethrowThrowsWrappedException() {
        final Exceptional<String> exceptional = Exceptional.of(new Exception("error"));

        try {
            exceptional.rethrowUnchecked();
            Assertions.fail();
        }
        catch (final Throwable t) {
            Assertions.assertTrue(t instanceof Exception);
            Assertions.assertEquals("error", t.getMessage());
        }
    }

    @Test
    void testTypeReturnsValueTypeIfPresent() {
        final Exceptional<String> exceptional = Exceptional.of("value");

        Assertions.assertNotNull(exceptional.type());
        Assertions.assertEquals(String.class, exceptional.type());
    }

    @Test
    void testTypeReturnsNullIfValueAbsent() {
        final Exceptional<String> exceptional = Exceptional.empty();
        Assertions.assertNull(exceptional.type());
    }

    @Test
    void testEqualReturnsTrueIfValueIsEqual() {
        final Exceptional<String> exceptional = Exceptional.of("value");
        Assertions.assertTrue(exceptional.equal("value"));
    }

    @Test
    void testEqualReturnsFalseIfValueIsAbsent() {
        final Exceptional<String> exceptional = Exceptional.empty();
        Assertions.assertFalse(exceptional.equal("value"));
    }

    @Test
    void testEqualReturnsFalseIfValueIsDifferent() {
        final Exceptional<String> exceptional = Exceptional.of("value");
        Assertions.assertFalse(exceptional.equal("other"));
    }
}
