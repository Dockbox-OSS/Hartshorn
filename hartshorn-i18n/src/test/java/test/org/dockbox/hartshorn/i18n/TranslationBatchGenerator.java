/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.org.dockbox.hartshorn.i18n;

import java.util.Objects;
import java.util.function.Predicate;
import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentRegistry;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.i18n.services.TranslationKeyGenerator;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Generator type which directly creates and outputs a translation batch based on
 * the currently registered InjectTranslation providers.
 */
public final class TranslationBatchGenerator {

    private static final List<String> BLACKLIST = List.of(
            // Test resources
            "class-resources.abstract.entry",
            "class-resources.concrete.entry",
            "resource.parameter.test.entry",
            "resource.test.entry",
            "test-resources.test.entry"
    );

    private static final List<String> HEADER = List.of("#",
            "#  Copyright 2019-2023 the original author or authors.",
            "#",
            "#  Licensed under the Apache License, Version 2.0 (the \"License\");",
            "#  you may not use this file except in compliance with the License.",
            "#  You may obtain a copy of the License at",
            "#",
            "#  https://www.apache.org/licenses/LICENSE-2.0",
            "#",
            "#  Unless required by applicable law or agreed to in writing, software",
            "#  distributed under the License is distributed on an \"AS IS\" BASIS,",
            "#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.",
            "#  See the License for the specific language governing permissions and",
            "#  limitations under the License.",
            "#", "");
    private static final DateTimeFormatter SDF = DateTimeFormatter.ofPattern("ddMMyyyy");

    private TranslationBatchGenerator() {}

    public static void main(String[] args) throws Exception {
        ApplicationContext context = HartshornApplication.create(TranslationBatchGenerator.class);
        Map<String, String> batches = migrateBatches(context);
        String date = SDF.format(LocalDateTime.now());
        Path outputPath = existingBatch().toPath().resolve("batches/" + date);
        outputPath.toFile().mkdirs();
        outputPath.toFile().mkdir();

        for (Entry<String, String> entry : batches.entrySet()) {
            String file = entry.getKey();
            String content = entry.getValue();
            Path out = outputPath.resolve(file);
            out.toFile().createNewFile();

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(out.toFile()), StandardCharsets.UTF_8);
            writer.write(content);
            writer.close();
        }
    }

    // File content identified by file name
    private static Map<String, String> migrateBatches(ApplicationContext context) throws IOException {
        String batch = TranslationBatchGenerator.createBatch(context);
        Properties properties = new Properties();
        properties.load(new StringReader(batch));

        Map<String, String> files = new HashMap<>();

        for (File file : TranslationBatchGenerator.existingFiles()) {
            List<String> strings = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            Properties cache = new Properties();
            cache.load(new StringReader(batch));

            for (String string : strings) {
                String[] property = string.split("=");
                String key = property[0];
                if (key.startsWith("$")) {
                    continue;
                }
                String value = String.join("=", Arrays.copyOfRange(property, 1, property.length));
                if (properties.containsKey(key)) {
                    // Override any existing, drop retired translations
                    cache.setProperty(key, value);
                }
            }

            List<String> content = new ArrayList<>();
            cache.forEach((key, value) -> {
                String next = String.valueOf(key) + '=' + value;
                content.add(next);
            });

            Collections.sort(content);
            Collection<String> output = CollectionUtilities.merge(HEADER, content);

            String fileOut = String.join("\n", output);
            files.put(file.getName(), fileOut);
        }
        return files;
    }

    private static File existingBatch() {
        // About as hacky as it gets. Please PR a better version
        return new File(TranslationBatchGenerator.class
                .getClassLoader().getResource("")
                .getFile())
                .toPath()
                .resolve("../../../../src/main/resources/hartshorn")
                .toFile();
    }

    private static String createBatch(ApplicationContext context) {
        Map<String, String> collect = collect(context);
        List<String> entries = new ArrayList<>();
        for (Entry<String, String> entry : collect.entrySet()) {
            if (entry.getValue().contains("\n")) {
                continue;
            }
            if (BLACKLIST.contains(entry.getKey())) {
                continue;
            }
            String next = entry.getKey() + '=' + entry.getValue();
            next = next.replaceAll("\r", "");
            entries.add(next);
        }
        Collections.sort(entries);
        return String.join("\n", entries);
    }

    private static List<File> existingFiles() {
        File batch = TranslationBatchGenerator.existingBatch();
        if (batch.exists() && batch.isDirectory()) {
            return Stream.of(Objects.requireNonNull(batch.listFiles()))
                    .filter(Predicate.not(File::isDirectory))
                    .toList();
        }
        else {
            throw new IllegalStateException("Existing batch could not be found");
        }
    }

    public static Map<String, String> collect(ApplicationContext context) {
        Map<String, String> batch = new HashMap<>();
        TranslationKeyGenerator keyGenerator = context.get(TranslationKeyGenerator.class);
        for (ComponentContainer<?> container : context.get(ComponentRegistry.class).containers()) {
            TypeView<?> type = container.type();
            List<? extends MethodView<?, ?>> methods = type.methods().annotatedWith(InjectTranslation.class);
            for (MethodView<?, ?> method : methods) {
                InjectTranslation annotation = method.annotations().get(InjectTranslation.class).get();
                String key = keyGenerator.key(type, method);
                batch.put(key, annotation.defaultValue());
            }
        }
        return batch;
    }
}
