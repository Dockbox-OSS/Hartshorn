package test.org.dockbox.hartshorn.profiles;

import java.util.Set;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.SimpleComposableProfileHolder;
import org.dockbox.hartshorn.profiles.loader.ApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.loader.CompositeProfileLoader;
import org.dockbox.hartshorn.profiles.loader.jackson.props.PropertiesApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.loader.jackson.yaml.YamlApplicationProfileLoader;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.resources.ClassLoaderClasspathResourceLocator;
import org.dockbox.hartshorn.util.resources.ClassPathResourceLookupStrategy;
import org.dockbox.hartshorn.util.resources.FallbackResourceLookup;
import org.dockbox.hartshorn.util.resources.ResourceLookup;
import org.junit.jupiter.api.Test;

public class ResourceLoaderTest {

    @Test
    void name() throws ApplicationException {
        ClassLoaderClasspathResourceLocator locator = new ClassLoaderClasspathResourceLocator();
        ClassPathResourceLookupStrategy strategy = new ClassPathResourceLookupStrategy(locator);
        ResourceLookup resourceLookup = new FallbackResourceLookup(strategy);
        ApplicationProfileLoader yamlProfileLoader = new YamlApplicationProfileLoader(resourceLookup, "application");
        ApplicationProfileLoader propsProfileLoader = new PropertiesApplicationProfileLoader(resourceLookup, "application");
        CompositeProfileLoader profileLoader = new CompositeProfileLoader(Set.of(yamlProfileLoader, propsProfileLoader));

        Set<ApplicationProfile> applicationProfiles = profileLoader.loadProfiles();
        SimpleComposableProfileHolder holder = new SimpleComposableProfileHolder(applicationProfiles);
        ProfilePropertyRegistry registry = holder.registry();
        Set<ProfileProperty> properties = registry.properties();
        for(ProfileProperty property : properties) {
            System.out.println(property.name() + " = " + property.rawValue());
        }
    }
}
