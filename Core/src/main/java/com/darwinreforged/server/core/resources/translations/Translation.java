package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.files.FileManager;
import com.darwinreforged.server.core.resources.ConfigSetting;
import com.darwinreforged.server.core.util.StringUtils;
import com.darwinreforged.server.modules.internal.darwin.DarwinServerModule;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.darwinreforged.server.core.resources.translations.DefaultTranslations.COLOR_ERROR;
import static com.darwinreforged.server.core.resources.translations.DefaultTranslations.COLOR_MINOR;
import static com.darwinreforged.server.core.resources.translations.DefaultTranslations.COLOR_PRIMARY;
import static com.darwinreforged.server.core.resources.translations.DefaultTranslations.COLOR_SECONDARY;

@SuppressWarnings("InstantiationOfUtilityClass")
public class Translation {

    private String value;
    private String key;

    public void setValue(String value) {
        this.value = value;
    }

    public Translation() {
    }

    Translation(String value, String key) {
        this.value = value;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    // String
    public String s() {
        return parseColors(this.value);
    }

    @JsonIgnore
    public static final Map<String, List<Translation>> TRANSLATION_STORAGE = new HashMap<>();

    // As these are utility classes filled with nothing but `public static final` Translation objects,
    // the would not be loaded until used, causing the translations not to be present yet. To 'resolve'
    // this we instantiate them here once, so the translation file can be filled.
    static {
        new BrushTranslations();
        new DaveTranslations();
        new DefaultTranslations();
    }

    // Unparsed
    @JsonIgnore
    public String u() {
        return value.replaceAll("[$|&][0-9a-fklmnor]", "");
    }

    // Formatted
    @JsonIgnore
    public String f(final Object... args) {
        return format(this.value, args);
    }

    // Formatted, shortened
    @JsonIgnore
    public String fsh(final Object... args) {
        int diff = s().length() - u().length();
        String formatted = f(args);
        return (49 + diff > formatted.length() ? formatted : f(args).substring(0, (50 + diff))) + "...";
    }

    @JsonIgnore
    public static String shorten(String s) {
        return s.substring(0, 50) + "...";
    }

    // Format value placeholders and colors
    @JsonIgnore
    public static String format(String m, Object... args) {
        if (args.length == 0) return m;
        Map<String, String> map = new LinkedHashMap<>();
        if (args.length > 0) {
            for (int i = args.length - 1; i >= 0; i--) {
                String arg = "" + args[i];
                if (arg == null || arg.isEmpty()) map.put(String.format("{%d}", i), "empty");
                else map.put(String.format("{%d}", i), arg);
            }
        }
        m = StringUtils.replaceFromMap(m, map);
        return parseColors(m);
    }

    @JsonIgnore
    public static String parseColors(String m) {
        char[] nativeFormats = "abcdef1234567890klmnor".toCharArray();
        for (char c : nativeFormats) m = m.replaceAll(String.format("&%s", c), String.format("\u00A7%s", c));

        return m
                .replaceAll("\\$1", String.format("\u00A7%s", COLOR_PRIMARY.value))
                .replaceAll("\\$2", String.format("\u00A7%s", COLOR_SECONDARY.value))
                .replaceAll("\\$3", String.format("\u00A7%s", COLOR_MINOR.value))
                .replaceAll("\\$4", String.format("\u00A7%s", COLOR_ERROR.value));
    }

    // Change
    @JsonIgnore
    private void c(String s) {
        this.value = s;
    }

    @JsonIgnore
    public static Translation create(String translation) {
        try {
            StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            // 0: Translations, 1: Translations, 2: Caller
            String caller = ste[2].getClassName();
            Class<?> callerClass = Class.forName(caller);
            String category = "other";
            if (callerClass.isAnnotationPresent(ConfigSetting.class)) {
                ConfigSetting cs = callerClass.getAnnotation(ConfigSetting.class);
                category = cs.value();
            }

            AtomicReference<String> key = new AtomicReference<>();
            Arrays.stream(callerClass.getDeclaredFields()).forEach(f -> {
                try {
                    Object fv = f.get(null);
                    // TODO : Obtain this value before it is completely assigned?
                    if (fv instanceof Translation) {
                        if (((Translation) fv).value.equals(translation))
                            key.set(f.getName().toLowerCase().replaceAll("_", "."));
                    } else System.out.println("Not instance of translation, instead : ");
                } catch (Throwable e) {
                    DarwinServer.error("Could not convert translation field", e);
                }
            });

            if (key.get() != null) {
                DarwinServer.getLog().info(String.format("Registered '%s.%s' for translation '%s'", category, key.get(), translation));
                Translation t = new Translation(translation, key.get());

                Translation.TRANSLATION_STORAGE.putIfAbsent(category, new ArrayList<>());
                List<Translation> categoryTranslations = Translation.TRANSLATION_STORAGE.get(category);
                categoryTranslations.add(t);
                Translation.TRANSLATION_STORAGE.put(category, categoryTranslations);
                return t;
            } else System.out.println("Key null");
        } catch (Throwable e) {
            DarwinServer.error("Failed to register translation", e);
        }
        DarwinServer.getLog().warn("Returning non-stored translation");
        return new Translation(translation, "empty");
    }


    @JsonIgnore
    public static void writeToFile() {
        FileManager fm = DarwinServer.get(FileManager.class);
        Path dataDir = fm.getDataDirectory(DarwinServerModule.class);
        File translationFile = new File(dataDir.toFile(), "translation-dump.yml");
        fm.writeYamlDataToFile(Translation.TRANSLATION_STORAGE, translationFile);
    }

}
