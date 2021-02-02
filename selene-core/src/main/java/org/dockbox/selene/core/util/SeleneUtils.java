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

package org.dockbox.selene.core.util;

import org.dockbox.selene.core.events.parents.Event;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.targets.AbstractIdentifiable;
import org.dockbox.selene.core.objects.tuple.Triad;
import org.dockbox.selene.core.objects.tuple.Tuple;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.tasks.CheckedRunnable;
import org.jetbrains.annotations.Contract;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wraps all utility classes to a common accessor. This way all {@code final} utility classes can be accessed at once
 * and indexed more easily.
 */
@SuppressWarnings({"unused", "OverlyComplexClass"})
public final class SeleneUtils {

    /**
     * The maximum amount of decimals used when rounding a number in {@link SeleneUtils#round(double, int)}.
     */
    public static final int MAXIMUM_DECIMALS = 15;

    /**
     * The globally 'empty' unique ID which can be used for empty implementations of
     * {@link AbstractIdentifiable}.
     */
    public static UUID EMPTY_UUID = UUID.fromString("00000000-1111-2222-3333-000000000000");

    private static final Random random = new Random();
    private static final Map<Object, Triad<LocalDateTime, Long, TemporalUnit>> activeCooldowns = SeleneUtils.emptyConcurrentMap();

    private static final char[] _hex = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static final java.util.regex.Pattern minorTimeString =
            java.util.regex.Pattern.compile("^\\d+$");
    private static final java.util.regex.Pattern timeString =
            java.util.regex.Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");

    private static final int secondsInMinute = 60;
    private static final int secondsInHour = 60 * SeleneUtils.secondsInMinute;
    private static final int secondsInDay = 24 * SeleneUtils.secondsInHour;
    private static final int secondsInWeek = 7 * SeleneUtils.secondsInDay;

    private SeleneUtils() { }


    /**
     * Constructs a new unique map from a given set of {@link Entry entries}. If no entries are provided
     * {@link SeleneUtils#emptyMap()} is returned. The constructed map is not concurrent. Entries can easily be created
     * using {@link SeleneUtils#entry(Object, Object)}
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
     * @see SeleneUtils#entry(Object, Object)
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> Map<K, V> ofEntries(Entry<? extends K, ? extends V>... entries) {
        if (0 == entries.length) { // implicit null check of entries array
            return SeleneUtils.emptyMap();
        } else {
            Map<K, V> map = SeleneUtils.emptyMap();
            for (Entry<? extends K, ? extends V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        }
    }

    /**
     * Returns a new empty map. This should be used globally instead of instantiating maps manually. The returned map is
     * not concurrent.
     *
     * @param <K>
     *         The (super)type of the map key-set
     * @param <V>
     *         The (super)type of the map value-set
     *
     * @return The new map
     * @see SeleneUtils#emptyConcurrentMap()
     */
    public static <K, V> Map<K, V> emptyMap() {
        return new HashMap<>();
    }

    /**
     * Creates a new entry based on a given key and value combination. Both the key and value may be null.
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
     * @see SeleneUtils#ofEntries(Entry...)
     */
    public static <K, V> Entry<K, V> entry(K k, V v) {
        return new Tuple<>(k, v);
    }


