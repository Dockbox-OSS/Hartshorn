package com.darwinreforged.servermodifications.util;

import com.darwinreforged.servermodifications.plugins.DavePluginWrapper;

import java.io.*;
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
            DavePluginWrapper.getLogger().error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(el -> DavePluginWrapper.getLogger().error(el.toString()));
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
            DavePluginWrapper.getLogger().error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(el -> DavePluginWrapper.getLogger().error(el.toString()));
        }
    }

    private String parseProperties(String property) {
        return properties.getProperty(property);
    }
}
