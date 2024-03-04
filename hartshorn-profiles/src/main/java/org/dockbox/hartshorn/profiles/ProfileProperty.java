package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.util.option.Option;

public sealed interface ProfileProperty permits ValueProfileProperty, CompositeProfileProperty {

    String name();

    <T> Option<T> parseValue(ProfilePropertyParser<T> parser);

    <T> T parseValue(ProfilePropertyParser<T> parser, T defaultValue);

}
