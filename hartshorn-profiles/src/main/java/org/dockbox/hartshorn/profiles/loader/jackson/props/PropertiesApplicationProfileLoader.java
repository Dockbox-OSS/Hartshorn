package org.dockbox.hartshorn.profiles.loader.jackson.props;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.loader.ApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.loader.ProfilePropertiesLoader;
import org.dockbox.hartshorn.profiles.loader.ResourceLookupApplicationProfileLoader;
import org.dockbox.hartshorn.util.resources.ResourceLookup;

public class PropertiesApplicationProfileLoader extends ResourceLookupApplicationProfileLoader {

    public PropertiesApplicationProfileLoader(
            ResourceLookup resourceLookup,
            String profileName
    ) {
        super(resourceLookup, profileName);
    }

    public PropertiesApplicationProfileLoader(
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
        return "%s%s.properties".formatted(prefix, profileName);
    }

    @Override
    protected ProfilePropertiesLoader propertiesLoader() {
        return new PropertiesProfilePropertiesLoader();
    }

    @Override
    protected ApplicationProfileLoader childLoader(ApplicationProfile parentProfile, String profileName) {
        return new PropertiesApplicationProfileLoader(this.resourceLookup(), parentProfile, profileName);
    }
}
