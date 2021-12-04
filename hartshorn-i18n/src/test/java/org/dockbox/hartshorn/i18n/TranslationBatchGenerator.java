/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.i18n.services.TranslationInjectPostProcessor;
import org.dockbox.hartshorn.testsuite.HartshornExtension;

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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Generator type which directly creates and outputs a translation batch based on
 * the currently registered InjectTranslation providers.
 */
public final class TranslationBatchGenerator {

    private static final List<String> BLACKLIST = HartshornUtils.asList(
            // Test resources
            "class-resources.abstract.entry",
            "class-resources.concrete.entry",
            "resource.parameter.test.entry",
            "resource.test.entry",
            "test-resources.test.entry"
    );
    private static final List<String> HEADER = HartshornUtils.asList("#",
            "# Copyright (C) 2020 Guus Lieben",
            "#",
            "# This framework is free software; you can redistribute it and/or modify",
            "# it under the terms of the GNU Lesser General Public License as",
            "# published by the Free Software Foundation, either version 2.1 of the",
            "# License, or (at your option) any later version.",
            "#",
            "# This library is distributed in the hope that it will be useful,",
            "# but WITHOUT ANY WARRANTY; without even the implied warranty of",
            "# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See",
            "# the GNU Lesser General Public License for more details.",
            "#",
            "# You should have received a copy of the GNU Lesser General Public License",
            "# along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.",
            "#", "");
    private static final DateTimeFormatter SDF = DateTimeFormatter.ofPattern("ddMMyyyy");

    private TranslationBatchGenerator() {}

    public static void main(final String[] args) throws Exception {
        final ApplicationContext context = HartshornExtension.createContext(TranslationBatchGenerator.class).orNull();
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

        final Map<String, String> files = HartshornUtils.emptyMap();

        for (final File file : TranslationBatchGenerator.existingFiles()) {
            final List<String> strings = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            final Properties cache = new Properties();
            cache.load(new StringReader(batch));

            for (final String string : strings) {
                final String[] property = string.split("=");
                final String key = property[0];
                if (key.startsWith("$")) continue;
                final String value = String.join("=", HartshornUtils.arraySubset(property, 1, property.length - 1));
                if (properties.containsKey(key)) {
                    // Override any existing, drop retired translations
                    cache.setProperty(key, value);
                }
            }

            final List<String> content = HartshornUtils.emptyList();
            cache.forEach((key, value) -> {
                final String next = String.valueOf(key) + '=' + value;
                content.add(next);
            });

            Collections.sort(content);
            final Collection<String> output = HartshornUtils.merge(HEADER, content);

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
        final List<String> entries = HartshornUtils.emptyList();
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
            return HartshornUtils.asList(batch.listFiles()).stream()
                    .filter(f -> !f.isDirectory())
                    .toList();
        }
        else throw new IllegalStateException("Existing batch could not be found");
    }

    public static Map<String, String> collect(final ApplicationContext context) {
        final Map<String, String> batch = HartshornUtils.emptyMap();
        for (final ComponentContainer container : context.locator().containers()) {
            final TypeContext<?> type = container.type();
            final List<? extends MethodContext<?, ?>> methods = type.methods(InjectTranslation.class);
            for (final MethodContext<?, ?> method : methods) {
                final InjectTranslation annotation = method.annotation(InjectTranslation.class).get();
                final String key = KeyGen.INSTANCE.key(context, type, method);
                batch.put(key, annotation.value());
            }
        }
        return batch;
    }

    private static class KeyGen extends TranslationInjectPostProcessor {

        private static final KeyGen INSTANCE = new KeyGen();

        @Override
        public String key(final ApplicationContext context, final TypeContext<?> type, final MethodContext<?, ?> method) {
            return super.key(context, type, method);
        }
    }

}
