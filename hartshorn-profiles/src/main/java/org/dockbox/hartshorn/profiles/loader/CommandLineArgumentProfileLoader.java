package org.dockbox.hartshorn.profiles.loader;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.SimpleApplicationProfile;
import org.dockbox.hartshorn.profiles.SimpleProfileProperty;
import org.dockbox.hartshorn.profiles.SimpleProfilePropertyRegistry;

public class CommandLineArgumentProfileLoader implements ApplicationProfileLoader {

    private final ApplicationArgumentParser argumentParser = new StandardApplicationArgumentParser();
    private final List<String> arguments;

    public CommandLineArgumentProfileLoader(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public Set<ApplicationProfile> loadProfile(ApplicationProfile parentProfile, String profileName) {
        if (parentProfile != null) {
            // CommandLineArgumentProfileLoader is not a child profile capable loader
            return Collections.emptySet();
        }

        Properties properties = argumentParser.parse(this.arguments);
        Set<ProfileProperty> profileProperties = properties.entrySet().stream()
                .map(entry -> new SimpleProfileProperty(entry.getKey().toString(), entry.getValue().toString()))
                .collect(Collectors.toSet());

        ProfilePropertyRegistry propertyRegistry = new SimpleProfilePropertyRegistry(Set.of(), profileProperties);
        ApplicationProfile profile = new SimpleApplicationProfile(profileName, propertyRegistry, null);
        return Set.of(profile);
    }
}
