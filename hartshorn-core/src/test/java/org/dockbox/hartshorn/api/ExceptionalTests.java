/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.api;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ExceptionalTests {

    @Test
    void testCanCreateFromFilledOptional() {
        Optional<String> optional = Optional.of("value");
        Exceptional<String> exceptional = Exceptional.of(optional);

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertEquals("value", exceptional.get());
    }

    @Test
    void testCanCreateFromEmptyOptional() {
        Optional<String> optional = Optional.empty();
        Exceptional<String> exceptional = Exceptional.of(optional);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
    }

    @Test
    void testCanCreateFromNonNullCallable() {
        Callable<String> callable = () -> "value";
        Exceptional<String> exceptional = Exceptional.of(callable);

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertEquals("value", exceptional.get());
    }

    @Test
    void testCanCreateFromNullCallable() {
        Callable<String> callable = () -> null;
        Exceptional<String> exceptional = Exceptional.of(callable);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
    }

    @Test
    void testCanCreateFromThrowingCallable() {
        Callable<String> callable = () -> {
            throw new Exception();
        };
        Exceptional<String> exceptional = Exceptional.of(callable);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
        Assertions.assertTrue(exceptional.caught());
    }

    @Test
    void testCanCreateFromValue() {
        Exceptional<String> exceptional = Exceptional.of("value");

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertEquals("value", exceptional.get());
    }

    @Test
    void testCanCreateFromNull() {
        Exceptional<String> exceptional = Exceptional.of((String) null);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
    }

    @Test
    void testCanCreateFromException() {
        Exceptional<String> exceptional = Exceptional.of(new Exception());

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
        Exceptional<String> exceptional = Exceptional.of("value", new Exception());

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertTrue(exceptional.caught());
    }

    @Test
    void testCanCreateFromNullValueAndException() {
        Exceptional<String> exceptional = Exceptional.of(null, new Exception());

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
        Assertions.assertTrue(exceptional.caught());
    }

    @Test
    void testCanCreateFromNullValueAndNullException() {
        Exceptional<String> exceptional = Exceptional.of(null, null);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
        Assertions.assertFalse(exceptional.caught());
    }

    @Test
    void testCanCreateFromValueAndNullException() {
        Exceptional<String> exceptional = Exceptional.of("value", null);

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertFalse(exceptional.caught());
    }

    @Test
    void testCanCreateConditionalTrue() {
        Exceptional<String> exceptional = Exceptional.of(() -> true, () -> "value", Exception::new);

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertFalse(exceptional.caught());
    }

    @Test
    void testCanCreateConditionalFalse() {
        Exceptional<String> exceptional = Exceptional.of(() -> false, () -> "value", Exception::new);

        Assertions.assertFalse(exceptional.present());
        Assertions.assertTrue(exceptional.absent());
        Assertions.assertTrue(exceptional.caught());
    }

    @Test
    void testNoneContainsNothing() {
        Exceptional<String> exceptional = Exceptional.empty();

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
        Exceptional<String> exceptional = Exceptional.of("value");

        Assertions.assertTrue(exceptional.present());
        Assertions.assertFalse(exceptional.absent());
        Assertions.assertFalse(exceptional.caught());
        Assertions.assertEquals("value", exceptional.get());
    }

    @Test
    void testGetWithSupplierReturnsDefaultValueIfAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();
        String value = exceptional.get(() -> "other");

        Assertions.assertNotNull(value);
        Assertions.assertEquals("other", value);
    }

    @Test
    void testPresentConsumerActivatesIfValueIsPresent() {
        Exceptional<String> exceptional = Exceptional.of("value");

        final boolean[] activated = { false };
        exceptional.present(value -> {
            Assertions.assertEquals("value", value);
            activated[0] = true;
        });

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testPresentConsumerDoesNotActivateIfAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();

        final boolean[] activated = { false };
        exceptional.present(value -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testAbsentConsumerActivatesIfValueIsAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();

        final boolean[] activated = { false };
        exceptional.absent(() -> activated[0] = true);

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testAbsentConsumerDoesNotActivateIfValueIsPresent() {
        Exceptional<String> exceptional = Exceptional.of("value");

        final boolean[] activated = { false };
        exceptional.absent(() -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testOrNullReturnsNullIfAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();
        Assertions.assertNull(exceptional.orNull());
    }

    @Test
    void testOrNullReturnsValueIfPresent() {
        Exceptional<String> exceptional = Exceptional.of("value");

        Assertions.assertNotNull(exceptional.orNull());
        Assertions.assertEquals("value", exceptional.orNull());
    }

    @Test
    void testOrReturnsValueIfPresent() {
        Exceptional<String> exceptional = Exceptional.of("value");
        Assertions.assertEquals("value", exceptional.or("other"));
    }

    @Test
    void testOrReturnsOtherIfAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();
        Assertions.assertEquals("other", exceptional.or("other"));
    }

    @Test
    void testOrReturnsThrowableIfPresent() {
        Exceptional<String> exceptional = Exceptional.of(new Exception("error"));
        Assertions.assertEquals("error", exceptional.or(new Exception("other")).getMessage());
    }

    @Test
    void testOrReturnsOtherIfThrowableAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();
        Assertions.assertEquals("other", exceptional.or(new Exception("other")).getMessage());
    }

    @Test
    void testThenDoesNotApplyIfValueAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();
        exceptional = exceptional.orElse(value -> Exceptional.of("other"));

        Assertions.assertNotNull(exceptional);
        Assertions.assertFalse(exceptional.present());
    }

    @Test
    void testThenAppliesIfValuePresent() {
        Exceptional<String> exceptional = Exceptional.of("value");
        exceptional = exceptional.orElse(value -> Exceptional.of("other"));

        Assertions.assertNotNull(exceptional);
        Assertions.assertTrue(exceptional.present());
        Assertions.assertEquals("other", exceptional.get());
    }

    @Test
    void testThenWithThrowableDoesNotApplyIfValueAbsent() {
        Exceptional<String> exceptional = Exceptional.of(new Exception());
        exceptional = exceptional.orElse((value, err) -> Exceptional.of("other"));

        Assertions.assertNotNull(exceptional);
        Assertions.assertFalse(exceptional.present());
    }

    @Test
    void testThenWithThrowableAppliesIfValuePresent() {
        Exceptional<String> exceptional = Exceptional.of("value", new Exception("error"));
        exceptional = exceptional.orElse((value, err) -> {
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
        Exceptional<String> exceptional = Exceptional.of(new Exception("error"));

        final boolean[] activated = { false };
        exceptional.caught(err -> {
            Assertions.assertEquals("error", err.getMessage());
            activated[0] = true;
        });

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testCaughtConsumerDoesNotApplyIfThrowableAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();

        final boolean[] activated = { false };
        exceptional.caught(err -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testCauseReturnsValueIfPresent() throws Exception {
        Exceptional<String> exceptional = Exceptional.of("value");
        String value = exceptional.cause(Exception::new);

        Assertions.assertNotNull(value);
        Assertions.assertEquals("value", value);
    }

    @Test
    void testCauseThrowsExceptionIfValueAbsent() {
        Assertions.assertThrows(Exception.class, () -> Exceptional.empty().cause(Exception::new));
    }

    @Test
    void testEmptyRunnableAppliesIfThrowableAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();

        final boolean[] activated = { false };
        exceptional.ifErrorAbsent(() -> activated[0] = true);

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testEmptyRunnableDoesNotApplyIfThrowablePresent() {
        Exceptional<String> exceptional = Exceptional.of(new Exception());

        final boolean[] activated = { false };
        exceptional.ifErrorAbsent(() -> activated[0] = true);

        Assertions.assertFalse(activated[0]);
    }

    @Test
    void testRethrowThrowsWrappedException() {
        Exceptional<String> exceptional = Exceptional.of(new Exception("error"));

        try {
            exceptional.rethrow();
            Assertions.fail();
        } catch (Throwable t) {
            Assertions.assertTrue(t instanceof RuntimeException);
            Assertions.assertNotNull(t.getCause());
            Assertions.assertEquals("error", t.getCause().getMessage());
        }
    }

    @Test
    void testTypeReturnsValueTypeIfPresent() {
        Exceptional<String> exceptional = Exceptional.of("value");

        Assertions.assertNotNull(exceptional.type());
        Assertions.assertEquals(String.class, exceptional.type());
    }

    @Test
    void testTypeReturnsNullIfValueAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();
        Assertions.assertNull(exceptional.type());
    }

    @Test
    void testEqualReturnsTrueIfValueIsEqual() {
        Exceptional<String> exceptional = Exceptional.of("value");
        Assertions.assertTrue(exceptional.equal("value"));
    }

    @Test
    void testEqualReturnsFalseIfValueIsAbsent() {
        Exceptional<String> exceptional = Exceptional.empty();
        Assertions.assertFalse(exceptional.equal("value"));
    }

    @Test
    void testEqualReturnsFalseIfValueIsDifferent() {
        Exceptional<String> exceptional = Exceptional.of("value");
        Assertions.assertFalse(exceptional.equal("other"));
    }
}
