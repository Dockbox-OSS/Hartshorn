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

package org.dockbox.hartshorn.api.i18n;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.i18n.annotations.Resource;
import org.dockbox.hartshorn.di.services.ServiceContainer;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Generator type which directly creates and outputs a translation batch based on
 * the currently registered Resource providers.
 */
public class TranslationBatchGenerator {

    private static final List<String> BLACKLIST = HartshornUtils.asList(
            // Test resources
            "class-resources.abstract.entry",
            "class-resources.concrete.entry",
            "resource.parameter.test.entry",
            "resource.test.entry",
            "test-resources.test.entry",
            "server.confirm.true",
            "server.confirm.false",

            // Formatting only resources
            "dave.discord.format",
            "dave.trigger.single",
            "hartshorn.exception",
            "hartshorn.info.service.row",
            "oldplots.list.single",
            "prefix"
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
    private static final SimpleDateFormat SDF = new SimpleDateFormat("ddMMyyyy");

    public static void main(String[] args) throws Exception {
        new HartshornRunner().beforeAll(null);
        final Map<String, String> batches = migrateBatches();
        String date = SDF.format(new Date());
        final Path outputPath = getExistingBatch().toPath().resolve("batches/" + date);
        outputPath.toFile().mkdirs();
        outputPath.toFile().mkdir();

        for (Entry<String, String> entry : batches.entrySet()) {
            String file = entry.getKey();
            String content = entry.getValue();
            final Path out = outputPath.resolve(file);
            out.toFile().createNewFile();

            FileWriter writer = new FileWriter(out.toFile());
            writer.write(content);
            writer.close();
        }
    }

    public static Map<String, String> collect() {
        Map<String, String> batch = HartshornUtils.emptyMap();
        int i = 0;
        for (ServiceContainer container : Hartshorn.context().locator().containers()) {
            final Class<?> type = container.getType();
            final Collection<Method> methods = Reflect.annotatedMethods(type, Resource.class);
            for (Method method : methods) {
                i++;
                final Resource annotation = method.getAnnotation(Resource.class);
                final String key = I18N.key(type, method);
                batch.put(key, annotation.value());
            }
        }
        return batch;
    }

    private static String createBatch() {
        final Map<String, String> collect = collect();
        List<String> entries = HartshornUtils.emptyList();
        for (Entry<String, String> entry : collect.entrySet()) {
            if (entry.getValue().contains("\n")) continue;
            if (BLACKLIST.contains(entry.getKey())) continue;
            String next = entry.getKey() + '=' + entry.getValue();
            next = next.replaceAll("\r", "");
            entries.add(next);
        }
        Collections.sort(entries);
        return String.join("\n", entries);
    }

    // File content identified by file name
    private static Map<String, String> migrateBatches() throws IOException {
        final String batch = TranslationBatchGenerator.createBatch();
        Properties properties = new Properties();
        properties.load(new StringReader(batch));

        Map<String, String> files = HartshornUtils.emptyMap();

        for (File file : TranslationBatchGenerator.getExistingFiles()) {
            final List<String> strings = Files.readAllLines(file.toPath());
            Properties cache = new Properties();
            cache.load(new StringReader(batch));

            for (String string : strings) {
                final String[] property = string.split("=");
                String key = property[0];
                if (key.startsWith("$")) continue;
                String value = String.join("=", HartshornUtils.getArraySubset(property, 1, property.length-1));
                if (properties.containsKey(key)) {
                    // Override any existing, drop retired translations
                    cache.setProperty(key, value);
                }
            }

            List<String> content = HartshornUtils.emptyList();
            cache.forEach((key, value) -> {
                String next = String.valueOf(key) + '=' + value;
                content.add(next);
            });

            Collections.sort(content);
            final List<String> output = HartshornUtils.merge(HEADER, content);

            String fileOut = String.join("\n", output);
            files.put(file.getName(), fileOut);
        }
        return files;
    }

    private static List<File> getExistingFiles() {
        final File batch = TranslationBatchGenerator.getExistingBatch();
        if (batch.exists() && batch.isDirectory()) {
            return HartshornUtils.asList(batch.listFiles()).stream()
                    .filter(f -> !f.isDirectory())
                    .collect(Collectors.toList());
        } else throw new IllegalStateException("Existing batch could not be found");
    }

    private static File getExistingBatch() {
        // About as hacky as it gets. Please PR a better version
        return new File(TranslationBatchGenerator.class
                .getClassLoader().getResource("")
                .getFile())
                .toPath()
                .resolve("../../../../src/main/resources/hartshorn")
                .toFile();
    }

}
