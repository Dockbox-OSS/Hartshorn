package com.darwinreforged.server.modules.extensions.chat.dave;

import com.darwinreforged.server.core.init.DarwinServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DaveConfigurationUtil {

    private Properties properties;
    private File file;

    public DaveConfigurationUtil(File file) {
        try {
            this.file = file;
            properties = new Properties();
            ThreadLocal<InputStream> inputStream = new ThreadLocal<>();
            inputStream.set(new FileInputStream(file));
            Reader stream = new InputStreamReader(inputStream.get());
            properties.load(stream);
        } catch (IOException e) {
            DarwinServer.error("Failed to ");
            Arrays.stream(e.getStackTrace()).forEach(el -> DarwinServer.getLogger().error(el.toString()));
        }
    }

    public Map<String, String> getAll() {
        Map<String, String> propertySet = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            String value = parseProperties(key);
            propertySet.put(key, value);
        }
        return propertySet;
    }

    public String get(String property) {
        String result = parseProperties(property);
        if (result != null) return result;
        else return "Failed to process";
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            DarwinServer.getLogger().error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(el -> DarwinServer.getLogger().error(el.toString()));
        }
    }

    private String parseProperties(String property) {
        return properties.getProperty(property);
    }
}
