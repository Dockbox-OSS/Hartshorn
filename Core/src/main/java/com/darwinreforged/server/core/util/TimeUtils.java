package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.resources.Translations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public abstract class TimeUtils {

    public static LocalDateTime localDateTimeFromMillis(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    public static TimeDifference getDifferenceFromSeconds(long seconds) {
        return getDifferenceFromMillis(seconds * 1000);
    }

    public static TimeDifference getDifferenceFromMillis(long millis) {
        long now = System.currentTimeMillis();
        if (millis > now || millis <= 0) return null;

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return new TimeDifference(millis, seconds, minutes, hours, days);
    }

    public static String getShortTimeDifferenceString(long time) {
        TimeDifference difference;
        if (time < 1000000000000L) difference = getDifferenceFromSeconds(time);
        else difference = getDifferenceFromMillis(time);

        if (difference.getDays() > 1) return Translations.TIME_DAYS_AGO.f(difference.getDays());
        if (difference.getDays() > 0) return Translations.TIME_YESTERDAY.s();
        if (difference.getHours() > 1) return Translations.TIME_HOURS_AGO.f(difference.getHours());
        if (difference.getHours() > 0) return Translations.TIME_HOUR_AGO.s();
        if (difference.getMinutes() > 1) return Translations.TIME_MINUTES_AGO.f(difference.getMinutes());
        if (difference.getMinutes() > 0) return Translations.TIME_MINUTE_AGO.s();
        if (difference.getSeconds() > 20) return Translations.TIME_SECONDS_AGO.f(difference.getSeconds());

        return Translations.TIME_JUST_NOW.s();
    }

    public abstract Scheduler schedule();

    public abstract static class Scheduler {

        public abstract Scheduler async();

        public abstract Scheduler name(String name);

        public abstract Scheduler delay(long delay, TimeUnit unit);

        public abstract Scheduler delayTicks(long delay);

        public abstract Scheduler interval(long delay, TimeUnit unit);

        public abstract Scheduler intervalTicks(long delay);

        public abstract Scheduler execute(Runnable runnable);

        public abstract void submit();

    }

}
