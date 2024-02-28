package org.dockbox.hartshorn.profiles.loader.jackson.props;

import org.dockbox.hartshorn.profiles.loader.jackson.JacksonApplicationProfileLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

public class PropertiesProfilePropertiesLoader extends JacksonApplicationProfileLoader {

    @Override
    protected ObjectMapper createObjectMapper() {
        return new JavaPropsMapper();
    }
}
