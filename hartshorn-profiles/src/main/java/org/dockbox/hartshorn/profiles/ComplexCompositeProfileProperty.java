package org.dockbox.hartshorn.profiles;

import java.util.ArrayList;
import java.util.List;
import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.util.option.Option;

public class ComplexCompositeProfileProperty implements CompositeProfileProperty {

    private final String path;
    private final ProfilePropertyRegistry registry;

    public ComplexCompositeProfileProperty(String path, ProfilePropertyRegistry registry) {
        this.path = path;
        this.registry = registry;
    }

    @Override
    public String name() {
        return this.path;
    }

    @Override
    public <T> Option<T> parseValue(ProfilePropertyParser<T> parser) {
        return parser.parse(this);
    }

    @Override
    public <T> T parseValue(ProfilePropertyParser<T> parser, T defaultValue) {
        return parser.parse(this).orElse(defaultValue);
    }

    @Override
    public List<ValueProfileProperty> properties() {
        List<ValueProfileProperty> properties = new ArrayList<>();
        for (ValueProfileProperty property : this.registry.allProperties()) {
            if (property.name().startsWith(this.path)) {
                properties.add(property);
            }
        }
        return properties;
    }
}
