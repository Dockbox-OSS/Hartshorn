package com.darwinreforged.server.core.types.time;

/**
 The type Time difference.
 */
public class TimeDifference {

    private long millis;
    private long seconds;
    private long minutes;
    private long hours;
    private long days;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeDifference)) return false;

        TimeDifference that = (TimeDifference) o;

        if (getMillis() != that.getMillis()) return false;
        if (getSeconds() != that.getSeconds()) return false;
        if (getMinutes() != that.getMinutes()) return false;
        if (getHours() != that.getHours()) return false;
        return getDays() == that.getDays();
    }

    @Override
    public int hashCode() {
        long result = getMillis();
        result = 31 * result + getSeconds();
        result = 31 * result + getMinutes();
        result = 31 * result + getHours();
        result = 31 * result + getDays();
        return result < Integer.MAX_VALUE ? (int) result : -1;
    }

    /**
     Gets millis.

     @return the millis
     */
    public long getMillis() {
        return millis;
    }

    /**
     Sets millis.

     @param millis
     the millis
     */
    public void setMillis(long millis) {
        this.millis = millis;
    }

    /**
     Gets seconds.

     @return the seconds
     */
    public long getSeconds() {
        return seconds;
    }

    /**
     Sets seconds.

     @param seconds
     the seconds
     */
    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    /**
     Gets minutes.

     @return the minutes
     */
    public long getMinutes() {
        return minutes;
    }

    /**
     Sets minutes.

     @param minutes
     the minutes
     */
    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    /**
     Gets hours.

     @return the hours
     */
    public long getHours() {
        return hours;
    }

    /**
     Sets hours.

     @param hours
     the hours
     */
    public void setHours(long hours) {
        this.hours = hours;
    }

    /**
     Gets days.

     @return the days
     */
    public long getDays() {
        return days;
    }

    /**
     Sets days.

     @param days
     the days
     */
    public void setDays(long days) {
        this.days = days;
    }

    /**
     Instantiates a new Time difference.

     @param millis
     the millis
     @param seconds
     the seconds
     @param minutes
     the minutes
     @param hours
     the hours
     @param days
     the days
     */
    public TimeDifference(long millis, long seconds, long minutes, long hours, long days) {
        this.millis = millis;
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
        this.days = days;
    }
}
