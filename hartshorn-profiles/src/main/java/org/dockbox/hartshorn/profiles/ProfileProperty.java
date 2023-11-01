package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.util.option.Option;

public interface ProfileProperty {

    String name();

    String rawValue();

    <T> Option<T> tryParseValue(Class<T> type);

    <T> T tryParseValue(Class<T> type, T defaultValue);

    <T> Option<T> parseValue(ProfilePropertyParser<T> parser);

    <T> T parseValue(ProfilePropertyParser<T> parser, T defaultValue);

}
