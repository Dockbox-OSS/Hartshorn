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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.api.CheckedRunnable;
import org.dockbox.hartshorn.api.domain.AbstractIdentifiable;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Triad;
import org.dockbox.hartshorn.api.domain.tuple.Tuple;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.util.exceptions.ImpossibleFileException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Wraps all utility classes to a common accessor. This way all {@code final} utility classes can be
 * accessed at once and indexed more easily.
 */
@SuppressWarnings({ "unused", "OverlyComplexClass", "ResultOfMethodCallIgnored" })
public final class HartshornUtils {

    /**
     * The maximum amount of decimals used when rounding a number in {@link HartshornUtils#round(double,
     * int)}.
     */
    public static final int MAXIMUM_DECIMALS = 15;

    private static final Random random = new Random();
    private static final Map<Object, Triad<LocalDateTime, Long, TemporalUnit>> activeCooldowns = HartshornUtils.emptyConcurrentMap();
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
     * AbstractIdentifiable}.
     */
    public static UUID EMPTY_UUID = UUID.fromString("00000000-1111-2222-3333-000000000000");

    private HartshornUtils() {}

    /**
     * Constructs a new unique map from a given set of {@link Entry entries}. If no entries are
     * provided {@link HartshornUtils#emptyMap()} is returned. The constructed map is not concurrent.
     * Entries can easily be created using {@link HartshornUtils#entry(Object, Object)}
     *
     * @param <K>
     *         The (super)type of all keys in the entry set
     * @param <V>
     *         The (super)type of all values in the entry set
     * @param entries
     *         The entries to use while constructing a new map
     *
     * @return The new non-concurrent map
     * @throws NullPointerException
     *         If a entry is null
     * @see HartshornUtils#entry(Object, Object)
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> Map<K, V> ofEntries(final Entry<? extends K, ? extends V>... entries) {
        if (0 == entries.length) { // implicit null check of entries array
            return HartshornUtils.emptyMap();
        }
        else {
            final Map<K, V> map = HartshornUtils.emptyMap();
            for (final Entry<? extends K, ? extends V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        }
    }

    /**
     * Returns a new empty map. This should be used globally instead of instantiating maps manually.
     * The returned map is not concurrent.
     *
     * @param <K>
     *         The (super)type of the map key-set
     * @param <V>
     *         The (super)type of the map value-set
     *
     * @return The new map
     * @see HartshornUtils#emptyConcurrentMap()
     */
    public static <K, V> Map<K, V> emptyMap() {
        return new HashMap<>();
    }

    public static <K, V> MapBuilder<K, V> mapBuilder() {
        return new MapBuilder<>();
    }

    /**
     * Creates a new entry based on a given key and value combination. Both the key and value may be
     * null.
     *
     * @param <K>
     *         The type of the key
     * @param <V>
     *         The type of the value
     * @param k
     *         The key
     * @param v
     *         The value
     *
     * @return The entry
     * @see HartshornUtils#ofEntries(Entry[])
     */
    public static <K, V> Entry<K, V> entry(final K k, final V v) {
        return new Tuple<>(k, v);
    }

    public static <T> List<T> singletonList(final T mockWorld) {
        return Collections.singletonList(mockWorld);
    }

    public static <T> List<T> emptyConcurrentList() {
        return new CopyOnWriteArrayList<>();
    }

    public static <T> Set<T> emptyConcurrentSet() {
        return ConcurrentHashMap.newKeySet();
    }

    public static <K, V> ConcurrentMap<K, V> emptyConcurrentMap() {
        return new ConcurrentHashMap<>();
    }

    @UnmodifiableView
    @NotNull
    @SafeVarargs
    public static <T> List<T> asUnmodifiableList(final T... objects) {
        return Collections.unmodifiableList(HartshornUtils.asList(objects));
    }

    @NotNull
    @SafeVarargs
    public static <T> List<T> asList(final T... objects) {
        return HartshornUtils.asList(Arrays.asList(objects));
    }

    public static <T> List<T> asList(final Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    @SafeVarargs
    public static <T> List<T> asList(final Predicate<T> predicate, final T... objects) {
        final List<T> list = HartshornUtils.emptyList();
        for (final T object : objects) {
            if (predicate.test(object)) list.add(object);
        }
        return list;
    }

    public static <T> List<T> emptyList() {
        return new ArrayList<>();
    }

    public static <T> List<T> asUnmodifiableList(final Collection<T> collection) {
        return Collections.unmodifiableList(HartshornUtils.asList(collection));
    }

    @UnmodifiableView
    @NotNull
    @SafeVarargs
    public static <T> Set<T> asUnmodifiableSet(final T... objects) {
        return Collections.unmodifiableSet(HartshornUtils.asSet(objects));
    }

    @NotNull
    @SafeVarargs
    public static <T> Set<T> asSet(final T... objects) {
        return new HashSet<>(HartshornUtils.asList(objects));
    }

    @SafeVarargs
    public static <T> Set<T> asSet(final Predicate<T> predicate, final T... objects) {
        final Set<T> list = HartshornUtils.emptySet();
        for (final T object : objects) {
            if (predicate.test(object)) list.add(object);
        }
        return list;
    }

    public static <T> Set<T> emptySet() {
        return new HashSet<>();
    }

    @SafeVarargs
    public static <T> Collection<T> asUnmodifiableCollection(final T... collection) {
        return Collections.unmodifiableCollection(Arrays.asList(collection));
    }

    public static <T> Collection<T> asUnmodifiableCollection(final Collection<T> collection) {
        return Collections.unmodifiableCollection(collection);
    }

    public static <K, V> Map<K, V> asUnmodifiableMap(final Map<K, V> map) {
        return Collections.unmodifiableMap(map);
    }

    @UnmodifiableView
    @NotNull
    public static <T> Set<T> asUnmodifiableSet(final Collection<T> objects) {
        return Set.copyOf(objects);
    }

    @UnmodifiableView
    @NotNull
    public static <T> List<T> asUnmodifiableList(final List<T> objects) {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Places a object in the cooldown queue for a given amount of time. If the object is already in
     * the cooldown queue it will not be overwritten and the existing queue position with be kept.
     *
     * @param o
     *         The object to place in cooldown
     * @param duration
     *         The duration
     * @param timeUnit
     *         The time unit in which the duration is kept
     */
    public static void cooldown(final Object o, final Long duration, final TemporalUnit timeUnit) {
        HartshornUtils.cooldown(o, duration, timeUnit, false);
    }

    /**
     * Places a object in the cooldown queue for a given amount of time. If the object is already in
     * the cooldown queue it may be overwritten depending on the value of {@code overwriteExisting}.
     *
     * @param o
     *         The object to place in cooldown
     * @param duration
     *         The duration
     * @param timeUnit
     *         The time unit in which the duration is kept
     * @param overwriteExisting
     *         Whether or not to overwrite existing cooldowns
     */
    public static void cooldown(final Object o, final Long duration, final TemporalUnit timeUnit, final boolean overwriteExisting) {
        if (HartshornUtils.inCooldown(o) && !overwriteExisting) return;
        activeCooldowns.put(o, new Triad<>(LocalDateTime.now(), duration, timeUnit));
    }

    /**
     * Returns true if a object is in a active cooldown queue. Otherwise returns false
     *
     * @param o
     *         The object
     *
     * @return true if a object is in a active cooldown queue. Otherwise false
     */
    public static boolean inCooldown(final Object o) {
        if (activeCooldowns.containsKey(o)) {
            final LocalDateTime now = LocalDateTime.now();
            final Triad<LocalDateTime, Long, TemporalUnit> cooldown = activeCooldowns.get(o);
            final LocalDateTime timeCooledDown = cooldown.first();
            final Long duration = cooldown.second();
            final TemporalUnit timeUnit = cooldown.third();

            final LocalDateTime endTime = timeCooledDown.plus(duration, timeUnit);

            return endTime.isAfter(now);

        }
        else return false;
    }

    public static String capitalize(final String value) {
        return HartshornUtils.empty(value)
                ? value
                : (value.substring(0, 1).toUpperCase() + value.substring(1));
    }

    public static boolean empty(final String value) {
        return null == value || value.isEmpty();
    }

    public static int length(final CharSequence s) {
        return null == s ? 0 : s.length();
    }

    public static int lastIndexOf(final String path, final char ch) {
        if (null == path) {
            return -1;
        }
        return path.lastIndexOf(ch);
    }

    @SuppressWarnings("MagicNumber")
    public static char convertDigit(final int value) {
        return _hex[value & 0x0f];
    }

    public static int[] range(final int max) {
        return HartshornUtils.range(0, max);
    }

    public static int[] range(final int min, final int max) {
        final int[] range = new int[(max - min) + 1]; // +1 as both min and max are inclusive
        for (int i = min; i <= max; i++) {
            range[i - min] = i;
        }
        return range;
    }

    public static String shorten(final String string, final int maxLength) {
        if (string.length() < maxLength) return string;
        return string.substring(0, maxLength);
    }

    public static int count(final String s, final char c) {
        if (HartshornUtils.empty(s)) {
            return 0;
        }

        int count = 0;
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }

        return count;
    }

    @NotNull
    public static String wildcardToRegexString(@NotNull final CharSequence wildcard) {
        final StringBuilder s = new StringBuilder(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            final char c = wildcard.charAt(i);
            switch (c) {
                case '*' -> s.append(".*");
                case '?' -> s.append('.');
                // escape special regexp-characters
                case '(', ')', '[', ']', '$', '^', '.', '{', '}', '|', '\\' -> {
                    s.append('\\');
                    s.append(c);
                }
                default -> s.append(c);
            }
        }
        s.append('$');
        return s.toString();
    }

    public static int levenshteinDistance(@NonNls final CharSequence source, @NonNls final CharSequence target) {
        final int length = verifyContentLength(source, target);
        if (-1 < length) return length;

        // create two work vectors of integer distances
        final int[] v0 = new int[target.length() + 1];
        final int[] v1 = new int[target.length() + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }

        final int sLen = source.length();
        final int tLen = target.length();
        for (int i = 0; i < sLen; i++) {
            // calculate v1 (current row distances) from the previous row v0

            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < tLen; j++) {
                final int cost = (source.charAt(i) == target.charAt(j)) ? 0 : 1;
                v1[j + 1] = (int) HartshornUtils.minimum(v1[j] + 1, v0[j + 1] + 1, v0[j] + cost);
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            System.arraycopy(v1, 0, v0, 0, v0.length);
        }

        return v1[target.length()];
    }

    private static int verifyContentLength(final CharSequence source, final CharSequence target) {
        if (null == source || "".contentEquals(source)) {
            return null == target || "".contentEquals(target) ? 0 : target.length();
        }
        else if (null == target || "".contentEquals(target)) {
            return source.length();
        }
        return -1;
    }

    public static long minimum(final long... values) {
        final int len = values.length;
        long current = values[0];
        for (int i = 1; i < len; i++) current = Math.min(values[i], current);
        return current;
    }

    @SuppressWarnings("OverlyComplexMethod")
    public static int damerauLevenshteinDistance(
            @NonNls final CharSequence source, @NonNls final CharSequence target) {
        final int length = verifyContentLength(source, target);
        if (-1 < length) return length;

        final int srcLen = source.length();
        final int targetLen = target.length();
        final int[][] distanceMatrix = new int[srcLen + 1][targetLen + 1];

        // We need indexers from 0 to the length of the source string.
        // This sequential set of numbers will be the row "headers"
        // in the matrix.
        for (int srcIndex = 0; srcIndex <= srcLen; srcIndex++) distanceMatrix[srcIndex][0] = srcIndex;

        // We need indexers from 0 to the length of the target string.
        // This sequential set of numbers will be the
        // column "headers" in the matrix.
        for (int targetIndex = 0; targetIndex <= targetLen; targetIndex++) {
            // Set the value of the first cell in the column
            // equivalent to the current value of the iterator
            distanceMatrix[0][targetIndex] = targetIndex;
        }

        for (int srcIndex = 1; srcIndex <= srcLen; srcIndex++) {
            for (int targetIndex = 1; targetIndex <= targetLen; targetIndex++) {
                // If the current characters in both strings are equal
                final int cost = source.charAt(srcIndex - 1) == target.charAt(targetIndex - 1) ? 0 : 1;

                // Find the current distance by determining the shortest path to a
                // match (hence the 'minimum' calculation on distances).
                distanceMatrix[srcIndex][targetIndex] = (int) HartshornUtils.minimum(
                        // Character match between current character in
                        // source string and next character in target
                        distanceMatrix[srcIndex - 1][targetIndex] + 1,
                        // Character match between next character in
                        // source string and current character in target
                        distanceMatrix[srcIndex][targetIndex - 1] + 1,
                        // No match, at current, add cumulative penalty
                        distanceMatrix[srcIndex - 1][targetIndex - 1] + cost);

                // We don't want to do the next series of calculations on
                // the first pass because we would get an index out of bounds
                // exception.
                if (1 == srcIndex || 1 == targetIndex) {
                    continue;
                }

                // transposition check (if the current and previous
                // character are switched around (e.g.: t[se]t and t[es]t)...
                if (source.charAt(srcIndex - 1) == target.charAt(targetIndex - 2) && source.charAt(srcIndex - 2) == target.charAt(targetIndex - 1)) {
                    // What's the minimum cost between the current distance
                    // and a transposition.
                    distanceMatrix[srcIndex][targetIndex] = (int) HartshornUtils.minimum(
                            // Current cost
                            distanceMatrix[srcIndex][targetIndex],
                            // Transposition
                            distanceMatrix[srcIndex - 2][targetIndex - 2] + cost);
                }
            }
        }

        return distanceMatrix[srcLen][targetLen];
    }

    @NotNull
    public static String randomString(final int minLen, final int maxLen) {
        final StringBuilder s = new StringBuilder();
        final int length = minLen + random.nextInt(maxLen - minLen + 1);
        for (int i = 0; i < length; i++) {
            s.append(HartshornUtils.randomChar(0 == i));
        }
        return s.toString();
    }

    @NotNull
    @SuppressWarnings({ "BooleanParameter", "MagicNumber" })
    public static String randomChar(final boolean upper) {
        final int r = random.nextInt(26);
        return upper ? "" + (char) ((int) 'A' + r) : "" + (char) ((int) 'a' + r);
    }

    @NotNull
    public static String createUtf8String(final byte[] bytes) {
        return HartshornUtils.createString(bytes, "UTF-8");
    }

    @NotNull
    public static String createString(final byte[] bytes, final String encoding) {
        try {
            return null == bytes ? "" : new String(bytes, encoding);
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException(String.format("Encoding (%s) is not supported by your JVM", encoding), e);
        }
    }

    public static byte[] bytesUTF8(final String s) {
        return HartshornUtils.bytes(s, "UTF-8");
    }

    public static byte[] bytes(final String s, final String encoding) {
        try {
            return null == s ? new byte[0] : s.getBytes(encoding);
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException(String.format("Encoding (%s) is not supported by your JVM", encoding), e);
        }
    }

    @NotNull
    public static String createUTF8String(final byte[] bytes) {
        return HartshornUtils.createString(bytes, "UTF-8");
    }

    @SuppressWarnings("MagicNumber")
    public static int hashCodeIgnoreCase(final CharSequence s) {
        if (null == s) return 0;
        int hash = 0;
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            final char c = Character.toLowerCase(s.charAt(i));
            hash = 31 * hash + c;
        }
        return hash;
    }

    public static long maximum(final long... values) {
        final int len = values.length;
        long current = values[0];
        for (int i = 1; i < len; i++) current = Math.max(values[i], current);
        return current;
    }

    public static double minimum(final double... values) {
        final int len = values.length;
        double current = values[0];
        for (int i = 1; i < len; i++) current = Math.min(values[i], current);
        return current;
    }

    public static double maximum(final double... values) {
        final int len = values.length;
        double current = values[0];
        for (int i = 1; i < len; i++) current = Math.max(values[i], current);
        return current;
    }

    public static void assertContainsIgnoreCase(final String source, final String... contains) {
        String lowerSource = source.toLowerCase();
        for (final String contain : contains) {
            final int idx = lowerSource.indexOf(contain.toLowerCase());
            final String msg = "'" + contain + "' not found in '" + lowerSource + "'";
            assert 0 <= idx : msg;
            lowerSource = lowerSource.substring(idx);
        }
    }

    public static Throwable deepestException(Throwable e) {
        while (null != e.getCause()) e = e.getCause();
        return e;
    }

    public static int size(final Object... array) {
        return null == array ? 0 : Array.getLength(array);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] removeItem(final T[] array, final int pos) {
        final int length = Array.getLength(array);
        final T[] dest = (T[]) Array.newInstance(array.getClass().getComponentType(), length - 1);

        System.arraycopy(array, 0, dest, 0, pos);
        System.arraycopy(array, pos + 1, dest, pos, length - pos - 1);
        return dest;
    }

    // Both start and end are inclusive
    public static <T> T[] arraySubset(final T[] array, final int start, final int end) {
        return Arrays.copyOfRange(array, start, end + 1);
    }

    @SuppressWarnings({ "unchecked", "SuspiciousToArrayCall" })
    public static <T> T[] toArray(final Class<T> classToCastTo, final Collection<?> c) {
        final T[] array = c.toArray((T[]) Array.newInstance(classToCastTo, c.size()));
        final Iterator<?> i = c.iterator();
        int idx = 0;
        while (i.hasNext()) {
            Array.set(array, idx++, i.next());
        }
        return array;
    }

    public static boolean empty(@NotNull final Path file) {
        return !Files.exists(file) || 0 >= file.toFile().length();
    }

    /**
     * Rounds a {@code double} value to a specific amount of decimals, up to at most {@link
     * HartshornUtils#MAXIMUM_DECIMALS the maximum amount of decimals}.
     *
     * @param value
     *         The {@code double} value to round
     * @param decimalPlaces
     *         The amount of decimal places
     *
     * @return The rounded {@code double}
     */
    public static double round(final double value, final int decimalPlaces) {
        if (Double.isNaN(value) || Double.isInfinite(value) || MAXIMUM_DECIMALS < decimalPlaces) {
            return value;
        }

        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return decimal.doubleValue();
    }

    @NotNull
    public static Path createPathIfNotExists(@NotNull final Path path) {
        if (!path.toFile().exists()) path.toFile().mkdirs();
        return path;
    }

    public static Path createFileIfNotExists(@NotNull final Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file.getParent());
                Files.createFile(file);
            }
            catch (final IOException e) {
                throw new ImpossibleFileException(file, e);
            }
        }
        return file;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static boolean unwrap(final Optional<Boolean> optional) {
        return optional.isPresent() && optional.get();
    }

    public static boolean unwrap(final Exceptional<Boolean> exceptional) {
        return exceptional.present() && exceptional.get();
    }

    /**
     * Returns true if {@code vec} is inside the 3D cuboid defined by the two furthest points {@code
     * min} and {@code max}. Assuming <a href="https://i.stack.imgur.com/hYcv0.png">this</a> is your
     * cuboid, {@code min} represents point D, {@code max} represents point F, and {@code vec} is
     * represented by point I.
     *
     * @param min
     *         The minimum vector of the cuboid region
     * @param max
     *         The maximum vector of the cuboid region
     * @param vec
     *         The vector position in 3D space
     *
     * @return true if {@code vec} is inside the 3D cuboid region
     * @see HartshornUtils#inCuboidRegion(int, int, int, int, int, int, int, int, int)
     */
    public static boolean inCuboidRegion(final Vector3N min, final Vector3N max, final Vector3N vec) {
        return HartshornUtils.inCuboidRegion(
                min.xI(), max.xI(),
                min.yI(), max.yI(),
                min.zI(), max.zI(),
                vec.xI(), vec.yI(), vec.zI()
        );
    }

    /**
     * Returns true if a vector defined by {@code x, y, z} is inside the 3D cuboid defined by the two
     * furthest points {@code min} and {@code max}. Each point is represented by 3 integer values, x,
     * y, and z. Assuming <a href="https://i.stack.imgur.com/hYcv0.png">this</a> is your cuboid,
     * {@code min} represents point D, {@code max} represents point F, and {@code vec} is represented
     * by point I.
     *
     * @param x_min
     *         The position of the minimum vector on the X axis
     * @param x_max
     *         The position of the maximum vector on the X axis
     * @param y_min
     *         The position of the minimum vector on the Y axis
     * @param y_max
     *         The position of the maximum vector on the Y axis
     * @param z_min
     *         The position of the minimum vector on the Z axis
     * @param z_max
     *         The position of the maximum vector on the Z axis
     * @param x
     *         The position of the vector on the X axis
     * @param y
     *         The position of the vector on the Y axis
     * @param z
     *         The position of the vector on the Z axis
     *
     * @return true if the defined vector is inside the 3D cuboid region
     */
    @SuppressWarnings("OverlyComplexBooleanExpression")
    public static boolean inCuboidRegion(final int x_min, final int x_max, final int y_min, final int y_max, final int z_min, final int z_max, final int x, final int y, final int z) {
        return x_min <= x && x <= x_max
                && y_min <= y && y <= y_max
                && z_min <= z && z <= z_max;
    }

    public static Vector3N minimumPoint(final Vector3N pos1, final Vector3N pos2) {
        final float minX = Math.min(pos1.xF(), pos2.xF());
        final float minY = Math.min(pos1.yF(), pos2.yF());
        final float minZ = Math.min(pos1.zF(), pos2.zF());
        return Vector3N.of(minX, minY, minZ);
    }

    public static Vector3N maximumPoint(final Vector3N pos1, final Vector3N pos2) {
        final float maxX = Math.max(pos1.xF(), pos2.xF());
        final float maxY = Math.max(pos1.yF(), pos2.yF());
        final float maxZ = Math.max(pos1.zF(), pos2.zF());
        return Vector3N.of(maxX, maxY, maxZ);
    }

    public static Vector3N centerPoint(final Vector3N pos1, final Vector3N pos2) {
        final float centerX = (pos1.xF() + pos2.xF()) / 2;
        final float centerY = (pos1.yF() + pos2.yF()) / 2;
        final float centerZ = (pos1.zF() + pos2.zF()) / 2;
        return Vector3N.of(centerX, centerY, centerZ);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Exceptional<LocalDateTime> toLocalDateTime(final Optional<Instant> optionalInstant) {
        return Exceptional.of(optionalInstant).map(HartshornUtils::toLocalDateTime);
    }

    @NotNull
    public static LocalDateTime toLocalDateTime(final Instant dt) {
        return LocalDateTime.ofInstant(dt, ZoneId.systemDefault());
    }

    /**
     * Merge t [ ].
     *
     * @param arrayOne
     *         the array one
     * @param arrayTwo
     *         the array two
     *
     * @return the t [ ]
     */
    public static <T> T[] merge(final T[] arrayOne, final T[] arrayTwo) {
        final Set<T> merged = HartshornUtils.asSet(arrayOne);
        merged.addAll(HartshornUtils.asSet(arrayTwo));
        return merged.toArray(arrayOne);
    }

    /**
     * Add all t [ ].
     *
     * @param <T>
     *         the type parameter
     * @param array1
     *         the array 1
     * @param array2
     *         the array 2
     *
     * @return the t [ ]
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] addAll(final T[] array1, final T[] array2) {
        if (null == array1) {
            return HartshornUtils.shallowCopy(array2);
        }
        else if (null == array2) {
            return HartshornUtils.shallowCopy(array1);
        }
        final T[] newArray = (T[]) Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
        System.arraycopy(array1, 0, newArray, 0, array1.length);
        System.arraycopy(array2, 0, newArray, array1.length, array2.length);
        return newArray;
    }

    /**
     * Shallow copy t @ nullable [ ].
     *
     * @param <T>
     *         the type parameter
     * @param array
     *         the array
     *
     * @return the t @ nullable [ ]
     */
    public static <T> T @Nullable [] shallowCopy(final T[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }

    public static boolean notEmpty(final String value) {
        return null != value && !value.isEmpty();
    }

    public static boolean empty(final Object object) {
        if (null == object) return true;
        if (object instanceof String) return HartshornUtils.empty((String) object);
        else if (object instanceof Collection) return ((Collection<?>) object).isEmpty();
        else if (object instanceof Map) return ((Map<?, ?>) object).isEmpty();
        else return false;
    }

    public static boolean equals(@NonNls final String str1, @NonNls final String str2) {
        if (null == str1 || null == str2) {
            //noinspection StringEquality
            return str1 == str2;
        }
        return str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(@NonNls final String s1, @NonNls final String s2) {
        if (null == s1 || null == s2) {
            //noinspection StringEquality
            return s1 == s2;
        }
        return s1.equalsIgnoreCase(s2);
    }

    public static boolean equalsWithTrim(@NonNls final String s1, @NonNls final String s2) {
        if (null == s1 || null == s2) {
            //noinspection StringEquality
            return s1 == s2;
        }
        return s1.trim().equals(s2.trim());
    }

    public static boolean equalsIgnoreCaseWithTrim(@NonNls final String s1, @NonNls final String s2) {
        if (null == s1 || null == s2) {
            //noinspection StringEquality
            return s1 == s2;
        }
        return s1.trim().equalsIgnoreCase(s2.trim());
    }

    public static boolean notEqual(final Object expected, final Object actual) {
        return !HartshornUtils.equal(expected, actual);
    }

    public static boolean equal(final Object expected, final Object actual) {
        if (null != expected || null != actual) {
            return !(null == expected || !expected.equals(actual));
        }
        return false;
    }

    public static boolean notSame(final Object expected, final Object actual) {
        return !HartshornUtils.same(expected, actual);
    }

    public static boolean same(final Object expected, final Object actual) {
        return expected == actual;
    }

    public static boolean hasContent(final String s) {
        return !(0 == HartshornUtils.trimLength(s)); // faster than returning !empty()
    }

    public static int trimLength(final String s) {
        return (null == s) ? 0 : s.trim().length();
    }

    public static boolean containsIgnoreCase(final String source, final String... contains) {
        String lowerSource = source.toLowerCase();
        for (final String contain : contains) {
            final int idx = lowerSource.indexOf(contain.toLowerCase());
            if (-1 == idx) {
                return false;
            }
            lowerSource = lowerSource.substring(idx);
        }
        return true;
    }

    public static <T> boolean contains(final T[] objects, final T object) {
        for (final T t : objects) {
            if (same(object, t)) return true;
        }
        return false;
    }

    public static <T> boolean containsEqual(final T[] objects, final T object) {
        for (final T t : objects) {
            if (same(object, t) || equal(object, t)) return true;
        }
        return false;
    }

    public static String strip(final String s) {
        return s.replaceAll("[\n\r ]+", "").trim();
    }

    public static boolean empty(final Object... array) {
        return null == array || 0 == Array.getLength(array);
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} does not throw any type of exception
     * when ran. Acts as inverse of {@link HartshornUtils#throwsException(CheckedRunnable)}.
     *
     * @param runnable
     *         The function to run
     *
     * @return true if the function does not throw a exception
     * @see HartshornUtils#throwsException(CheckedRunnable)
     */
    public static boolean doesNotThrow(final CheckedRunnable runnable) {
        return !HartshornUtils.throwsException(runnable);
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} throws any type of exception when ran.
     *
     * @param runnable
     *         The function to run
     *
     * @return true if the function throws a exception
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
     * Class)} )}.
     *
     * @param runnable
     *         The function to run
     * @param exception
     *         The expected type of exception
     *
     * @return true if the function does not throw a exception
     * @see HartshornUtils#throwsException(CheckedRunnable, Class)
     */
    public static boolean doesNotThrow(final CheckedRunnable runnable, final Class<? extends Throwable> exception) {
        return !HartshornUtils.throwsException(runnable, exception);
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} throws a specific type of exception
     * when ran.
     *
     * @param runnable
     *         The function to run
     * @param exception
     *         The expected type of exception
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

    private static long durationAmount(@Nullable final String g, final int multipler) {
        if (null != g && !g.isEmpty()) {
            return multipler * Long.parseUnsignedLong(g);
        }

        return 0;
    }

    @SafeVarargs
    public static <T> Collection<T> merge(final Collection<T>... collections) {
        final Collection<T> merged = HartshornUtils.emptySet();
        for (final Collection<T> collection : collections) {
            merged.addAll(collection);
        }
        return merged;
    }

    @SafeVarargs
    public static <T, R> Object[] all(final Function<T, R> function, final T... input) {
        final List<R> out = HartshornUtils.emptyList();
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

    public static String wrap(final String fullName, final int max) {
        final int start = fullName.length() - max;
        if (start < 0) return fullName + repeat(" ", -start);
        else return fullName.substring(start);
    }

    @NotNull
    public static String repeat(final String string, final int amount) {
        final StringBuilder sb = new StringBuilder();
        for (final int ignored : HartshornUtils.range(1, amount)) sb.append(string);
        return sb.toString();
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
        return ((st > 0) || (len < s.length())) ? s.substring(st, len) : s;
    }

    public static <T> List<T> list(final int size) {
        final List<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) list.add(null);
        return list;
    }

    public static <T> Set<T> difference(final Collection<T> collectionOne, final Collection<T> collectionTwo) {
        BiFunction<Collection<T>, Collection<T>, List<T>> filter = (c1, c2) -> c1.stream()
                .filter(element -> !c2.contains(element))
                .toList();
        List<T> differenceInOne = filter.apply(collectionOne, collectionTwo);
        List<T> differenceInTwo = filter.apply(collectionTwo, collectionOne);
        return asSet(merge(differenceInOne, differenceInTwo));
    }

    @NotNull
    public static <T> Set<T> asSet(final Collection<T> collection) {
        return new HashSet<>(collection);
    }

    public static <T> List<T> merge(final Collection<T> collectionOne, final Collection<T> collectionTwo) {
        final Set<T> merged = HartshornUtils.asSet(collectionOne);
        merged.addAll(HartshornUtils.asSet(collectionTwo));
        return HartshornUtils.asList(merged);
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

    public enum Provision {
        /** Uses the field name to process field information. */
        FIELD,
        /** Uses the field to process field information. */
        FIELD_NAME
    }
}
