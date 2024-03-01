package org.dockbox.hartshorn.profiles.loader.jackson.props;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.loader.ProfilePropertiesLoader;
import org.dockbox.hartshorn.profiles.loader.ResourceLookupApplicationProfileLoader;
import org.dockbox.hartshorn.util.resources.ResourceLookup;

public class PropertiesApplicationProfileLoader extends ResourceLookupApplicationProfileLoader {

    public PropertiesApplicationProfileLoader(ResourceLookup resourceLookup) {
        super(resourceLookup);
    }

    @Override
    protected String fileName(ApplicationProfile parentProfile, String profileName) {
        String prefix = parentProfile != null
                ? parentProfile + "."
                : "";
        return "%s%s.properties".formatted(prefix, profileName);
    }

    @Override
    protected ProfilePropertiesLoader propertiesLoader() {
        return new PropertiesProfilePropertiesLoader();
    }
}
