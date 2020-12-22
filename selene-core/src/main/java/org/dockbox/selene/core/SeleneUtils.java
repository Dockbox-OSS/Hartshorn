/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core;

import org.dockbox.selene.core.annotations.entity.Ignore;
import org.dockbox.selene.core.annotations.entity.Property;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.events.parents.Event;
import org.dockbox.selene.core.exceptions.global.UncheckedSeleneException;
import org.dockbox.selene.core.extension.ExtensionManager;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.keys.Key;
import org.dockbox.selene.core.objects.keys.PersistentDataKey;
import org.dockbox.selene.core.objects.keys.RemovableKey;
import org.dockbox.selene.core.objects.keys.TransactionResult;
import org.dockbox.selene.core.objects.keys.data.DoublePersistentDataKey;
import org.dockbox.selene.core.objects.keys.data.IntegerPersistentDataKey;
import org.dockbox.selene.core.objects.keys.data.StringPersistentDataKey;
import org.dockbox.selene.core.objects.keys.data.TypedPersistentDataKey;
import org.dockbox.selene.core.objects.tuple.Triad;
import org.dockbox.selene.core.objects.tuple.Tuple;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.core.server.IntegratedExtension;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
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
import java.util.Locale;
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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Common utilities for a variety of actions. Including, but not restricted to:
 * <ul>
 *     <li>Array- and collection manipulation</li>
 *     <li>Reflections</li>
 *     <li>Strict equality</li>
 *     <li>Natural Language Processing</li>
 *     <li>Virtual randomness</li>
 *     <li>Exceptions</li>
 *     <li>Primite type assignability</li>
 *     <li>etc.</li>
 * </ul>
 *
 * <p>
 *     Utilities which are duplicated across classes should be moved to this type.
 * </p>
 */
@SuppressWarnings("OverlyComplexClass")
public enum SeleneUtils {
    ;

    /**
     * The maximum amount of decimals used when rounding a number in {@link SeleneUtils#round(double, int)}.
     */
    public static final int MAXIMUM_DECIMALS = 15;

    /**
     * The globally 'empty' unique ID which can be used for empty implementations of
     * {@link org.dockbox.selene.core.objects.targets.Identifiable}.
     */
    public static final UUID EMPTY_UUID = UUID.fromString("00000000-1111-2222-3333-000000000000");

