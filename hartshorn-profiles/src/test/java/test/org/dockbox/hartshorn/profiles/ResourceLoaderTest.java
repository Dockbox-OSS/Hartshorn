package test.org.dockbox.hartshorn.profiles;

import java.util.List;
import java.util.Set;
import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.SimpleComposableProfileHolder;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.profiles.loader.ApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.loader.CompositeProfileLoader;
import org.dockbox.hartshorn.profiles.loader.jackson.props.PropertiesApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.loader.jackson.yaml.YamlApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.parse.support.ListProfilePropertyParser;
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
        ApplicationProfileLoader yamlProfileLoader = new YamlApplicationProfileLoader(resourceLookup);
        ApplicationProfileLoader propsProfileLoader = new PropertiesApplicationProfileLoader(resourceLookup);
        CompositeProfileLoader profileLoader = new CompositeProfileLoader(List.of(yamlProfileLoader, propsProfileLoader));

        Set<ApplicationProfile> applicationProfiles = profileLoader.loadProfile("application");
        SimpleComposableProfileHolder holder = new SimpleComposableProfileHolder(applicationProfiles);
        ProfilePropertyRegistry registry = holder.registry();
        List<ValueProfileProperty> properties = registry.properties();
        for(ValueProfileProperty property : properties) {
            System.out.println(property.name() + " = " + property.rawValue().orElse("???"));
        }

        ProfileProperty property = registry.property("sample.list").get();
        List<ProfileProperty> list = property.parseValue(new ListProfilePropertyParser()).get();
        for (ProfileProperty profileProperty : list) {
            ValueProfileProperty valueProfileProperty = (ValueProfileProperty) profileProperty;
            System.out.println(profileProperty.name() + " = " + valueProfileProperty.rawValue().orElse("???"));
        }
    }
}
