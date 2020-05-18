package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.internal.Utility;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.types.time.TimeDifference;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 The type Time utils.
 */
@Utility("Time parsing and scheduling")
public abstract class TimeUtils {

    private static final Map<Object, Map<UUID, LocalDateTime>> playerRegistrationsPerModule = new HashMap<>();

    /**
     Local date time from millis local date time.

     @param millis
     the millis

     @return the local date time
     */
    public static LocalDateTime localDateTimeFromMillis(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    public static void registerUuidTimeout(UUID uuid, Object module) {
        if (!playerRegistrationsPerModule.containsKey(module))
            playerRegistrationsPerModule.put(module, new HashMap<>());
        playerRegistrationsPerModule.get(module).put(uuid, LocalDateTime.now());
    }

    public static Optional<TimeDifference> getTimeSinceLastUuidTimeout(UUID uuid, Object module) {
        if (!playerRegistrationsPerModule.containsKey(module)) return Optional.empty();
        if (!playerRegistrationsPerModule.get(module).containsKey(uuid)) return Optional.empty();

        LocalDateTime lastTimeout = playerRegistrationsPerModule.get(module).get(uuid);
        if (lastTimeout.isAfter(LocalDateTime.now())) return Optional.empty();

        long millis = ChronoUnit.MILLIS.between(lastTimeout, LocalDateTime.now());
        return Optional.ofNullable(getDifferenceFromMillis(millis));
    }

    /**
     Gets difference from seconds.

     @param seconds
     the seconds

     @return the difference from seconds
     */
    public static TimeDifference getDifferenceFromSeconds(long seconds) {
        return getDifferenceFromMillis(seconds * 1000);
    }

    /**
     Gets difference from millis.

     @param millis
     the millis

     @return the difference from millis
     */
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

    /**
     Gets short time difference string.

     @param time
     the time

     @return the short time difference string
     */
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

    /**
     Schedule scheduler.

     @return the scheduler
     */
    public abstract Scheduler schedule();

    /**
     The type Scheduler.
     */
    public abstract static class Scheduler {

        /**
         Async scheduler.

         @return the scheduler
         */
        public abstract Scheduler async();

        /**
         Name scheduler.

         @param name
         the name

         @return the scheduler
         */
        public abstract Scheduler name(String name);

        /**
         Delay scheduler.

         @param delay
         the delay
         @param unit
         the unit

         @return the scheduler
         */
        public abstract Scheduler delay(long delay, TimeUnit unit);

        /**
         Delay ticks scheduler.

         @param delay
         the delay

         @return the scheduler
         */
        public abstract Scheduler delayTicks(long delay);

        /**
         Interval scheduler.

         @param delay
         the delay
         @param unit
         the unit

         @return the scheduler
         */
        public abstract Scheduler interval(long delay, TimeUnit unit);

        /**
         Interval ticks scheduler.

         @param delay
         the delay

         @return the scheduler
         */
        public abstract Scheduler intervalTicks(long delay);

        /**
         Execute scheduler.

         @param runnable
         the runnable

         @return the scheduler
         */
        public abstract Scheduler execute(Runnable runnable);

        /**
         Submit.
         */
        public abstract void submit();

    }

}