    private static final Random random = new Random();
    private static final Map<Object, Triad<LocalDateTime, Long, TemporalUnit>> activeCooldowns = emptyConcurrentMap();
    private static final List<Class<?>> nbtSupportedTypes = asList(
            boolean.class, byte.class, short.class, int.class, long.class, float.class, double.class,
            byte[].class, int[].class, long[].class,
            String.class, List.class, Map.class
    );
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap =
            ofEntries(entry(boolean.class, Boolean.class),
                    entry(byte.class, Byte.class),
                    entry(char.class, Character.class),
                    entry(double.class, Double.class),
                    entry(float.class, Float.class),
                    entry(int.class, Integer.class),
                    entry(long.class, Long.class),
                    entry(short.class, Short.class));
    private static final char[] _hex = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

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
            return emptyMap();
        } else {
            Map<K, V> map = emptyMap();
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
        cooldown(o, duration, timeUnit, false);
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
        if (isInCooldown(o) && !overwriteExisting) return;
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

    @Contract(value = "null -> false", pure = true)
    public static boolean isNotEmpty(String value) {
        return null != value && !value.isEmpty();
    }

    public static String capitalize(String value) {
        return isEmpty(value) ? value : (value.substring(0, 1).toUpperCase() + value.substring(1));
    }

    @Contract(value = "null -> true", pure = true)
    public static boolean isEmpty(String value) {
        return null == value || value.isEmpty();
    }

    public static boolean isEmpty(Object object) {
        if (null == object) return true;
        if (object instanceof String) return isEmpty((String) object);
        else if (object instanceof Collection) return ((Collection<?>) object).isEmpty();
        else if (object instanceof Map) return ((Map<?, ?>) object).isEmpty();
        else if (hasMethod(object, "isEmpty"))
            return getMethodValue(object, "isEmpty", Boolean.class).orElse(false);
        else return false;
    }

    /**
     * Returns true if a instance has a declared method of which the name equals the value of {@code method} and has no
     * parameters.
     *
     * @param instance
     *         The instance
     * @param method
     *         The name of the method to check for
     *
     * @return true if the type of the instance has a declared method which matches {@code method}
     */
    public static boolean hasMethod(Object instance, String method) {
        return hasMethod(instance.getClass(), method);
    }

    /**
     * Attempts to get the return value of a method which may not be publicly accessible (e.g. protected or private).
     * If the method does not exist, or throws a exception the error is wrapped in a {@link Exceptional}. Otherwise the
     * (nullable) return value is returned wrapped in a {@link Exceptional}.
     *
     * @param <T>
     *         The type of the expected return value
     * @param instance
     *         The instance to call the method on
     * @param method
     *         The method to call
     * @param expectedType
     *         The type of the expected return value
     * @param args
     *         The arguments which are provided to the method call
     *
     * @return The result of the method call, wrapped in {@link Exceptional}
     */
    @SuppressWarnings("unchecked")
    public static <T> Exceptional<T> getMethodValue(Object instance, String method, Class<T> expectedType, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        return getMethodValue(instance.getClass(), instance, method, expectedType, argTypes, args);
    }

    /**
     * Returns true if a type has a declared method of which the name equals the value of {@code method} and has no
     * parameters.
     *
     * @param type
     *         The type
     * @param method
     *         The name of the method to check for
     *
     * @return true if the type has a declared method which matches {@code method}
     */
    public static boolean hasMethod(Class<?> type, @NonNls String method) {
        for (Method m : type.getDeclaredMethods()) {
            if (m.getName().equals(method)) return true;
        }
        return false;
    }

    /**
     * Attempts to get the return value of a method which may not be publicly accessible (e.g. protected or private).
     * If the method does not exist, or throws a exception the error is wrapped in a {@link Exceptional}. Otherwise the
     * (nullable) return value is returned wrapped in a {@link Exceptional}.
     *
     * @param <T>
     *         The type of the expected return value
     * @param methodHolder
     *         The type to call the method on
     * @param instance
     *         The instance to call the method with
     * @param method
     *         The method to call
     * @param expectedType
     *         The type of the expected return value
     * @param argumentTypes
     *         The types of the arguments, used to collect the appropriate method
     * @param args
     *         The arguments which are provided to the method call
     *
     * @return The result of the method call, wrapped in {@link Exceptional}
     */
    @SuppressWarnings("unchecked")
    public static <T> Exceptional<T> getMethodValue(Class<?> methodHolder, Object instance, String method, Class<T> expectedType, Class<?>[] argumentTypes, Object... args) {
        try {
            Method m = methodHolder.getDeclaredMethod(method, argumentTypes);
            if (!m.isAccessible()) m.setAccessible(true);
            T value = (T) m.invoke(instance, args);
            return Exceptional.ofNullable(value);
        } catch (ClassCastException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return Exceptional.of(e);
        }
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
        return !equal(expected, actual);
    }

    public static boolean equal(Object expected, Object actual) {
        if (null != expected || null != actual) {
            return !(null == expected || !expected.equals(actual));
        }
        return false;
    }

    public static boolean notSame(Object expected, Object actual) {
        return !same(expected, actual);
    }

    public static boolean same(Object expected, Object actual) {
        return expected == actual;
    }

    public static boolean hasContent(final String s) {
        return !(0 == trimLength(s));    // faster than returning !isEmpty()
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
            sb.append(convertDigit(aByte >> 4));
            sb.append(convertDigit(aByte & 0x0f));
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
        return range(0, max);
    }

    @Contract(pure = true)
    public static int[] range(int min, int max) {
        int[] range = new int[(max - min) + 1]; // +1 as both min and max are inclusive
        for (int i = min; i <= max; i++) {
            range[i - min] = i;
        }
        return range;
    }

    @NotNull
    public static String repeat(String string, int amount) {
        StringBuilder sb = new StringBuilder();
        for (int ignored : range(1, amount)) sb.append(string);
        return sb.toString();
    }

    public static int count(String s, char c) {
        if (isEmpty(s)) {
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
                v1[j + 1] = (int) minimum(v1[j] + 1, v0[j + 1] + 1, v0[j] + cost);
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
                distanceMatrix[srcIndex][targetIndex] = (int) minimum(
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
                    distanceMatrix[srcIndex][targetIndex] = (int) minimum(
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
            s.append(getRandomChar(0 == i));
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
        return createString(bytes, "UTF-8");
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
        return getBytes(s, "UTF-8");
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
        return createString(bytes, "UTF-8");
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

    public static boolean checkContainsIgnoreCase(String source, String... contains) {
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

    public static Throwable getDeepestException(Throwable e) {
        while (null != e.getCause())
            e = e.getCause();
        return e;
    }

    @Contract("null -> true")
    public static boolean isEmpty(final Object... array) {
        return null == array || 0 == Array.getLength(array);
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

    @Contract("null, _ -> false; !null, null -> false")
    public static <T> boolean isGenericInstanceOf(T instance, Class<?> type) {
        return null != instance && null != type && isAssignableFrom(type, instance.getClass());
    }

    /**
     * Returns true if {@code to} is equal to-, a super type of, or a primitive wrapper of {@code from}.
     *
     * <p>
     * Primitive wrappers include all JDK wrappers for native types (int, char, double, etc). E.g. all of the
     * following assignabilities return true:
     * <pre>{@code
     *          SeleneUtils.isAssignableFrom(int.class, Integer.class);
     *          SeleneUtils.isAssignableFrom(Integer.class, int.class);
     *          SeleneUtils.isAssignableFrom(int.class, int.class);
     *          SeleneUtils.isAssignableFrom(Number.class, Integer.class);
     *     }</pre>
     * </p>
     *
     * @param to
     *         The possible (super) type or primite wrapper of {@code from}
     * @param from
     *         The type to compare assignability against
     *
     * @return true if {@code to} is equal to-, a super type of, or a primitive wrapper of {@code from}
     * @see SeleneUtils#isPrimitiveWrapperOf(Class, Class)
     */
    public static boolean isAssignableFrom(Class<?> to, Class<?> from) {
        if (to.isAssignableFrom(from)) {
            return true;
        }
        if (from.isPrimitive()) {
            return isPrimitiveWrapperOf(to, from);
        }
        if (to.isPrimitive()) {
            return isPrimitiveWrapperOf(from, to);
        }
        return false;
    }

    /**
     * Returns true if {@code targetClass} is a primitive wrapper of {@code primitive}.
     *
     * @param targetClass
     *         The primitive wrapper (e.g. Integer)
     * @param primitive
     *         The primitive type (e.g. int)
     *
     * @return true if {@code targetClass} is a primitive wrapper of {@code primitive}.
     */
    public static boolean isPrimitiveWrapperOf(Class<?> targetClass, Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException("First argument has to be primitive type");
        }
        return primitiveWrapperMap.get(primitive) == targetClass;
    }

    public static boolean isFileEmpty(@NotNull Path file) {
        return Files.exists(file) && 0 <= file.toFile().length();
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
    @Contract("_ -> new")
    public static <T> Set<T> asSet(Collection<T> collection) {
        return new HashSet<>(collection);
    }

    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <T> List<T> asUnmodifiableList(T... objects) {
        return Collections.unmodifiableList(asList(objects));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <T> List<T> asList(T... objects) {
        return asList(Arrays.asList(objects));
    }

    public static <T> List<T> asList(Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    public static <T> List<T> asUnmodifiableList(Collection<T> collection) {
        return Collections.unmodifiableList(emptyList());
    }

    public static <T> List<T> emptyList() {
        return new ArrayList<>();
    }

    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <T> Set<T> asUnmodifiableSet(T... objects) {
        return Collections.unmodifiableSet(asSet(objects));
    }

    @NotNull
    @Contract("_ -> new")
    @SafeVarargs
    public static <T> Set<T> asSet(T... objects) {
        return new HashSet<>(asList(objects));
    }

    public static <T> Collection<T> asUnmodifiableCollection(T... collection) {
        return Collections.unmodifiableCollection(Arrays.asList(collection));
    }

    public static <T> Collection<T> asUnmodifiableCollection(Collection<T> collection) {
        return Collections.unmodifiableCollection(collection);
    }

    public static <K, V> Map<K, V> asUnmodifiableMap(Map<K, V> map) {
        return Collections.unmodifiableMap(map);
    }

    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <T> Set<T> asUnmodifiableSet(Set<T> objects) {
        return Collections.unmodifiableSet(objects);
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
        return !throwsException(runnable);
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
        return !throwsException(runnable, exception);
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
            return isAssignableFrom(exception, t.getClass());
        }
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
                Selene.except("Could not create file '" + file.getFileName() + "'", ex);
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
        return isInCuboidRegion(
                min.getXi(), max.getXi(),
                min.getYi(), max.getYi(),
                min.getZi(), max.getZi(),
                vec.getXi(), vec.getYi(), vec.getZi());
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

    /*
     =====================
      TODO GuusLieben:
       Continue documentation for methods below this point
     =====================
     */

    /**
     * Gets annoted methods.
     *
     * @param <A>
     *         the type parameter
     * @param clazz
     *         the clazz
     * @param annotation
     *         the annotation
     * @param rule
     *         the rule
     *
     * @return the annoted methods
     */
    @NotNull
    @Unmodifiable
    public static <A extends Annotation> Collection<Method> getAnnotedMethods(Class<?> clazz, Class<A> annotation, Predicate<A> rule) {
        return getAnnotedMethods(clazz, annotation, rule, false);
    }

    /**
     * Gets annoted methods.
     *
     * @param <A>
     *         the type parameter
     * @param clazz
     *         the clazz
     * @param annotation
     *         the annotation
     * @param rule
     *         the rule
     * @param skipParents
     *         the skip parents
     *
     * @return the annoted methods
     */
    @NotNull
    @Unmodifiable
    public static <A extends Annotation> Collection<Method> getAnnotedMethods(Class<?> clazz, Class<A> annotation, Predicate<A> rule, boolean skipParents) {
        List<Method> annotatedMethods = emptyList();
        for (Method method : asList(skipParents ? clazz.getMethods() : clazz.getDeclaredMethods())) {
            if (!method.isAccessible()) method.setAccessible(true);
            if (method.isAnnotationPresent(annotation) && rule.test(method.getAnnotation(annotation))) {
                annotatedMethods.add(method);
            }
        }
        return asUnmodifiableList(annotatedMethods);
    }

    @UnmodifiableView
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <T> List<T> asUnmodifiableList(List<T> objects) {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Gets annotated types.
     *
     * @param <A>
     *         the type parameter
     * @param prefix
     *         the prefix
     * @param annotation
     *         the annotation
     *
     * @return the annotated types
     */
    public static <A extends Annotation> Collection<Class<?>> getAnnotatedTypes(String prefix, Class<A> annotation) {
        return getAnnotatedTypes(prefix, annotation, false);
    }

    /**
     * Gets annotated types.
     *
     * @param <A>
     *         the type parameter
     * @param prefix
     *         the prefix
     * @param annotation
     *         the annotation
     * @param skipParents
     *         the skip parents
     *
     * @return the annotated types
     */
    public static <A extends Annotation> Collection<Class<?>> getAnnotatedTypes(String prefix, Class<A> annotation, boolean skipParents) {
        Reflections reflections = new Reflections(prefix);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(annotation, !skipParents);
        return asList(types);
    }

    /**
     * Gets property value.
     *
     * @param <T>
     *         the type parameter
     * @param key
     *         the key
     * @param expectedType
     *         the expected type
     * @param properties
     *         the properties
     *
     * @return the property value
     */
    public static <T> Exceptional<T> getPropertyValue(@NonNls String key, Class<T> expectedType, InjectorProperty<?>... properties) {
        InjectorProperty<T> property = getProperty(key, expectedType, properties);
        if (null != property) {
            return Exceptional.of(property::getObject);
        }
        return Exceptional.empty();
    }

    /**
     * Gets property.
     *
     * @param <T>
     *         the type parameter
     * @param key
     *         the key
     * @param expectedType
     *         the expected type
     * @param properties
     *         the properties
     *
     * @return the property
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> InjectorProperty<T> getProperty(@NonNls String key, Class<T> expectedType, InjectorProperty<?>... properties) {
        for (InjectorProperty<?> property : properties) {
            if (property.getKey().equals(key)
                    && null != property.getObject()
                    && isAssignableFrom(expectedType, property.getObject().getClass())
            ) {
                return (InjectorProperty<T>) property;
            }
        }
        return null;
    }

    /**
     * Gets sub properties.
     *
     * @param <T>
     *         the type parameter
     * @param propertyFilter
     *         the property filter
     * @param properties
     *         the properties
     *
     * @return the sub properties
     */
    @SuppressWarnings("unchecked")
    public static <T extends InjectorProperty<?>> List<T> getSubProperties(Class<T> propertyFilter, InjectorProperty<?>... properties) {
        List<T> values = emptyList();
        for (InjectorProperty<?> property : properties) {
            if (isAssignableFrom(propertyFilter, property.getClass())) values.add((T) property);
        }
        return values;
    }

    /**
     * Try create from raw exceptional.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param valueCollector
     *         the value collector
     * @param inject
     *         the inject
     *
     * @return the exceptional
     */
    public static <T> Exceptional<T> tryCreateFromRaw(Class<T> type, Function<Field, Object> valueCollector, boolean inject) {
        return tryCreate(type, valueCollector, inject, Provision.FIELD);
    }

    /**
     * Try create exceptional.
     *
     * @param <T>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param type
     *         the type
     * @param valueCollector
     *         the value collector
     * @param inject
     *         the inject
     * @param provision
     *         the provision
     *
     * @return the exceptional
     */
    @SuppressWarnings("unchecked")
    public static <T, A> Exceptional<T> tryCreate(Class<T> type, Function<A, Object> valueCollector, boolean inject, Provision provision) {
        T instance = inject ? Selene.getInstance(type) : getInstance(type);
        if (null != instance)
            try {
                for (Field field : type.getDeclaredFields()) {
                    if (!field.isAccessible()) field.setAccessible(true);
                    if (field.isAnnotationPresent(Ignore.class)) continue;
                    Object value;
                    if (Provision.FIELD == provision) {
                        value = valueCollector.apply((A) field);
                    } else {
                        String fieldName = processFieldName(field);
                        value = valueCollector.apply((A) fieldName);
                    }
                    if (null == value) continue;

                    boolean useFieldDirect = true;
                    if (field.isAnnotationPresent(Property.class)) {
                        Property property = field.getAnnotation(Property.class);

                        //noinspection CallToSuspiciousStringMethod
                        if (!"".equals(property.setter()) && hasMethod(type, property.setter())) {
                            Class<?> parameterType = field.getType();
                            if (isNotVoid(property.accepts())) parameterType = property.accepts();

                            Method method = type.getMethod(property.setter(), parameterType);
                            method.invoke(instance, value);
                            useFieldDirect = false;
                        }
                    }

                    if (useFieldDirect && isAssignableFrom(field.getType(), value.getClass()))
                        field.set(instance, value);
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassCastException e) {
                return Exceptional.of(e);
            }
        return Exceptional.ofNullable(instance);
    }

    /**
     * Gets instance.
     *
     * @param <T>
     *         the type parameter
     * @param clazz
     *         the clazz
     *
     * @return the instance
     */
    public static <T> T getInstance(Class<T> clazz) {
        try {
            Constructor<T> ctor = clazz.getConstructor();
            return ctor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return Selene.getInstance(clazz);
        }
    }

    /**
     * Process field name string.
     *
     * @param field
     *         the field
     *
     * @return the string
     */
    public static String processFieldName(Field field) {
        String fieldName = field.getName();
        if (field.isAnnotationPresent(Property.class))
            fieldName = field.getAnnotation(Property.class).value();
        return fieldName;
    }

    /**
     * Is not void boolean.
     *
     * @param type
     *         the type
     *
     * @return the boolean
     */
    public static boolean isNotVoid(Class<?> type) {
        return !(type.equals(Void.class) || type == Void.TYPE);
    }

    /**
     * Try create from map exceptional.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param map
     *         the map
     *
     * @return the exceptional
     */
    public static <T> Exceptional<T> tryCreateFromMap(Class<T> type, Map<String, Object> map) {
        return tryCreateFromProcessed(type, key -> map.getOrDefault(key, null), true);
    }

    /**
     * Try create from processed exceptional.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param valueCollector
     *         the value collector
     * @param inject
     *         the inject
     *
     * @return the exceptional
     */
    public static <T> Exceptional<T> tryCreateFromProcessed(Class<T> type, Function<String, Object> valueCollector, boolean inject) {
        return tryCreate(type, valueCollector, inject, Provision.FIELD_NAME);
    }

    /**
     * Is either assignable from boolean.
     *
     * @param to
     *         the to
     * @param from
     *         the from
     *
     * @return the boolean
     */
    public static boolean isEitherAssignableFrom(Class<?> to, Class<?> from) {
        return isAssignableFrom(from, to) || isAssignableFrom(to, from);
    }

    /**
     * Gets field property name.
     *
     * @param field
     *         the field
     *
     * @return the field property name
     */
    public static String getFieldPropertyName(Field field) {
        return field.isAnnotationPresent(Property.class)
                ? field.getAnnotation(Property.class).value()
                : field.getName();
    }

    /**
     * Gets static fields.
     *
     * @param type
     *         the type
     *
     * @return the static fields
     */
    public static Collection<Field> getStaticFields(Class<?> type) {
        Field[] declaredFields = type.getDeclaredFields();
        Collection<Field> staticFields = emptyList();
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                staticFields.add(field);
            }
        }
        return staticFields;
    }

    /**
     * Gets enum values.
     *
     * @param type
     *         the type
     *
     * @return the enum values
     */
    public static Collection<? extends Enum<?>> getEnumValues(Class<?> type) {
        if (!type.isEnum()) return emptyList();
        Collection<Enum<?>> constants = emptyList();
        try {
            Field f = type.getDeclaredField("$VALUES");
            if (!f.isAccessible()) f.setAccessible(true);
            Object o = f.get(null);
            Enum<?>[] e = (Enum<?>[]) o;
            constants.addAll(Arrays.asList(e));
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | ClassCastException e) {
            Selene.log().warn("Error obtaining enum constants in " + type.getCanonicalName(), e);
        }
        return constants;
    }

    /**
     * Is annotation present recursively boolean.
     *
     * @param <T>
     *         the type parameter
     * @param method
     *         the method
     * @param annotationClass
     *         the annotation class
     *
     * @return the boolean
     * @throws SecurityException
     *         the security exception
     */
    public static <T extends Annotation> boolean isAnnotationPresentRecursively(Method method, Class<T> annotationClass)
            throws SecurityException {
        return getAnnotationRecursively(method, annotationClass) != null;
    }

    /**
     * Gets annotation recursively.
     *
     * @param <T>
     *         the type parameter
     * @param method
     *         the method
     * @param annotationClass
     *         the annotation class
     *
     * @return the annotation recursively
     * @throws SecurityException
     *         the security exception
     */
    public static <T extends Annotation> T getAnnotationRecursively(Method method, Class<T> annotationClass)
            throws SecurityException {
        T result;
        if (null == (result = method.getAnnotation(annotationClass))) {
            final String name = method.getName();
            final Class<?>[] params = method.getParameterTypes();

            Class<?> declaringClass = method.getDeclaringClass();
            for (Class<?> supertype : getSupertypes(declaringClass)) {
                try {
                    Method m = supertype.getDeclaredMethod(name, params);

                    // Static method doesn't override
                    if (Modifier.isStatic(m.getModifiers())) break;

                    if (null != (result = m.getAnnotation(annotationClass))) break;
                } catch (NoSuchMethodException ignored) {
                    // Current class doesn't have this method
                }
            }
        }
        return result;
    }

    /**
     * Gets supertypes.
     *
     * @param current
     *         the current
     *
     * @return the supertypes
     */
    public static Collection<Class<?>> getSupertypes(Class<?> current) {
        Set<Class<?>> supertypes = emptySet();
        Set<Class<?>> next = emptySet();
        Class<?> superclass = current.getSuperclass();
        if (Object.class != superclass && null != superclass) {
            supertypes.add(superclass);
            next.add(superclass);
        }
        for (Class<?> interfaceClass : current.getInterfaces()) {
            supertypes.add(interfaceClass);
            next.add(interfaceClass);
        }
        for (Class<?> cls : next) {
            supertypes.addAll(getSupertypes(cls));
        }
        return supertypes;
    }

    /**
     * Gets methods recursively.
     *
     * @param cls
     *         the cls
     *
     * @return the methods recursively
     * @throws SecurityException
     *         the security exception
     */
    public static List<Method> getMethodsRecursively(Class<?> cls) throws SecurityException {
        try {
            Set<InternalMethodWrapper> set = SeleneUtils.emptySet();
            Class<?> current = cls;
            do {
                Method[] methods = current.getDeclaredMethods();
                for (Method m : methods) {
                    // if there's already a method that is overriding the current method, add() will return false
                    set.add(new InternalMethodWrapper(m));
                }
            } while (Object.class != (current = current.getSuperclass()) && null != current);

            // Guava equivalent:       Lists.transform(set, w -> w.method);
            // Stream API equivalent:  set.stream().map(w -> w.method).collect(Collectors.toList());
            List<Method> result = SeleneUtils.emptyList();
            for (InternalMethodWrapper methodWrapper : set) result.add(methodWrapper.method);
            return result;
        } catch (Throwable e) {
            return SeleneUtils.emptyList();
        }
    }

    public static <T> Set<T> emptySet() {
        return new HashSet<>();
    }

    /**
     * Gets extension.
     *
     * @param type
     *         the type
     *
     * @return the extension
     */
    @Nullable
    public static Extension getExtension(Class<?> type) {
        if (null == type) return null;
        if (type.equals(Selene.class)) return getExtension(Selene.getInstance(IntegratedExtension.class).getClass());
        Extension extension = type.getAnnotation(Extension.class);
        extension = null != extension ? extension : getExtension(type.getSuperclass());
        if (null == extension)
            extension = Selene.getInstanceSafe(ExtensionManager.class).map(em -> em.getHeader(type).orNull()).orNull();
        return extension;
    }

    /**
     * Run with extension t.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param function
     *         the function
     *
     * @return the t
     */
    @Nullable
    public static <T> T runWithExtension(Class<?> type, Function<Extension, T> function) {
        Extension extension = getExtension(type);
        if (null != extension) return function.apply(extension);
        return null;
    }

    /**
     * Run with extension.
     *
     * @param type
     *         the type
     * @param consumer
     *         the consumer
     */
    public static void runWithExtension(Class<?> type, Consumer<Extension> consumer) {
        Extension extension = getExtension(type);
        if (null != extension) consumer.accept(extension);
    }

    /**
     * Run with instance.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param consumer
     *         the consumer
     */
    public static <T> void runWithInstance(Class<T> type, Consumer<T> consumer) {
        T instance = Selene.getInstance(type);
        if (null != instance) consumer.accept(instance);
    }

    /**
     * Convert to extension id string string.
     *
     * @param name
     *         the name
     * @param extension
     *         the extension
     *
     * @return the string
     */
    public static String convertToExtensionIdString(String name, Extension extension) {
        name = name.toLowerCase(Locale.ROOT)
                .replaceAll("[ .]", "")
                .replaceAll("-", "_");
        return extension.id() + ':' + name;
    }

    /**
     * Persistent key of persistent data key.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param name
     *         the name
     * @param owningClass
     *         the owning class
     *
     * @return the persistent data key
     */
    public static <T> PersistentDataKey<T> persistentKeyOf(Class<T> type, String name, Class<?> owningClass) {
        return persistentKeyOf(type, name, getExtension(owningClass));
    }

    /**
     * Persistent key of persistent data key.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param name
     *         the name
     * @param extension
     *         the extension
     *
     * @return the persistent data key
     */
    @SuppressWarnings("unchecked")
    public static <T> PersistentDataKey<T> persistentKeyOf(Class<T> type, String name, Extension extension) {
        if (!isNbtSupportedType(type))
            throw new UncheckedSeleneException("Unsupported data type for persistent key: " + type.getCanonicalName());

        if (type.equals(String.class))
            return (PersistentDataKey<T>) StringPersistentDataKey.of(name, extension);
        else if (isAssignableFrom(Integer.class, type)) {
            return (PersistentDataKey<T>) IntegerPersistentDataKey.of(name, extension);
        } else if (isAssignableFrom(Double.class, type)) {
            return (PersistentDataKey<T>) DoublePersistentDataKey.of(name, extension);
        }

        return new TypedPersistentDataKey<>(
                name,
                convertToExtensionIdString(name, extension),
                extension,
                type);
    }

    /**
     * Is nbt supported type boolean.
     *
     * @param type
     *         the type
     *
     * @return the boolean
     */
    public static boolean isNbtSupportedType(Class<?> type) {
        boolean supportedType = false;
        for (Class<?> nbtSupportedType : nbtSupportedTypes) {
            if (nbtSupportedType.isAssignableFrom(type)) {
                supportedType = true;
                break;
            }
        }
        return supportedType;
    }

    /**
     * Dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> dynamicKeyOf(BiConsumer<K, A> setter, Function<K, Exceptional<A>> getter) {
        return new Key<K, A>(transactSetter(setter), getter) {
        };
    }

    /**
     * Transact setter bi function.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     *
     * @return the bi function
     */
    public static <K, A> BiFunction<K, A, TransactionResult> transactSetter(BiConsumer<K, A> setter) {
        return transactSetter(setter, TransactionResult.success());
    }

    /**
     * Transact setter bi function.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param defaultResult
     *         the default result
     *
     * @return the bi function
     */
    public static <K, A> BiFunction<K, A, TransactionResult> transactSetter(BiConsumer<K, A> setter, TransactionResult defaultResult) {
        return (t, u) -> {
            setter.accept(t, u);
            return defaultResult;
        };
    }

    /**
     * Checked dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> checkedDynamicKeyOf(BiFunction<K, A, TransactionResult> setter, Function<K, Exceptional<A>> getter) {
        return new Key<K, A>(setter, getter) {
        };
    }

    /**
     * Unsafe dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> unsafeDynamicKeyOf(BiConsumer<K, A> setter, CheckedFunction<K, A> getter) {
        return new Key<K, A>(transactSetter(setter), k -> Exceptional.of(() -> getter.apply(k))) {
        };
    }

    /**
     * Unsafe checked dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> unsafeCheckedDynamicKeyOf(BiFunction<K, A, TransactionResult> setter, CheckedFunction<K, A> getter) {
        return new Key<K, A>(setter, k -> Exceptional.of(() -> getter.apply(k))) {
        };
    }

    /**
     * Dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> dynamicKeyOf(BiConsumer<K, A> setter, Function<K, Exceptional<A>> getter, Consumer<K> remover) {
        return new RemovableKey<K, A>(transactSetter(setter), getter, remover) {
        };
    }

    /**
     * Checked dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> checkedDynamicKeyOf(BiFunction<K, A, TransactionResult> setter, Function<K, Exceptional<A>> getter, Consumer<K> remover) {
        return new RemovableKey<K, A>(setter, getter, remover) {
        };
    }

    /**
     * Unsafe dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> unsafeDynamicKeyOf(BiConsumer<K, A> setter, CheckedFunction<K, A> getter, Consumer<K> remover) {
        return new RemovableKey<K, A>(transactSetter(setter), k -> Exceptional.of(() -> getter.apply(k)), remover) {
        };
    }

    /**
     * Unsafe checked dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> unsafeCheckedDynamicKeyOf(BiFunction<K, A, TransactionResult> setter, CheckedFunction<K, A> getter, Consumer<K> remover) {
        return new RemovableKey<K, A>(setter, k -> Exceptional.of(() -> getter.apply(k)), remover) {
        };
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
    public <T> T[] merge(T[] arrayOne, T[] arrayTwo) {
        Object[] merged = Stream.of(arrayOne, arrayTwo).flatMap(Stream::of).toArray(Object[]::new);
        return this.convertGenericArray(merged);
    }

    @SuppressWarnings("unchecked")
    private <T> T[] convertGenericArray(Object[] array) {
        try {
            Class<T> type = (Class<T>) array.getClass().getComponentType();
            T[] finalArray = (T[]) Array.newInstance(type, 0);
            return (T[]) addAll(finalArray, array);
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
            return shallowCopy(array2);
        } else if (null == array2) {
            return shallowCopy(array1);
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

    /**
     * The enum Provision.
     */
    public enum Provision {
        /**
         * Field provision.
         */
        FIELD,
        /**
         * Field name provision.
         */
        FIELD_NAME
    }

}
