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

package org.dockbox.hartshorn.util;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.option.Option;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of utility methods for working with strings.
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public final class StringUtilities {

    /**
     * Pattern for matching a string that represents a duration in seconds. The string must be a
     * single unsigned long.
     */
    private static final Pattern MINOR_TIME_STRING = Pattern.compile("^\\d+$");

    /**
     * Pattern for matching a string that represents a complex duration according to a simplified
     * ISO-8601 format. The string must be a sequence of one or more of the following:
     * <ul>
     *     <li>Unsigned long followed by 'w' for weeks</li>
     *     <li>Unsigned long followed by 'd' for days</li>
     *     <li>Unsigned long followed by 'h' for hours</li>
     *     <li>Unsigned long followed by 'm' for minutes</li>
     *     <li>Unsigned long followed by 's' for seconds</li>
     * </ul>
     * The string must not contain any other characters, including whitespace.
     *
     * <p>As this is a simplified format, months and years are not supported.
     *
     * <p>Examples:
     * <ul>
     *     <li>1w: 1 week</li>
     *     <li>1w2d: 1 week, 2 days</li>
     *     <li>1w2d3h: 1 week, 2 days, 3 hours</li>
     *     <li>1w2d3h4m: 1 week, 2 days, 3 hours, 4 minutes</li>
     *     <li>1w2d3h4m5s: 1 week, 2 days, 3 hours, 4 minutes, 5 seconds</li>
     *     <li>1d4m: 1 day, 4 minutes</li>
     * </ul>
     */
    private static final Pattern DURATION_PATTERN = Pattern.compile(
        "^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$"
    );
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 60 * StringUtilities.SECONDS_IN_MINUTE;
    private static final int SECONDS_IN_DAY = 24 * StringUtilities.SECONDS_IN_HOUR;
    private static final int SECONDS_IN_WEEK = 7 * StringUtilities.SECONDS_IN_DAY;

    private StringUtilities() {
        // Utility class
    }

    /**
     * Capitalizes the first letter of the given string. If the string is empty, the string is
     * returned as-is.
     *
     * @param value the string to capitalize
     *
     * @return the capitalized string
     */
    public static String capitalize(String value) {
        return StringUtilities.empty(value)
            ? value
            : (value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1));
    }

    /**
     * Returns {@code true} if the given string is {@code null} or empty.
     *
     * @param value the string to check
     *
     * @return {@code true} if the given string is {@code null} or empty
     */
    public static boolean empty(CharSequence value) {
        return null == value || value.isEmpty();
    }

    /**
     * Returns {@code true} if the given string is not {@code null} and not empty.
     *
     * @param value the string to check
     *
     * @return {@code true} if the given string is not {@code null} and not empty
     */
    public static boolean notEmpty(CharSequence value) {
        return null != value && !value.isEmpty();
    }

    /**
     * Returns an empty string if the given string is {@code null}, otherwise returns the given
     * string.
     *
     * @param value the string to check
     *
     * @return an empty string if the given string is {@code null}, otherwise returns the given
     * string
     */
    public static String emptyIfNull(String value) {
        return null == value ? "" : value;
    }

    /**
     * Returns {@code null} if the given string is empty, otherwise returns the given string.
     *
     * @param value the string to check
     *
     * @return {@code null} if the given string is empty, otherwise returns the given string.
     */
    public static String nullIfEmpty(String value) {
        return StringUtilities.empty(value) ? null : value;
    }

    /**
     * Strips all whitespace from the given string. This includes newlines, carriage returns, tabs,
     * and spaces.
     *
     * @param value the string to strip
     *
     * @return the stripped string
     */
    public static String strip(String value) {
        return value.replaceAll("[\n\r\t ]+", "").trim();
    }

    /**
     * Returns a second-precision duration representing the given string. The string must be an
     * unsigned long if only seconds are represented, or a sequence of one or more of the
     * following:
     * <ul>
     *     <li>Unsigned long followed by 'w' for weeks</li>
     *     <li>Unsigned long followed by 'd' for days</li>
     *     <li>Unsigned long followed by 'h' for hours</li>
     *     <li>Unsigned long followed by 'm' for minutes</li>
     *     <li>Unsigned long followed by 's' for seconds</li>
     * </ul>
     * The string must not contain any other characters, including whitespace.
     *
     * @param value the string to parse
     *
     * @return a {@link Duration} representing the given string, or {@link Option#empty()} if the
     * string could not be parsed
     */
    public static Option<Duration> durationOf(String value) {
        // First, if just digits, return the number in seconds.

        if (StringUtilities.MINOR_TIME_STRING.matcher(value).matches()) {
            return Option.of(Duration.ofSeconds(Long.parseUnsignedLong(value)));
        }

        Matcher m = StringUtilities.DURATION_PATTERN.matcher(value);
        if (m.matches()) {
            long time = StringUtilities.durationAmount(m.group(2), StringUtilities.SECONDS_IN_WEEK);
            time += StringUtilities.durationAmount(m.group(4), StringUtilities.SECONDS_IN_DAY);
            time += StringUtilities.durationAmount(m.group(6), StringUtilities.SECONDS_IN_HOUR);
            time += StringUtilities.durationAmount(m.group(8), StringUtilities.SECONDS_IN_MINUTE);
            time += StringUtilities.durationAmount(m.group(10), 1);

            if (0 < time) {
                return Option.of(Duration.ofSeconds(time));
            }
        }
        return Option.empty();
    }

    private static long durationAmount(@Nullable String value, int multiplier) {
        if (null != value && !value.isEmpty()) {
            return multiplier * Long.parseUnsignedLong(value);
        }
        return 0;
    }

    /**
     * Splits the given string into an array of strings, using each capital letter as a delimiter.
     * The capital letters are included in the resulting strings.
     *
     * <p>Examples:
     * <ul>
     *     <li>"HelloWorld" -> ["Hello", "World"]</li>
     *     <li>"HelloW" -> ["Hello", "W"]</li>
     *     <li>"Hello" -> ["Hello"]</li>
     * </ul>
     *
     * @param value the string to split
     *
     * @return an array of strings, using each capital letter as a delimiter
     */
    public static String[] splitCapitals(String value) {
        return value.split("(?=\\p{Lu})");
    }

    /**
     * Trims the given string of all leading and trailing characters matching the given character.
     *
     * <p>Examples:
     * <ul>
     *     <li>trimWith(' ', " value  ") -> "value"</li>
     *     <li>trimWith('$', "$value$$") -> "value"</li>
     * </ul>
     *
     * @param trimCharacter the character to trim
     * @param value the string to trim
     *
     * @return the trimmed string
     */
    public static String trimWith(char trimCharacter, String value) {
        int length = value.length();
        int currentIndex = 0;
        char[] characters = value.toCharArray();

        while ((currentIndex < length) && (characters[currentIndex] <= trimCharacter)) {
            currentIndex++;
        }
        while ((currentIndex < length) && (characters[length - 1] <= trimCharacter)) {
            length--;
        }
        return ((currentIndex > 0) || (length < value.length()))
            ? value.substring(currentIndex, length)
            : value;
    }

    /**
     * Formats the given string using the given arguments. The string must contain placeholders in
     * the form of {@code {0}}, {@code {1}}, etc. The placeholders are replaced with the given
     * arguments in the order they are provided.
     *
     * <p>If the string contains a placeholder that is not present in the given arguments, the
     * placeholder is left as-is. If the arguments contains an index that has no corresponding
     * placeholder in the string, the argument is ignored.
     *
     * <p>Examples:
     * <ul>
     *     <li>{@code "Hello {0}", "world"} -> {@code "Hello world"}</li>
     *     <li>
     *         {@code "Hello {0}", "world", "!"} -> {@code "Hello world"}
     *         (ignores value {@code "!"})
     *     </li>
     *     <li>
     *         {@code "{0} {1}", "Hello"} -> {@code "Hello {1}"}
     *         (ignores placeholder {@code {1}})
     *     </li>
     * </ul>
     *
     * @param format the string to format
     * @param args the arguments to use for formatting
     *
     * @return the formatted string
     */
    public static String format(String format, Object... args) {
        if (0 == args.length) {
            return format;
        }
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String arg = String.valueOf(args[i]);
            map.put(String.format("{%d}", i), arg);
        }
        return StringUtilities.format(format, map);
    }

    /**
     * Formats the given string using the given replacements. The string must contain placeholders
     * matching the keys in the given map. The placeholders are replaced with the corresponding
     * values in the map. If the map contains a key that has no corresponding placeholder in the
     * string, the key is ignored.
     *
     * <p>Examples:
     * <ul>
     *     <li>{@code "Hello {name}", {"name", "world"}} -> {@code "Hello world"}</li>
     *     <li>
     *         {@code "Hello {name}", {"name", "world"}, {"!", "!"}} -> {@code "Hello world"}
     *         (ignores value {@code "!"})
     *     </li>
     *     <li>
     *         {@code "{name} {exclamation}", {"name", "Hello"}} -> {@code "Hello {exclamation}"}
     *         (ignores placeholder {@code {exclamation}})
     *     </li>
     * </ul>
     *
     * @param string the string to format
     * @param replacements the replacements to use for formatting
     *
     * @return the formatted string
     */
    public static String format(String string, Map<String, String> replacements) {
        StringBuilder sb = new StringBuilder(string);
        int size = string.length();
        for (Entry<String, String> entry : replacements.entrySet()) {
            if (0 == size) {
                break;
            }
            String key = entry.getKey();
            String value = entry.getValue();
            int nextSearchStart;
            int start = sb.indexOf(key, 0);
            while (-1 < start) {
                int end = start + key.length();
                nextSearchStart = start + value.length();
                sb.replace(start, end, value);
                size -= end - start;
                start = sb.indexOf(key, nextSearchStart);
            }
        }
        return sb.toString();
    }

    /**
     * Tests if the given raw string matches the given formatted string. The raw string may contain
     * placeholders in the form of {@code {0}}, {@code {1}}, etc. This is a matcher for strings that
     * are typically formatted with {@link #format(String, Object...)}.
     *
     * @param raw the raw string with placeholders
     * @param formatted the formatted string to match against
     * @return {@code true} if the raw string matches the formatted string, {@code false} otherwise
     */
    public static boolean matchesFormatted(String raw, String formatted) {
        String regex = Pattern.quote(raw);
       regex = regex.replaceAll("\\{\\d+}", "(.+?)");
        regex = regex.replaceAll("\\\\Q(.+?)\\\\E", "$1");
        return Pattern.compile("^" + regex + "$", Pattern.DOTALL).matcher(formatted).matches();
    }

    /**
     * Joins the given elements into a string, separated by the given delimiter. The elements are
     * converted to strings using the given function. If the given collection is empty, an empty
     * string is returned.
     *
     * <p>Examples:
     * <ul>
     *     <li>{@code join(", ", Arrays.asList(1, 2, 3), String::valueOf)}
     *     -> {@code "1, 2, 3"}</li>
     *     <li>{@code join(", ", Arrays.asList("a", "b", "c"), String::toUpperCase)}
     *     -> {@code "A, B, C"}</li>
     * </ul>
     *
     * @param delimiter the delimiter to use
     * @param elements the elements to join
     * @param toStringFunction the function to convert elements to strings
     * @param <T> the type of elements to join
     *
     * @return the joined string
     */
    public static <T> String join(
        String delimiter,
        Iterable<T> elements,
        Function<T, String> toStringFunction
    ) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (T element : elements) {
            joiner.add(toStringFunction.apply(element));
        }
        return joiner.toString();
    }

    /**
     * @return a new {@link MatrixBuilder} instance
     *
     * @see MatrixBuilder
     */
    public static MatrixBuilder matrix() {
        return new MatrixBuilder();
    }

    /**
     * A builder for creating a matrix of strings. The matrix is built by adding segments, where
     * each segment is a collection of strings. The resulting matrix contains all possible
     * combinations of the segments.
     *
     * <p>For example, given the segments:
     * <ul>
     *     <li>{@code ["a", "b"]}</li>
     *     <li>{@code ["1", "2"]}</li>
     *     <li>{@code ["X", "Y"]}</li>
     * </ul>
     * The resulting matrix will be:
     * <ul>
     *     <li>{@code "a1X"}</li>
     *     <li>{@code "a1Y"}</li>
     *     <li>{@code "a2X"}</li>
     *     <li>{@code "a2Y"}</li>
     *     <li>{@code "b1X"}</li>
     *     <li>{@code "b1Y"}</li>
     *     <li>{@code "b2X"}</li>
     *     <li>{@code "b2Y"}</li>
     * </ul>
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public static class MatrixBuilder {

        record Segment(Collection<String> values, boolean optional) {}

        private final List<Segment> segments = new ArrayList<>();

        /**
         * Adds a segment to the matrix.
         *
         * @param segment the segment to add
         *
         * @return this {@link MatrixBuilder} instance
         */
        public MatrixBuilder segment(Collection<String> segment) {
            this.segments.add(new Segment(segment, false));
            return this;
        }

        /**
         * Adds a segment to the matrix.
         *
         * @param segment the segment to add
         *
         * @return this {@link MatrixBuilder} instance
         */
        public MatrixBuilder segment(String... segment) {
            return this.segment(List.of(segment));
        }

        public MatrixBuilder optionalSegment(Collection<String> segment) {
            this.segments.add(new Segment(segment, true));
            return this;
        }

        public MatrixBuilder optionalSegment(String... segment) {
            return this.optionalSegment(List.of(segment));
        }

        /**
         * Builds the matrix and returns the resulting list of strings. Each string in the resulting
         * list is a unique combination of the segments added to the builder.
         *
         * @return the resulting list of strings
         */
        public List<String> build() {
            List<String> result = new ArrayList<>();
            this.generate(result, "", 0);
            return result;
        }

        private void generate(
            List<String> results,
            String current,
            int depth
        ) {
            if (depth >= this.segments.size()) {
                results.add(current);
                return;
            }

            Segment segment = this.segments.get(depth);
            for (String value : segment.values) {
                this.generate(results, current + value, depth + 1);
            }
            if (segment.optional) {
                this.generate(results, current, depth + 1);
            }
        }
    }
}
