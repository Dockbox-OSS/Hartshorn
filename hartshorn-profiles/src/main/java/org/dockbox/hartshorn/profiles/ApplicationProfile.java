package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.util.option.Option;

public interface ApplicationProfile {

    Option<ApplicationProfile> parent();

    String name();

    ProfilePropertyRegistry registry();

}
