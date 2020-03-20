package com.darwinreforged.servermodifications.objects;

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

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }

    public TimeDifference(long millis, long seconds, long minutes, long hours, long days) {
        this.millis = millis;
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
        this.days = days;
    }
}
