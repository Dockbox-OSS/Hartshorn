package org.dockbox.hartshorn.profiles.loader.jackson.yaml;

import org.dockbox.hartshorn.profiles.loader.jackson.JacksonApplicationProfileLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class YamlProfilePropertiesLoader extends JacksonApplicationProfileLoader {

    @Override
    protected ObjectMapper createObjectMapper() {
        return new YAMLMapper();
    }
}
