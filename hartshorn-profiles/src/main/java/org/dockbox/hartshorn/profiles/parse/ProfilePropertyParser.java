package org.dockbox.hartshorn.profiles.parse;

import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.util.option.Option;

@FunctionalInterface
public interface ProfilePropertyParser<T> {

    Option<T> parse(ProfileProperty property);

}
