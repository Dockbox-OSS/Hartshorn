/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.application.StandardApplicationBuilder;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.i18n.services.TranslationInjectPostProcessor;
import org.dockbox.hartshorn.testsuite.HartshornLifecycleExtension;
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
            "#  Copyright 2019-2022 the original author or authors.",
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

    public static void main(final String[] args) throws Exception {
        final ApplicationContext context = HartshornLifecycleExtension
                .createTestContext(new StandardApplicationBuilder().loadDefaults(), TranslationBatchGenerator.class)
                .orNull();
        final Map<String, String> batches = migrateBatches(context);
        final String date = SDF.format(LocalDateTime.now());
        final Path outputPath = existingBatch().toPath().resolve("batches/" + date);
        outputPath.toFile().mkdirs();
        outputPath.toFile().mkdir();

        for (final Entry<String, String> entry : batches.entrySet()) {
            final String file = entry.getKey();
            final String content = entry.getValue();
            final Path out = outputPath.resolve(file);
            out.toFile().createNewFile();

            final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(out.toFile()), StandardCharsets.UTF_8);
            writer.write(content);
            writer.close();
        }
    }

    // File content identified by file name
    private static Map<String, String> migrateBatches(final ApplicationContext context) throws IOException {
        final String batch = TranslationBatchGenerator.createBatch(context);
        final Properties properties = new Properties();
        properties.load(new StringReader(batch));

        final Map<String, String> files = new HashMap<>();

        for (final File file : TranslationBatchGenerator.existingFiles()) {
            final List<String> strings = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            final Properties cache = new Properties();
            cache.load(new StringReader(batch));

            for (final String string : strings) {
                final String[] property = string.split("=");
                final String key = property[0];
                if (key.startsWith("$")) continue;
                final String value = String.join("=", Arrays.copyOfRange(property, 1, property.length));
                if (properties.containsKey(key)) {
                    // Override any existing, drop retired translations
                    cache.setProperty(key, value);
                }
            }

            final List<String> content = new ArrayList<>();
            cache.forEach((key, value) -> {
                final String next = String.valueOf(key) + '=' + value;
                content.add(next);
            });

            Collections.sort(content);
            final Collection<String> output = CollectionUtilities.merge(HEADER, content);

            final String fileOut = String.join("\n", output);
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

    private static String createBatch(final ApplicationContext context) {
        final Map<String, String> collect = collect(context);
        final List<String> entries = new ArrayList<>();
        for (final Entry<String, String> entry : collect.entrySet()) {
            if (entry.getValue().contains("\n")) continue;
            if (BLACKLIST.contains(entry.getKey())) continue;
            String next = entry.getKey() + '=' + entry.getValue();
            next = next.replaceAll("\r", "");
            entries.add(next);
        }
        Collections.sort(entries);
        return String.join("\n", entries);
    }

    private static List<File> existingFiles() {
        final File batch = TranslationBatchGenerator.existingBatch();
        if (batch.exists() && batch.isDirectory()) {
            return Stream.of(batch.listFiles())
                    .filter(f -> !f.isDirectory())
                    .toList();
        }
        else throw new IllegalStateException("Existing batch could not be found");
    }

    public static Map<String, String> collect(final ApplicationContext context) {
        final Map<String, String> batch = new HashMap<>();
        for (final ComponentContainer container : context.get(ComponentLocator.class).containers()) {
            final TypeView<?> type = container.type();
            final List<? extends MethodView<?, ?>> methods = type.methods().annotatedWith(InjectTranslation.class);
            for (final MethodView<?, ?> method : methods) {
                final InjectTranslation annotation = method.annotations().get(InjectTranslation.class).get();
                final String key = KeyGen.INSTANCE.key(context, type, method);
                batch.put(key, annotation.value());
            }
        }
        return batch;
    }

    private static class KeyGen extends TranslationInjectPostProcessor {

        private static final KeyGen INSTANCE = new KeyGen();

        @Override
        public String key(final ApplicationContext context, final TypeView<?> type, final MethodView<?, ?> method) {
            return super.key(context, type, method);
        }
    }

}
