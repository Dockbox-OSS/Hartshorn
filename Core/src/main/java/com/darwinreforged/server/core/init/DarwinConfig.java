package com.darwinreforged.server.core.init;

import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.modules.internal.darwin.DarwinServerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 The type Darwin config.
 */
public class DarwinConfig {

    private static final Map<String, ConfigFlag<?>> FLAGS = new HashMap<>();

    /**
     The constant DISCORD_CHANNEL_WHITELIST.
     */
    public static final ListFlag<String> DISCORD_CHANNEL_WHITELIST = new ListFlag<>("discordChannelWhitelist", new ArrayList<>());
    /**
     The constant LOAD_EXTERNAL_MODULES.
     */
    public static final BooleanFlag LOAD_EXTERNAL_MODULES = new BooleanFlag("loadExternalModules", true);
    /**
     The constant FRIENDLY_ERRORS.
     */
    public static final BooleanFlag FRIENDLY_ERRORS = new BooleanFlag("useFriendlyErrors", true);
    /**
     The constant STACKTRACES.
     */
    public static final BooleanFlag STACKTRACES = new BooleanFlag("useStacktraces", true);
    /**
     The constant BOT_TOKEN.
     */
    public static final StringFlag BOT_TOKEN = new StringFlag("botToken", "[insert token]");

    /**
     Instantiates a new Darwin config.
     */
    public DarwinConfig() {
        FileUtils fu = DarwinServer.getUtilChecked(FileUtils.class);
        Map<String, Object> config = fu.getYamlDataForConfig(DarwinServerModule.class);
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
        if (written.get()) fu.writeYamlDataForConfig(config, DarwinServerModule.class);
    }

    /**
     The type Config flag.

     @param <T>
     the type parameter
     */
    public static abstract class ConfigFlag<T> {

        private final String key;
        private T value;

        /**
         Instantiates a new Config flag.

         @param key
         the key
         @param defVal
         the def val
         */
        public ConfigFlag(String key, T defVal) {
            this.key = key;
            this.value = defVal;
            DarwinConfig.FLAGS.put(key, this);
        }

        /**
         Sets value.

         @param value
         the value
         */
        @SuppressWarnings("unchecked")
        void setValue(Object value) {
            this.value = (T) value;
        }

        /**
         Get t.

         @return the t
         */
        public T get() {
            return value;
        }
    }

    /**
     The type Boolean flag.
     */
    public static class BooleanFlag extends ConfigFlag<Boolean> {
        /**
         Instantiates a new Boolean flag.

         @param key
         the key
         @param defVal
         the def val
         */
        public BooleanFlag(String key, Boolean defVal) {
            super(key, defVal);
        }
    }

    /**
     The type String flag.
     */
    public static class StringFlag extends ConfigFlag<String> {
        /**
         Instantiates a new String flag.

         @param key
         the key
         @param defVal
         the def val
         */
        public StringFlag(String key, String defVal) {
            super(key, defVal);
        }
    }

    /**
     The type List flag.

     @param <T>
     the type parameter
     */
    public static class ListFlag<T> extends ConfigFlag<List<T>> {
        /**
         Instantiates a new List flag.

         @param key
         the key
         @param defVal
         the def val
         */
        public ListFlag(String key, List<T> defVal) {
            super(key, defVal);
        }
    }
}
