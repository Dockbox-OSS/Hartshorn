package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.SingleConfiguredProperty;
import org.dockbox.hartshorn.properties.ValueProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleProfilePropertyRegistryAggregator implements ProfilePropertyRegistryAggregator {

    @Override
    public PropertyRegistry aggregate(ProfileRegistry profileRegistry) {
        Map<String, ConfiguredProperty> properties = new HashMap<>();
        List<EnvironmentProfile> profiles = profileRegistry.profiles();
        for (int i = profiles.size() - 1; i >= 0; i--) {
            EnvironmentProfile profile = profiles.get(i);
            PropertyRegistry propertyRegistry = profile.propertyRegistry();
            List<String> keys = propertyRegistry.keys();
            for (String key : keys) {
                // Don't override properties defined in higher priority profiles
                if (!properties.containsKey(key)) {
                    // TODO: Keys only returns first element (e.g. x instead of x.y). We need to either expose all
                    //  properties by default (as seen in AbstractMapProperty#properties), or have a way to retrieve
                    //  all properties recursively.
                    ValueProperty valueProperty = propertyRegistry.get(key)
                            .orElseThrow(() -> new IllegalStateException("Property " + key + " not found in registry"));
                    properties.put(key, new SingleConfiguredProperty(valueProperty.name(),
                            valueProperty.value().orElseThrow(() -> new IllegalStateException("Property " + key + " has no value"))));
                }
            }
        }
        return new MapPropertyRegistry(properties);
    }
}
