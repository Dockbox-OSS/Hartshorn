package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.util.option.Option;

public interface ProfilePropertyParser<T> {

    Option<T> parse(ProfileProperty property);

}
