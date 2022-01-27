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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.domain.tuple.Vector3N;
import org.dockbox.hartshorn.core.function.CheckedRunnable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Wraps all utility classes to a common accessor. This way all {@code final} utility classes can be
 * accessed at once and indexed more easily.
 */
public final class HartshornUtils {

    private static final Random random = new Random();
    private static final char[] _hex = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    private static final java.util.regex.Pattern minorTimeString = java.util.regex.Pattern.compile("^\\d+$");
    private static final java.util.regex.Pattern timeString = java.util.regex.Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");
    private static final int secondsInMinute = 60;
    private static final int secondsInHour = 60 * HartshornUtils.secondsInMinute;
    private static final int secondsInDay = 24 * HartshornUtils.secondsInHour;
    private static final int secondsInWeek = 7 * HartshornUtils.secondsInDay;
    /**
     * The globally 'empty' unique ID which can be used for empty implementations of {@link
     * org.dockbox.hartshorn.core.domain.Identifiable}.
     */
    public static UUID EMPTY_UUID = UUID.fromString("00000000-1111-2222-3333-000000000000");

    private HartshornUtils() {}

    /**
     * Constructs a new unique map from a given set of {@link Entry entries}. If no entries are
     * provided an empty {@link Map} is returned. The constructed map is not concurrent.
     * Entries can easily be created using {@link org.dockbox.hartshorn.core.domain.tuple.Tuple#of(Object, Object)}
     *
     * @param <K> The (super)type of all keys in the entry set
     * @param <V> The (super)type of all values in the entry set
     * @param entries The entries to use while constructing a new map
     *
     * @return The new non-concurrent map
     * @throws NullPointerException If an entry is null
     * @see org.dockbox.hartshorn.core.domain.tuple.Tuple#of(Object, Object)
     */
    @SafeVarargs
    public static <K, V> Map<K, V> ofEntries(final Entry<? extends K, ? extends V>... entries) {
        if (0 == entries.length) { // implicit null check of entries array
            return new HashMap<>();
        }
        else {
            final Map<K, V> map = new HashMap<>();
            for (final Entry<? extends K, ? extends V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        }
    }

    public static <T> Set<T> emptyConcurrentSet() {
        return ConcurrentHashMap.newKeySet();
    }

    @NonNull
    @SafeVarargs
    public static <T> List<T> asList(final T... objects) {
        return new ArrayList<>(Arrays.asList(objects));
    }

    public static String capitalize(final String value) {
        return HartshornUtils.empty(value)
                ? value
                : (value.substring(0, 1).toUpperCase() + value.substring(1));
    }

    public static boolean empty(final String value) {
        return null == value || value.isEmpty();
    }

    // Both start and end are inclusive
    public static <T> T[] arraySubset(final T[] array, final int start, final int end) {
        return Arrays.copyOfRange(array, start, end + 1);
    }

    public static String contentOrEmpty(@NonNull final Path file) {
        try {
            return Files.readString(file);
        }
        catch (final IOException ignored) {
            return "";
        }
    }

    /**
     * Merge t [ ].
     *
     * @param arrayOne the array one
     * @param arrayTwo the array two
     *
     * @return the t [ ]
     */
    public static <T> T[] merge(final T[] arrayOne, final T[] arrayTwo) {
        final Set<T> merged = new HashSet<>();
        merged.addAll(Set.of(arrayOne));
        merged.addAll(Set.of(arrayTwo));
        return merged.toArray(arrayOne);
    }

    public static boolean notEmpty(final String value) {
        return null != value && !value.isEmpty();
    }

    public static String strip(final String s) {
        return s.replaceAll("[\n\r ]+", "").trim();
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} does not throw any type of exception
     * when ran. Acts as inverse of {@link HartshornUtils#throwsException(CheckedRunnable)}.
     *
     * @param runnable The function to run
     *
     * @return true if the function does not throw an exception
     * @see HartshornUtils#throwsException(CheckedRunnable)
     */
    public static boolean doesNotThrow(final CheckedRunnable runnable) {
        return !HartshornUtils.throwsException(runnable);
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} throws any type of exception when ran.
     *
     * @param runnable The function to run
     *
     * @return true if the function throws an exception
     */
    public static boolean throwsException(final CheckedRunnable runnable) {
        try {
            runnable.run();
            return false;
        }
        catch (final Throwable t) {
            return true;
        }
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} does not throw a specific type of
     * exception when ran. Acts as inverse of {@link HartshornUtils#throwsException(CheckedRunnable,
     * Class)}.
     *
     * @param runnable The function to run
     * @param exception The expected type of exception
     *
     * @return true if the function does not throw an exception
     * @see HartshornUtils#throwsException(CheckedRunnable, Class)
     */
    public static boolean doesNotThrow(final CheckedRunnable runnable, final Class<? extends Throwable> exception) {
        return !HartshornUtils.throwsException(runnable, exception);
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} throws a specific type of exception
     * when ran.
     *
     * @param runnable The function to run
     * @param exception The expected type of exception
     *
     * @return true if the function throws the expected exception
     */
    public static boolean throwsException(final CheckedRunnable runnable, final Class<? extends Throwable> exception) {
        try {
            runnable.run();
            return false;
        }
        catch (final Throwable t) {
            return exception.isAssignableFrom(t.getClass());
        }
    }

    public static Exceptional<Duration> durationOf(final String in) {
        // First, if just digits, return the number in seconds.

        if (HartshornUtils.minorTimeString.matcher(in).matches()) {
            return Exceptional.of(Duration.ofSeconds(Long.parseUnsignedLong(in)));
        }

        final Matcher m = HartshornUtils.timeString.matcher(in);
        if (m.matches()) {
            long time = HartshornUtils.durationAmount(m.group(2), HartshornUtils.secondsInWeek);
            time += HartshornUtils.durationAmount(m.group(4), HartshornUtils.secondsInDay);
            time += HartshornUtils.durationAmount(m.group(6), HartshornUtils.secondsInHour);
            time += HartshornUtils.durationAmount(m.group(8), HartshornUtils.secondsInMinute);
            time += HartshornUtils.durationAmount(m.group(10), 1);

            if (0 < time) {
                return Exceptional.of(Duration.ofSeconds(time));
            }
        }
        return Exceptional.empty();
    }

    private static long durationAmount(@Nullable final String g, final int multiplier) {
        if (null != g && !g.isEmpty()) {
            return multiplier * Long.parseUnsignedLong(g);
        }

        return 0;
    }

    @SafeVarargs
    public static <T> Collection<T> merge(final Collection<T>... collections) {
        final Collection<T> merged = new HashSet<>();
        for (final Collection<T> collection : collections) {
            merged.addAll(collection);
        }
        return merged;
    }

    @SafeVarargs
    public static <T, R> Object[] all(final Function<T, R> function, final T... input) {
        final Collection<R> out = new ArrayList<>();
        for (final T t : input) {
            out.add(function.apply(t));
        }
        return out.toArray();
    }

    public static <T> Stream<T> stream(final Iterable<T> iterable) {
        return stream(iterable.iterator());
    }

    public static <T> Stream<T> stream(final Iterator<T> tIterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(tIterator, Spliterator.ORDERED), false);
    }

