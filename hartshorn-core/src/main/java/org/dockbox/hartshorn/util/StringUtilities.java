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

package org.dockbox.hartshorn.util;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.option.Option;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtilities {

    private static final Pattern minorTimeString = Pattern.compile("^\\d+$");
    private static final Pattern timeString = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");
    private static final int secondsInMinute = 60;
    private static final int secondsInHour = 60 * StringUtilities.secondsInMinute;
    private static final int secondsInDay = 24 * StringUtilities.secondsInHour;
    private static final int secondsInWeek = 7 * StringUtilities.secondsInDay;

    public static String capitalize(final String value) {
        return StringUtilities.empty(value)
                ? value
                : (value.substring(0, 1).toUpperCase() + value.substring(1));
    }

    public static boolean empty(final String value) {
        return null == value || value.isEmpty();
    }

    public static boolean notEmpty(final String value) {
        return null != value && !value.isEmpty();
    }

    public static String emptyIfNull(final String value) {
        return null == value ? "" : value;
    }

    public static String nullIfEmpty(final String value) {
        return StringUtilities.empty(value) ? null : value;
    }

    public static String strip(final String s) {
        return s.replaceAll("[\n\r ]+", "").trim();
    }

    public static Option<Duration> durationOf(final String in) {
        // First, if just digits, return the number in seconds.

        if (StringUtilities.minorTimeString.matcher(in).matches()) {
            return Option.of(Duration.ofSeconds(Long.parseUnsignedLong(in)));
        }

        final Matcher m = StringUtilities.timeString.matcher(in);
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

    private static long durationAmount(@Nullable final String g, final int multiplier) {
        if (null != g && !g.isEmpty()) {
            return multiplier * Long.parseUnsignedLong(g);
        }

        return 0;
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

    public static String format(final String format, final Object... args) {
        if (0 == args.length) return format;
        final Map<String, String> map = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            final String arg = "" + args[i];
            map.put(String.format("{%d}", i), arg);
        }
        return StringUtilities.format(format, map);
    }

    public static String format(final String string, final Map<String, String> replacements) {
        final StringBuilder sb = new StringBuilder(string);
        int size = string.length();
        for (final Entry<String, String> entry : replacements.entrySet()) {
            if (0 == size) {
                break;
            }
            final String key = entry.getKey();
            final String value = entry.getValue();
            int nextSearchStart;
            int start = sb.indexOf(key, 0);
            while (-1 < start) {
                final int end = start + key.length();
                nextSearchStart = start + value.length();
                sb.replace(start, end, value);
                size -= end - start;
                start = sb.indexOf(key, nextSearchStart);
            }
        }
        return sb.toString();
    }
}
