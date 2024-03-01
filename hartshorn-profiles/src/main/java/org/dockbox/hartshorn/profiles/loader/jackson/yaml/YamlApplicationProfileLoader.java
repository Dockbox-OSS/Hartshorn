package org.dockbox.hartshorn.profiles.loader.jackson.yaml;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.loader.ProfilePropertiesLoader;
import org.dockbox.hartshorn.profiles.loader.ResourceLookupApplicationProfileLoader;
import org.dockbox.hartshorn.util.resources.ResourceLookup;

public class YamlApplicationProfileLoader extends ResourceLookupApplicationProfileLoader {

    public YamlApplicationProfileLoader(ResourceLookup resourceLookup) {
        super(resourceLookup);
    }

    @Override
    protected String fileName(ApplicationProfile parentProfile, String profileName) {
        String prefix = parentProfile != null
                ? parentProfile + "."
                : "";
        return "%s%s.yml".formatted(prefix, profileName);
    }

    @Override
    protected ProfilePropertiesLoader propertiesLoader() {
        return new YamlProfilePropertiesLoader();
    }
}