    public static String asTable(final List<List<String>> rows) {
        final int[] maxLengths = new int[rows.get(0).size()];
        for (final List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                maxLengths[i] = Math.max(maxLengths[i], row.get(i).length());
            }
        }

        final StringBuilder formatBuilder = new StringBuilder();
        for (final int maxLength : maxLengths) {
            formatBuilder.append("%-").append(maxLength + 2).append("s");
        }
        final String format = formatBuilder.toString();

        final StringBuilder result = new StringBuilder();
        for (final List<String> row : rows) {
            result.append(String.format(format, row.toArray(new Object[0]))).append("\n");
        }
        return result.toString();
    }

    public static String[] splitCapitals(final String s) {
        return s.split("(?=\\p{Lu})");
    }

    public static String trimWith(final char c, final String s) {
        int len = s.length();
        int st = 0;
        final char[] val = s.toCharArray();

        while ((st < len) && (val[st] <= c)) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= c)) {
            len--;
        }
        // skipcq: JAVA-W0243
        return ((st > 0) || (len < s.length())) ? s.substring(st, len) : s;
    }

    public static <T> List<T> list(final int size) {
        final List<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) list.add(null);
        return list;
    }

    public static <T> Set<T> difference(final Collection<T> collectionOne, final Collection<T> collectionTwo) {
        final BiFunction<Collection<T>, Collection<T>, List<T>> filter = (c1, c2) -> c1.stream()
                .filter(element -> !c2.contains(element))
                .toList();
        final List<T> differenceInOne = filter.apply(collectionOne, collectionTwo);
        final List<T> differenceInTwo = filter.apply(collectionTwo, collectionOne);
        return asSet(merge(differenceInOne, differenceInTwo));
    }

    @NonNull
    public static <T> Set<T> asSet(final Collection<T> collection) {
        return new HashSet<>(collection);
    }

    public static Vector3N cuboidSize(final Vector3N min, final Vector3N max) {
        final double x = max.xD() - min.xD();
        final double y = max.yD() - min.yD();
        final double z = max.zD() - min.zD();
        return Vector3N.of(x, y, z);
    }

    public static boolean isCI() {
        for (final StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) return true;
        }

        return System.getenv().containsKey("GITLAB_CI")
                || System.getenv().containsKey("JENKINS_HOME")
                || System.getenv().containsKey("TRAVIS")
                || System.getenv().containsKey("GITHUB_ACTIONS")
                || System.getenv().containsKey("APPVEYOR");
    }
}
