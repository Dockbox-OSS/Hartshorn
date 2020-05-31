package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("time")
public class TimeTranslations {

    public static final Translation TIME_DAYS_AGO = Translation.create("ago_days", "$1{0} $2days ago");
    public static final Translation TIME_YESTERDAY = Translation.create("ago_yesterday", "$1Yesterday");
    public static final Translation TIME_HOURS_AGO = Translation.create("ago_hours", "$1{0} $2hours ago");
    public static final Translation TIME_HOUR_AGO = Translation.create("ago_hour", "$1One $2hour ago");
    public static final Translation TIME_MINUTES_AGO = Translation.create("ago_minutes", "$1{0} $2minutes ago");
    public static final Translation TIME_MINUTE_AGO = Translation.create("ago_minute", "$1One $2minute ago");
    public static final Translation TIME_SECONDS_AGO = Translation.create("ago_seconds", "$1{0} $2seconds ago");
    public static final Translation TIME_JUST_NOW = Translation.create("ago_just_now", "$1Just now");

    private TimeTranslations() {
    }
}
