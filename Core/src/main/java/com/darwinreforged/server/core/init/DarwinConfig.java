package com.darwinreforged.server.core.init;

import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.modules.internal.darwin.DarwinServerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DarwinConfig {

    private static final Map<String, ConfigFlag<?>> FLAGS = new HashMap<>();

    public static final ListFlag<String> DISCORD_CHANNEL_WHITELIST = new ListFlag<>("discordChannelWhitelist", new ArrayList<>());
    public static final BooleanFlag LOAD_EXTERNAL_MODULES = new BooleanFlag("loadExternalModules", true);
    public static final BooleanFlag FRIENDLY_ERRORS = new BooleanFlag("useFriendlyErrors", true);

    public DarwinConfig() {
        FileUtils fu = DarwinServer.getUtilChecked(FileUtils.class);
        Map<String, Object> config = fu.getConfigYamlData(DarwinServerModule.class);
        AtomicBoolean written = new AtomicBoolean(config.isEmpty());
        FLAGS.forEach((k, v) -> {
            boolean oneWritten = false;
            if (config.containsKey(k)) {
                Object obj = config.get(k);
                if (v.value.getClass().equals(obj.getClass())) {
                    v.setValue(obj);
                    oneWritten = true;
                    written.set(true);
                }
            }
            if (!oneWritten) config.put(v.key, v.value);
        });
        if (written.get()) fu.writeConfigYaml(config, DarwinServerModule.class);
    }

    public static abstract class ConfigFlag<T> {

        private final String key;
        private T value;

        public ConfigFlag(String key, T defVal) {
            this.key = key;
            this.value = defVal;
            DarwinConfig.FLAGS.put(key, this);
        }

        @SuppressWarnings("unchecked")
        void setValue(Object value) {
            this.value = (T) value;
        }

        public T get() {
            return value;
        }
    }

    public static class BooleanFlag extends ConfigFlag<Boolean> {
        public BooleanFlag(String key, Boolean defVal) {
            super(key, defVal);
        }
    }

    public static class StringFlag extends ConfigFlag<String> {
        public StringFlag(String key, String defVal) {
            super(key, defVal);
        }
    }

    public static class ListFlag<T> extends ConfigFlag<List<T>> {
        public ListFlag(String key, List<T> defVal) {
            super(key, defVal);
        }
    }
}
