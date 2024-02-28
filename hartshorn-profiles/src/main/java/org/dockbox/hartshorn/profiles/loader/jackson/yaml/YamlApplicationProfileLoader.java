package org.dockbox.hartshorn.profiles.loader.jackson.yaml;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.loader.ApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.loader.ProfilePropertiesLoader;
import org.dockbox.hartshorn.profiles.loader.ResourceLookupApplicationProfileLoader;
import org.dockbox.hartshorn.util.resources.ResourceLookup;

public class YamlApplicationProfileLoader extends ResourceLookupApplicationProfileLoader {

    public YamlApplicationProfileLoader(
            ResourceLookup resourceLookup,
            String profileName
    ) {
        super(resourceLookup, profileName);
    }

    public YamlApplicationProfileLoader(
            ResourceLookup resourceLookup,
            ApplicationProfile parent,
            String profileName
    ) {
        super(resourceLookup, parent, profileName);
    }

    @Override
    protected String fileName(String profileName) {
        String prefix = this.parent() != null
                ? this.parent().name() + "."
                : "";
        return "%s%s.yml".formatted(prefix, profileName);
    }

    @Override
    protected ProfilePropertiesLoader propertiesLoader() {
        return new YamlProfilePropertiesLoader();
    }

    @Override
    protected ApplicationProfileLoader childLoader(ApplicationProfile parentProfile, String profileName) {
        return new YamlApplicationProfileLoader(this.resourceLookup(), parentProfile, profileName);
    }
}
