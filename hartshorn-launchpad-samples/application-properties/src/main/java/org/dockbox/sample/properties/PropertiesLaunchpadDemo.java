package org.dockbox.sample.properties;

import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.condition.support.RequiresProperty;
import org.dockbox.hartshorn.launchpad.ApplicationStarter;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.profiles.EnvironmentProfile;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.util.ApplicationException;
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class PropertiesLaunchpadDemo {

    public static void main(String[] args) throws ApplicationException {
        // Uses properties defined in application.yml, and application-demo.yml due to the demo profile being active
        // via hartshorn.profiles in application.yml.
        HartshornApplication.create(args).close();

        // Uses properties defined in application.yml, and application-cli.yml due to the cli profile being active
        // via hartshorn.profiles passed through arguments.
        HartshornApplication.create("hartshorn.profiles=cli").close();
    }

    @Singleton
    // Properties can be used to conditionally enable components. Here, the ApplicationStarter will only be
    // registered if the property 'sample.condition' is set to 'true'. Any other value, or if the property is missing,
    // will prevent this component from being registered.
    @RequiresProperty(name = "sample.condition", withValue = "true")
    public ApplicationStarter applicationStarter(Logger logger, PropertyRegistry propertyRegistry) {
        return applicationContext -> {
            // Properties can be accessed via the PropertyRegistry. You can retrieve the registry as a component,
            // or through the ApplicationContext's environment.
            String greeting = propertyRegistry.get("sample.greeting")
                    .flatMap(ValueProperty::value)
                    .orElseThrow(() -> new IllegalStateException("Property 'sample.greeting' not found"));

            // Due to the presence of hartshorn.profiles in application.yml, the sample.greeting
            // property will resolve to the value defined in application-demo.yml.
            logger.info("Greeting: {}", greeting);

            // Active profiles can be inspected by casting the PropertyRegistry to ProfilePropertyRegistry
            if (propertyRegistry instanceof ProfilePropertyRegistry profilePropertyRegistry) {
                List<EnvironmentProfile> profiles = profilePropertyRegistry.profileRegistry().profiles();
                String profilesLabel = profiles.stream()
                        .map(EnvironmentProfile::name)
                        .collect(Collectors.joining(", "));

                // This should log "Active profiles: default, demo". The default profile is always active,
                // and the demo profile is activated via hartshorn.profiles in application.yml.
                logger.info("Active profiles: {}", profilesLabel);
            }
        };
    }
}
