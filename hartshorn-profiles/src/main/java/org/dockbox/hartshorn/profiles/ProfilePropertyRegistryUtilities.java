package org.dockbox.hartshorn.profiles;

import java.util.Properties;

public class ProfilePropertyRegistryUtilities {

    public static Properties toProperties(ProfilePropertyRegistry registry) {
        Properties properties = new Properties();
        for (ProfileProperty property : registry.properties()) {
            properties.setProperty(property.name(), property.rawValue());
        }
        return properties;
    }
}
