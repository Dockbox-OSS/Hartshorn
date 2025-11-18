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

import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.NotPrimitiveException;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.types.TypeConversionException;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class UtilitiesTests {

    private static final int minute = 60;
    private static final int hour = 60 * minute;
    private static final int day = 24 * hour;
    private static final int week = 7 * day;

    private static Stream<Arguments> capitalizeValues() {
        return Stream.of(
                Arguments.of("value", "Value"),
                Arguments.of("Value", "Value"),
                Arguments.of("VALUE", "VALUE"),
                Arguments.of(" value", " value")
        );
    }

    private static Stream<Arguments> durations() {
        return Stream.of(
                Arguments.of("1", 1),
                Arguments.of("1s", 1),
                Arguments.of("1m", minute),
                Arguments.of("1h", hour),
                Arguments.of("1d", day),
                Arguments.of("1w", week),
                Arguments.of("1w1d1h1m1s", week + day + hour + minute + 1),
                Arguments.of("2w3d", (2 * week) + (3 * day)),
                Arguments.of("2w3d5h", (2 * week) + (3 * day) + (5 * hour)),
                Arguments.of("17h21m13s", (17 * hour) + (21 * minute) + 13),
                Arguments.of("123456", 123456)
        );
    }

    public static Stream<Arguments> differences() {
        return Stream.of(
                Arguments.of(List.of("a", "b"), List.of("a"), List.of("b")),
                Arguments.of(List.of("a"), List.of("a", "b"), List.of("b"))
        );
    }

    public static Stream<Arguments> stringFormats() {
        return Stream.of(
                Arguments.of("Hello world!", "Hello world!", new Object[0]),
                Arguments.of("Hello %s!", "Hello %s!", new Object[]{ "world" }),
                Arguments.of("Hello {0}!", "Hello world!", new Object[]{ "world" }),
                Arguments.of("{0} {1}!", "Hello world!", new Object[]{ "Hello", "world" }),
                Arguments.of("{0} {0}!", "Hello Hello!", new Object[]{ "Hello", "world" })
        );
    }

    public static Stream<Arguments> stringMapFormats() {
        return Stream.of(
                Arguments.of("Hello world!", "Hello world!", Map.of()),
                Arguments.of("Hello %s!", "Hello %s!", Map.of("{0}", "world")),
                Arguments.of("Hello {0}!", "Hello world!", Map.of("{0}", "world")),
                Arguments.of("{0} {1}!", "Hello world!", Map.of("{0}", "Hello", "{1}", "world")),
                Arguments.of("{0} {0}!", "Hello Hello!", Map.of("{0}", "Hello", "{1}", "world")),
                Arguments.of("Hello world!", "Hello user!", Map.of("world", "user"))
        );
    }

    private static <T, R> Function<T, R> asFunction(Function<T, R> function) {
        return function;
    }

    public static Stream<Arguments> stringJoinValues() {
        return Stream.of(
                Arguments.of(", ", List.of(1, 2, 3), UtilitiesTests.asFunction(String::valueOf), "1, 2, 3"),
                Arguments.of("|", List.of("A", "b", "c"), UtilitiesTests.<String, String>asFunction(String::toUpperCase), "A|B|C"),
                Arguments.of("", List.of(""), UtilitiesTests.asFunction(String::valueOf), "")
        );
    }

    @ParameterizedTest
    @MethodSource("capitalizeValues")
    void capitalizeChangesOnlyFirstCharacter(String input, String expected) {
        String value = StringUtilities.capitalize(input);
        assertThat(value)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    void capitalizeAcceptsEmptyValue() {
        String value = assertThatCode(() -> StringUtilities.capitalize("")).doesNotThrowAnyException();
        assertThat(value).isNotNull();
        assertThat(value).isEmpty();
    }

    @Test
    void isEmptyStringTrueIfNull() {
        assertThat(StringUtilities.empty(null)).isTrue();
    }

    @Test
    void isEmptyStringTrueIfEmpty() {
        assertThat(StringUtilities.empty("")).isTrue();
    }

    @Test
    void isEmptyStringFalseIfContent() {
        assertThat(StringUtilities.empty("value")).isFalse();
    }

    @Test
    void isNotEmptyStringFalseIfNull() {
        assertThat(StringUtilities.notEmpty(null)).isFalse();
    }

    @Test
    void isNotEmptyStringFalseIfEmpty() {
        assertThat(StringUtilities.notEmpty("")).isFalse();
    }

    @Test
    void isNotEmptyStringTrueIfContent() {
        assertThat(StringUtilities.notEmpty("value")).isTrue();
    }

    @Test
    void stripReplacesAllSpaces() {
        String stripped = StringUtilities.strip(" val ue  ");
        assertThat(stripped)
                .isNotNull()
                .isEqualTo("value");
    }

    @Test
    void stripReplacesAllTabs() {
        String stripped = StringUtilities.strip("\tval\tue\t\t");
        assertThat(stripped)
                .isNotNull()
                .isEqualTo("value");
    }

    @Test
    void stripReplacesAllNewLines() {
        String stripped = StringUtilities.strip("\nval\nue\n\n");
        assertThat(stripped)
                .isNotNull()
                .isEqualTo("value");
    }

    @Test
    void stripReplacesAllCarriageReturns() {
        String stripped = StringUtilities.strip("\rval\rue\r\r");
        assertThat(stripped)
                .isNotNull()
                .isEqualTo("value");
    }

    @Test
    void trimWithSpaces() {
        String trimmed = StringUtilities.trimWith(' ', " value  ");
        assertThat(trimmed)
                .isNotNull()
                .isEqualTo("value");
    }

    @Test
    void trimWithRegExCharacter() {
        String trimmed = StringUtilities.trimWith('$', "$value$$");
        assertThat(trimmed)
                .isNotNull()
                .isEqualTo("value");
    }

    @Test
    void collectionMerge() {
        Collection<Integer> col1 = Arrays.asList(1, 2, 3);
        Collection<Integer> col2 = Arrays.asList(4, 5, 6);
        Collection<Integer> merged = CollectionUtilities.merge(col1, col2);

        assertThat(merged)
                .hasSize(6)
                .containsAll(Arrays.asList(1, 2, 3, 4, 5, 6));
    }

    @ParameterizedTest
    @MethodSource("durations")
    void durationOf(String in, long expected) {
        Option<Duration> duration = StringUtilities.durationOf(in);
        assertThat(duration.present()).isTrue();
        assertThat(duration.get()).hasSeconds(expected);
    }

    @Test
    void durationOfWithInvalidValue() {
        Option<Duration> duration = StringUtilities.durationOf("NotAValidDurationString");
        assertThat(duration.present()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("differences")
    void differenceInCollections(Collection<String> collectionOne, Collection<String> collectionTwo, Collection<String> expected) {
        Set<String> difference = CollectionUtilities.difference(collectionOne, collectionTwo);
        assertThat(expected).hasSameSizeAs(difference);
        assertThat(difference).containsAll(expected);
        assertThat(expected).containsAll(difference);
    }

    @Test
    void splitCapitals() {
        String input = "ThisIsAString";
        String[] expected = { "This", "Is", "A", "String" };
        String[] actual = StringUtilities.splitCapitals(input);
        assertThat(actual).containsExactly(expected);
    }

    @ParameterizedTest
    @MethodSource("stringFormats")
    void format(String format, String expected, Object... args) {
        String actual = StringUtilities.format(format, args);
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("stringMapFormats")
    void mapFormat(String format, String expected, Map<String, String> replacements) {
        String actual = StringUtilities.format(format, replacements);
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("stringJoinValues")
    <T> void join(String delimiter, Iterable<T> elements, Function<T, String> toStringFunction, String expected) {
        String joined = StringUtilities.join(delimiter, elements, toStringFunction);
        assertThat(joined).isEqualTo(expected);
    }

    public static Stream<Arguments> stringsToPrimitives() {
        return Stream.of(
                Arguments.of("1", int.class, 1),
                Arguments.of("1", long.class, 1L),
                Arguments.of("1", short.class, (short) 1),
                Arguments.of("1", byte.class, (byte) 1),
                Arguments.of("1", float.class, 1.0f),
                Arguments.of("1", double.class, 1.0d),
                Arguments.of("true", boolean.class, true),
                Arguments.of("1", char.class, '1'),
                Arguments.of("ONE", TestEnum.class, TestEnum.ONE),
                Arguments.of("TWO", TestEnum.class, TestEnum.TWO),
                Arguments.of("THREE", TestEnum.class, TestEnum.THREE)
        );
    }

    public static Stream<Arguments> invalidStringsToPrimitives() {
        return Stream.of(
                Arguments.of("one", int.class),
                Arguments.of("one", long.class),
                Arguments.of("one", short.class),
                Arguments.of("one", byte.class),
                Arguments.of("one", float.class),
                Arguments.of("one", double.class),
                Arguments.of("yes", boolean.class),
                Arguments.of("too long", char.class),
                Arguments.of("FOUR", TestEnum.class)
        );
    }

    @ParameterizedTest
    @MethodSource("stringsToPrimitives")
    <T> void testToPrimitive(String input, Class<T> type, T expected) {
        T actual = TypeUtils.toPrimitive(type, input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toPrimitiveDoesNotAcceptObjects() {
        assertThatExceptionOfType(NotPrimitiveException.class).isThrownBy(() -> TypeUtils.toPrimitive(Object.class, "value"));
        assertThatExceptionOfType(NotPrimitiveException.class).isThrownBy(() -> TypeUtils.toPrimitive(String.class, "value"));
        assertThatExceptionOfType(NotPrimitiveException.class).isThrownBy(() -> TypeUtils.toPrimitive(List.class, "value"));
    }

    @ParameterizedTest
    @MethodSource("invalidStringsToPrimitives")
    <T> void toPrimitiveThrowsOnInvalidInput(String input, Class<T> type) {
        assertThatExceptionOfType(TypeConversionException.class).isThrownBy(() -> TypeUtils.toPrimitive(type, input));
    }

    @Test
    void validWildcardAdjustment() {
        List<?> list = Arrays.asList("one", "two", "three");
        List<String> adjusted = assertThatCode(() -> TypeUtils.unchecked(list, List.class)).doesNotThrowAnyException();
        assertThat(adjusted)
                .isNotNull()
                .isSameAs(list);
    }

    @Test
    void wildcardAdjustmentDoesAdjustParent() {
        List<?> list = Arrays.asList("one", "two", "three");
        List<String> adjusted = assertThatCode(() -> TypeUtils.unchecked(list, Collection.class)).doesNotThrowAnyException();
        assertThat(adjusted)
                .isNotNull()
                .isSameAs(list);
    }

    @Test
    void annotationCreatesEmptyAnnotation() {
        TestAnnotation annotation = TypeUtils.annotation(TestAnnotation.class);
        assertThat(annotation).isNotNull();
    }

    @Test
    void annotationCreatesAnnotationWithValues() {
        TestAnnotationWithValue annotation = TypeUtils.annotation(TestAnnotationWithValue.class, Map.of("value", "test"));
        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEqualTo("test");
    }

    @Test
    void annotationValidatesValues() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> TypeUtils.annotation(TestAnnotationWithValue.class, Map.of("value", 1)));
    }

    private @interface TestAnnotation {
    }

    private @interface TestAnnotationWithValue {
        String value();
    }

    private enum TestEnum {
        ONE,
        TWO,
        THREE
    }
}
