/*
 * Copyright 2019-2023 the original author or authors.
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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtilities {

    private static final Pattern minorTimeString = Pattern.compile("^\\d+$");
    private static final Pattern timeString = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");
    private static final int secondsInMinute = 60;
    private static final int secondsInHour = 60 * StringUtilities.secondsInMinute;
    private static final int secondsInDay = 24 * StringUtilities.secondsInHour;
    private static final int secondsInWeek = 7 * StringUtilities.secondsInDay;

    public static String capitalize(String value) {
        return StringUtilities.empty(value)
                ? value
                : (value.substring(0, 1).toUpperCase() + value.substring(1));
    }

    public static boolean empty(CharSequence value) {
        return null == value || value.isEmpty();
    }

    public static boolean notEmpty(CharSequence value) {
        return null != value && !value.isEmpty();
    }

    public static String emptyIfNull(String value) {
        return null == value ? "" : value;
    }

    public static String nullIfEmpty(String value) {
        return StringUtilities.empty(value) ? null : value;
    }

    public static String strip(String value) {
        return value.replaceAll("[\n\r\t ]+", "").trim();
    }

    public static Option<Duration> durationOf(String value) {
        // First, if just digits, return the number in seconds.

        if (StringUtilities.minorTimeString.matcher(value).matches()) {
            return Option.of(Duration.ofSeconds(Long.parseUnsignedLong(value)));
        }

        Matcher m = StringUtilities.timeString.matcher(value);
        if (m.matches()) {
            long time = StringUtilities.durationAmount(m.group(2), StringUtilities.secondsInWeek);
            time += StringUtilities.durationAmount(m.group(4), StringUtilities.secondsInDay);
            time += StringUtilities.durationAmount(m.group(6), StringUtilities.secondsInHour);
            time += StringUtilities.durationAmount(m.group(8), StringUtilities.secondsInMinute);
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

    public static String[] splitCapitals(String value) {
        return value.split("(?=\\p{Lu})");
    }

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
        return ((currentIndex > 0) || (length < value.length())) ? value.substring(currentIndex, length) : value;
    }

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
}