    public static <T> Collection<T> singletonList(T mockWorld) {
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


    @NotNull
    @Contract("_ -> new")
    public static <T> Set<T> asSet(Collection<T> collection) {
        return new HashSet<>(collection);
    }

    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <T> List<T> asUnmodifiableList(T... objects) {
        return Collections.unmodifiableList(SeleneUtils.asList(objects));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <T> List<T> asList(T... objects) {
        return SeleneUtils.asList(Arrays.asList(objects));
    }

    public static <T> List<T> asList(Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    public static <T> List<T> asUnmodifiableList(Collection<T> collection) {
        return Collections.unmodifiableList(SeleneUtils.emptyList());
    }

    public static <T> List<T> emptyList() {
        return new ArrayList<>();
    }

    public static <T> Set<T> emptySet() {
        return new HashSet<>();
    }

    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <T> Set<T> asUnmodifiableSet(T... objects) {
        return Collections.unmodifiableSet(SeleneUtils.asSet(objects));
    }

    @NotNull
    @Contract("_ -> new")
    @SafeVarargs
    public static <T> Set<T> asSet(T... objects) {
        return new HashSet<>(SeleneUtils.asList(objects));
    }

    @SafeVarargs
    public static <T> Collection<T> asUnmodifiableCollection(T... collection) {
        return Collections.unmodifiableCollection(Arrays.asList(collection));
    }

    public static <T> Collection<T> asUnmodifiableCollection(Collection<T> collection) {
        return Collections.unmodifiableCollection(collection);
    }

    public static <K, V> Map<K, V> asUnmodifiableMap(Map<K, V> map) {
        return Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("RedundantUnmodifiable")
    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <T> Set<T> asUnmodifiableSet(Set<T> objects) {
        return Collections.unmodifiableSet(objects);
    }

    @SuppressWarnings("RedundantUnmodifiable")
    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <T> List<T> asUnmodifiableList(List<T> objects) {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Places a object in the cooldown queue for a given amount of time. If the object is already in the cooldown queue
     * it will not be overwritten and the existing queue position with be kept.
     *
     * @param o
     *         The object to place in cooldown
     * @param duration
     *         The duration
     * @param timeUnit
     *         The time unit in which the duration is kept
     */
    public static void cooldown(Object o, Long duration, TemporalUnit timeUnit) {
        SeleneUtils.cooldown(o, duration, timeUnit, false);
    }

    /**
     * Places a object in the cooldown queue for a given amount of time. If the object is already in the cooldown queue
     * it may be overwritten depending on the value of {@code overwriteExisting}.
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
    public static void cooldown(Object o, Long duration, TemporalUnit timeUnit, boolean overwriteExisting) {
        if (SeleneUtils.isInCooldown(o) && !overwriteExisting) return;
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
    public static boolean isInCooldown(Object o) {
        if (activeCooldowns.containsKey(o)) {
            LocalDateTime now = LocalDateTime.now();
            Triad<LocalDateTime, Long, TemporalUnit> cooldown = activeCooldowns.get(o);
            LocalDateTime timeCooledDown = cooldown.getFirst();
            Long duration = cooldown.getSecond();
            TemporalUnit timeUnit = cooldown.getThird();

            LocalDateTime endTime = timeCooledDown.plus(duration, timeUnit);

            return endTime.isAfter(now);

        } else return false;
    }

    /**
     * Returns a {@link List} of non-null events based on the provided {@link Event events}. This should typically be
     * used for event listeners with multiple event parameters.
     *
     * @param events
     *         The events
     *
     * @return The fired (non-null) events
     */
    public static List<Event> getFiredEvents(Event... events) {
        return Arrays.stream(events)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Returns the first fired event based on the provided {@link Event events}.
     *
     * @param events
     *         The events
     *
     * @return The first fired (non-null) event
     */
    @Contract(pure = true)
    @Nullable
    public static Event getFirstFiredEvent(Event... events) {
        for (Event event : events) {
            if (null != event) {
                return event;
            }
        }
        return null;
    }

    public static String capitalize(String value) {
        return SeleneUtils.isEmpty(value) ? value : (value.substring(0, 1).toUpperCase() + value.substring(1));
    }

    public static int trimLength(final String s) {
        return (null == s) ? 0 : s.trim().length();
    }

    public static int length(final CharSequence s) {
        return null == s ? 0 : s.length();
    }

    @Contract(pure = true)
    public static int lastIndexOf(String path, char ch) {
        if (null == path) {
            return -1;
        }
        return path.lastIndexOf(ch);
    }

    @SuppressWarnings("MagicNumber")
    public static byte[] decode(CharSequence s) {
        int len = s.length();
        if (0 != len % 2) {
            return new byte[0];
        }

        byte[] bytes = new byte[len / 2];
        int pos = 0;

        for (int i = 0; i < len; i += 2) {
            byte hi = (byte) Character.digit(s.charAt(i), 16);
            byte lo = (byte) Character.digit(s.charAt(i + 1), 16);
            bytes[pos++] = (byte) (hi * 16 + lo);
        }

        return bytes;
    }

    @NotNull
    @SuppressWarnings("MagicNumber")
    public static String encode(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length << 1);
        for (byte aByte : bytes) {
            sb.append(SeleneUtils.convertDigit(aByte >> 4));
            sb.append(SeleneUtils.convertDigit(aByte & 0x0f));
        }
        return sb.toString();
    }

    @Contract(pure = true)
    @SuppressWarnings("MagicNumber")
    public static char convertDigit(int value) {
        return _hex[value & 0x0f];
    }

    @Contract(pure = true)
    public static int[] range(int max) {
        return SeleneUtils.range(0, max);
    }

    @Contract(pure = true)
    public static int[] range(int min, int max) {
        int[] range = new int[(max - min) + 1]; // +1 as both min and max are inclusive
        for (int i = min; i <= max; i++) {
            range[i - min] = i;
        }
        return range;
    }

    public static String shorten(String string, int maxLength) {
        if (string.length() < maxLength) return string;
        return string.substring(0, maxLength - 1);
    }

    @NotNull
    public static String repeat(String string, int amount) {
        StringBuilder sb = new StringBuilder();
        for (int ignored : SeleneUtils.range(1, amount)) sb.append(string);
        return sb.toString();
    }

    public static int count(String s, char c) {
        if (SeleneUtils.isEmpty(s)) {
            return 0;
        }

        int count = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }

        return count;
    }

    @NotNull
    public static String wildcardToRegexString(@NotNull CharSequence wildcard) {
        StringBuilder s = new StringBuilder(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch (c) {
                case '*':
                    s.append(".*");
                    break;

                case '?':
                    s.append('.');
                    break;

                // escape special regexp-characters
                case '(':
                case ')':
                case '[':
                case ']':
                case '$':
                case '^':
                case '.':
                case '{':
                case '}':
                case '|':
                case '\\':
                    s.append('\\');
                    s.append(c);
                    break;

                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return s.toString();
    }

    public static int levenshteinDistance(@NonNls CharSequence s, @NonNls CharSequence t) {
        // degenerate cases          s
        if (null == s || "".contentEquals(s)) {
            return null == t || "".contentEquals(t) ? 0 : t.length();
        } else if (null == t || "".contentEquals(t)) {
            return s.length();
        }

        // create two work vectors of integer distances
        int[] v0 = new int[t.length() + 1];
        int[] v1 = new int[t.length() + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }

        int sLen = s.length();
        int tLen = t.length();
        for (int i = 0; i < sLen; i++) {
            // calculate v1 (current row distances) from the previous row v0

            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < tLen; j++) {
                int cost = (s.charAt(i) == t.charAt(j)) ? 0 : 1;
                v1[j + 1] = (int) SeleneUtils.minimum(v1[j] + 1, v0[j + 1] + 1, v0[j] + cost);
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            System.arraycopy(v1, 0, v0, 0, v0.length);
        }

        return v1[t.length()];
    }

    @Contract(pure = true)
    public static long minimum(long... values) {
        int len = values.length;
        long current = values[0];
        for (int i = 1; i < len; i++) current = Math.min(values[i], current);
        return current;
    }

    public static int damerauLevenshteinDistance(@NonNls CharSequence source, @NonNls CharSequence target) {
        if (null == source || "".contentEquals(source)) {
            return null == target || "".contentEquals(target) ? 0 : target.length();
        } else if (null == target || "".contentEquals(target)) {
            return source.length();
        }

        int srcLen = source.length();
        int targetLen = target.length();
        int[][] distanceMatrix = new int[srcLen + 1][targetLen + 1];

        // We need indexers from 0 to the length of the source string.
        // This sequential set of numbers will be the row "headers"
        // in the matrix.
        for (int srcIndex = 0; srcIndex <= srcLen; srcIndex++) {
            distanceMatrix[srcIndex][0] = srcIndex;
        }

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
                int cost = source.charAt(srcIndex - 1) == target.charAt(targetIndex - 1) ? 0 : 1;

                // Find the current distance by determining the shortest path to a
                // match (hence the 'minimum' calculation on distances).
                distanceMatrix[srcIndex][targetIndex] = (int) SeleneUtils.minimum(
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
                    distanceMatrix[srcIndex][targetIndex] = (int) SeleneUtils.minimum(
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
    public static String getRandomString(int minLen, int maxLen) {
        StringBuilder s = new StringBuilder();
        int length = minLen + random.nextInt(maxLen - minLen + 1);
        for (int i = 0; i < length; i++) {
            s.append(SeleneUtils.getRandomChar(0 == i));
        }
        return s.toString();
    }

    @NotNull
    @SuppressWarnings({"BooleanParameter", "MagicNumber"})
    public static String getRandomChar(boolean upper) {
        int r = random.nextInt(26);
        return upper ? "" + (char) ((int) 'A' + r) : "" + (char) ((int) 'a' + r);
    }

    @NotNull
    public static String createUtf8String(byte[] bytes) {
        return SeleneUtils.createString(bytes, "UTF-8");
    }

    @NotNull
    public static String createString(byte[] bytes, String encoding) {
        try {
            return null == bytes ? "" : new String(bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(String.format("Encoding (%s) is not supported by your JVM", encoding), e);
        }
    }

    public static byte[] getUTF8Bytes(String s) {
        return SeleneUtils.getBytes(s, "UTF-8");
    }

    public static byte[] getBytes(String s, String encoding) {
        try {
            return null == s ? new byte[0] : s.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(String.format("Encoding (%s) is not supported by your JVM", encoding), e);
        }
    }

    @NotNull
    public static String createUTF8String(byte[] bytes) {
        return SeleneUtils.createString(bytes, "UTF-8");
    }

    @SuppressWarnings("MagicNumber")
    public static int hashCodeIgnoreCase(CharSequence s) {
        if (null == s) return 0;
        int hash = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = Character.toLowerCase(s.charAt(i));
            hash = 31 * hash + c;
        }
        return hash;
    }

    @Contract(pure = true)
    public static long maximum(long... values) {
        int len = values.length;
        long current = values[0];
        for (int i = 1; i < len; i++) current = Math.max(values[i], current);
        return current;
    }

    @Contract(pure = true)
    public static double minimum(double... values) {
        int len = values.length;
        double current = values[0];
        for (int i = 1; i < len; i++) current = Math.min(values[i], current);
        return current;
    }

    @Contract(pure = true)
    public static double maximum(double... values) {
        int len = values.length;
        double current = values[0];
        for (int i = 1; i < len; i++) current = Math.max(values[i], current);
        return current;
    }

    public static void assertContainsIgnoreCase(String source, String... contains) {
        String lowerSource = source.toLowerCase();
        for (String contain : contains) {
            int idx = lowerSource.indexOf(contain.toLowerCase());
            String msg = "'" + contain + "' not found in '" + lowerSource + "'";
            assert 0 <= idx : msg;
            lowerSource = lowerSource.substring(idx);
        }
    }

    public static Throwable getDeepestException(Throwable e) {
        while (null != e.getCause())
            e = e.getCause();
        return e;
    }


    public static int size(final Object... array) {
        return null == array ? 0 : Array.getLength(array);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] removeItem(T[] array, int pos) {
        int length = Array.getLength(array);
        T[] dest = (T[]) Array.newInstance(array.getClass().getComponentType(), length - 1);

        System.arraycopy(array, 0, dest, 0, pos);
        System.arraycopy(array, pos + 1, dest, pos, length - pos - 1);
        return dest;
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static <T> T[] getArraySubset(T[] array, int start, int end) {
        return Arrays.copyOfRange(array, start, end);
    }

    @SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
    public static <T> T[] toArray(Class<T> classToCastTo, Collection<?> c) {
        T[] array = c.toArray((T[]) Array.newInstance(classToCastTo, c.size()));
        Iterator<?> i = c.iterator();
        int idx = 0;
        while (i.hasNext()) {
            Array.set(array, idx++, i.next());
        }
        return array;
    }

    public static boolean isFileEmpty(@NotNull Path file) {
        return !Files.exists(file) || 0 >= file.toFile().length();
    }

    /**
     * Rounds a {@code double} value to a specific amount of decimals, up to at most
     * {@link SeleneUtils#MAXIMUM_DECIMALS the maximum amount of decimals}.
     *
     * @param value
     *         The {@code double} value to round
     * @param decimalPlaces
     *         The amount of decimal places
     *
     * @return The rounded {@code double}
     */
    public static double round(double value, int decimalPlaces) {
        if (Double.isNaN(value) || Double.isInfinite(value) || MAXIMUM_DECIMALS < decimalPlaces) {
            return value;
        }

        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return decimal.doubleValue();
    }

    @NotNull
    @Contract("_ -> param1")
    public static Path createPathIfNotExists(@NotNull Path path) {
        if (!path.toFile().exists()) path.toFile().mkdirs();
        return path;
    }

    @Contract("_ -> param1")
    public static Path createFileIfNotExists(@NotNull Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file.getParent());
                Files.createFile(file);
            } catch (IOException ex) {
                Selene.handle("Could not create file '" + file.getFileName() + "'", ex);
            }
        }
        return file;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static boolean unwrap(Optional<Boolean> optional) {
        return optional.isPresent() && optional.get();
    }

    /**
     * Returns true if {@code vec} is inside the 3D cuboid defined by the two furthest points {@code min} and
     * {@code max}. Assuming <a href="https://i.stack.imgur.com/hYcv0.png">this</a> is your cuboid, {@code min}
     * represents point D, {@code max} represents point F, and {@code vec} is represented by point I.
     *
     * @param min
     *         The minimum vector of the cuboid region
     * @param max
     *         The maximum vector of the cuboid region
     * @param vec
     *         The vector position in 3D space
     *
     * @return true if {@code vec} is inside the 3D cuboid region
     * @see SeleneUtils#isInCuboidRegion(int, int, int, int, int, int, int, int, int)
     */
    public static boolean isInCuboidRegion(Vector3N min, Vector3N max, Vector3N vec) {
        return SeleneUtils.isInCuboidRegion(
                min.getXi(), max.getXi(),
                min.getYi(), max.getYi(),
                min.getZi(), max.getZi(),
                vec.getXi(), vec.getYi(), vec.getZi());
    }

    public static Vector3N getMinimumPoint(Vector3N pos1, Vector3N pos2) {
        float minX = Math.min(pos1.getXf(), pos2.getXf());
        float minY = Math.min(pos1.getYf(), pos2.getYf());
        float minZ = Math.min(pos1.getZf(), pos2.getZf());
        return new Vector3N(minX, minY, minZ);
    }

    public static Vector3N getMaximumPoint(Vector3N pos1, Vector3N pos2) {
        float maxX = Math.max(pos1.getXf(), pos2.getXf());
        float maxY = Math.max(pos1.getYf(), pos2.getYf());
        float maxZ = Math.max(pos1.getZf(), pos2.getZf());
        return new Vector3N(maxX, maxY, maxZ);
    }

    public static Vector3N getCenterPoint(Vector3N pos1, Vector3N pos2) {
        float centerX = (pos1.getXf() + pos2.getXf()) / 2;
        float centerY = (pos1.getYf() + pos2.getYf()) / 2;
        float centerZ = (pos1.getZf() + pos2.getZf()) / 2;
        return new Vector3N(centerX, centerY, centerZ);
    }

    /**
     * Returns true if a vector defined by {@code x, y, z} is inside the 3D cuboid defined by the two furthest points
     * {@code min} and {@code max}. Each point is represented by 3 integer values, x, y, and z. Assuming
     * <a href="https://i.stack.imgur.com/hYcv0.png">this</a> is your cuboid, {@code min}
     * represents point D, {@code max} represents point F, and {@code vec} is represented by point I.
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
    @Contract(pure = true)
    public static boolean isInCuboidRegion(int x_min, int x_max, int y_min, int y_max, int z_min, int z_max, int x, int y, int z) {
        return x_min <= x && x <= x_max && y_min <= y && y <= y_max && z_min <= z && z <= z_max;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Exceptional<LocalDateTime> toLocalDateTime(Optional<Instant> optionalInstant) {
        return Exceptional.of(optionalInstant).map(SeleneUtils::toLocalDateTime);
    }

    @NotNull
    @Contract("_ -> new")
    public static LocalDateTime toLocalDateTime(Instant dt) {
        return LocalDateTime.ofInstant(dt, ZoneId.systemDefault());
    }

    /**
     * Merge t [ ].
     *
     * @param <T>
     *         the type parameter
     * @param arrayOne
     *         the array one
     * @param arrayTwo
     *         the array two
     *
     * @return the t [ ]
     */
    public static <T> T[] merge(T[] arrayOne, T[] arrayTwo) {
        Object[] merged = Stream.of(arrayOne, arrayTwo).flatMap(Stream::of).toArray(Object[]::new);
        return SeleneUtils.convertGenericArray(merged);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] convertGenericArray(Object[] array) {
        try {
            Class<T> type = (Class<T>) array.getClass().getComponentType();
            T[] finalArray = (T[]) Array.newInstance(type, 0);
            return (T[]) SeleneUtils.addAll(finalArray, array);
        } catch (ClassCastException e) {
            Selene.log().error("Attempted to convert generic array not matching generic type T", e);
        }
        return (T[]) new Object[0];
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
            return SeleneUtils.shallowCopy(array2);
        } else if (null == array2) {
            return SeleneUtils.shallowCopy(array1);
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
    @Contract("null -> null")
    public static <T> T @Nullable [] shallowCopy(final T[] array) {
        if (null == array) {
            return null;
        }
        return array.clone();
    }


    @Contract(value = "null -> false", pure = true)
    public static boolean isNotEmpty(String value) {
        return null != value && !value.isEmpty();
    }

    @Contract(value = "null -> true", pure = true)
    public static boolean isEmpty(String value) {
        return null == value || value.isEmpty();
    }

    public static boolean isEmpty(Object object) {
        if (null == object) return true;
        if (object instanceof String) return SeleneUtils.isEmpty((String) object);
        else if (object instanceof Collection) return ((Collection<?>) object).isEmpty();
        else if (object instanceof Map) return ((Map<?, ?>) object).isEmpty();
        else if (Reflect.hasMethod(object, "isEmpty"))
            return Reflect.getMethodValue(object, "isEmpty", Boolean.class).orElse(false);
        else return false;
    }

    @Contract(pure = true)
    public static boolean equals(@NonNls final String str1, @NonNls final String str2) {
        if (null == str1 || null == str2) {
            //noinspection StringEquality
            return str1 == str2;
        }
        return str1.equals(str2);
    }

    @Contract(pure = true)
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

    public static boolean notEqual(Object expected, Object actual) {
        return !SeleneUtils.equal(expected, actual);
    }

    public static boolean equal(Object expected, Object actual) {
        if (null != expected || null != actual) {
            return !(null == expected || !expected.equals(actual));
        }
        return false;
    }

    public static boolean notSame(Object expected, Object actual) {
        return !SeleneUtils.same(expected, actual);
    }

    public static boolean same(Object expected, Object actual) {
        return expected == actual;
    }

    public static boolean hasContent(final String s) {
        return !(0 == SeleneUtils.trimLength(s));    // faster than returning !isEmpty()
    }

    public static boolean containsIgnoreCase(String source, String... contains) {
        String lowerSource = source.toLowerCase();
        for (String contain : contains) {
            int idx = lowerSource.indexOf(contain.toLowerCase());
            if (-1 == idx) {
                return false;
            }
            lowerSource = lowerSource.substring(idx);
        }
        return true;
    }

    @Contract("null -> true")
    public static boolean isEmpty(final Object... array) {
        return null == array || 0 == Array.getLength(array);
    }


    /**
     * Returns true if a given {@link CheckedRunnable function} does not throw any type of exception when ran. Acts as
     * inverse of {@link SeleneUtils#throwsException(CheckedRunnable)}.
     *
     * @param runnable
     *         The function to run
     *
     * @return true if the function does not throw a exception
     * @see SeleneUtils#throwsException(CheckedRunnable)
     */
    public static boolean doesNotThrow(CheckedRunnable runnable) {
        return !SeleneUtils.throwsException(runnable);
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} throws any type of exception when ran.
     *
     * @param runnable
     *         The function to run
     *
     * @return true if the function throws a exception
     */
    public static boolean throwsException(CheckedRunnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (Throwable t) {
            return true;
        }
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} does not throw a specific type of  exception when ran.
     * Acts as inverse of {@link SeleneUtils#throwsException(CheckedRunnable, Class)} )}.
     *
     * @param runnable
     *         The function to run
     * @param exception
     *         The expected type of exception
     *
     * @return true if the function does not throw a exception
     * @see SeleneUtils#throwsException(CheckedRunnable, Class)
     */
    public static boolean doesNotThrow(CheckedRunnable runnable, Class<? extends Throwable> exception) {
        return !SeleneUtils.throwsException(runnable, exception);
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} throws a specific type of exception when ran.
     *
     * @param runnable
     *         The function to run
     * @param exception
     *         The expected type of exception
     *
     * @return true if the function throws the expected exception
     */
    public static boolean throwsException(CheckedRunnable runnable, Class<? extends Throwable> exception) {
        try {
            runnable.run();
            return false;
        } catch (Throwable t) {
            return Reflect.isAssignableFrom(exception, t.getClass());
        }
    }

    public static Exceptional<Duration> durationOf(String in) {
        // First, if just digits, return the number in seconds.

        if (SeleneUtils.minorTimeString.matcher(in).matches()) {
            return Exceptional.of(Duration.ofSeconds(Long.parseUnsignedLong(in)));
        }

        Matcher m = SeleneUtils.timeString.matcher(in);
        if (m.matches()) {
            long time = SeleneUtils.durationAmount(m.group(2), SeleneUtils.secondsInWeek);
            time += SeleneUtils.durationAmount(m.group(4), SeleneUtils.secondsInDay);
            time += SeleneUtils.durationAmount(m.group(6), SeleneUtils.secondsInHour);
            time += SeleneUtils.durationAmount(m.group(8), SeleneUtils.secondsInMinute);
            time += SeleneUtils.durationAmount(m.group(10), 1);

            if (0 < time) {
                return Exceptional.of(Duration.ofSeconds(time));
            }
        }
        return Exceptional.empty();
    }

    private static long durationAmount(@Nullable String g, int multipler) {
        if (null != g && !g.isEmpty()) {
            return multipler * Long.parseUnsignedLong(g);
        }

        return 0;
    }

    public static String getFirstCauseMessage(Throwable throwable) {
        final String noCause = "No message provided";
        if (null == throwable) return noCause;
        while (true) {
            if (null != throwable.getMessage()) break;
            else if (null != throwable.getCause()) throwable = throwable.getCause();
            else break;
        }
        return null == throwable.getMessage() ? noCause : throwable.getMessage();
    }

    /**
     * Common enumeration of processed field information in {@link Reflect#tryCreateFromProcessed(Class, Function, boolean) tryCreate}.
     */
    public enum Provision {
        /**
         * Uses the field name to process field information.
         */
        FIELD,
        /**
         * Uses the field to process field information.
         */
        FIELD_NAME
    }

}
