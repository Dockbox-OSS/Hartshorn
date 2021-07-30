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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class SimpleConfigurationManager implements ConfigurationManager {

    private final Path path;
    private final String fileKey;
    @Getter
    private final Map<String, Map<String, Object>> cache = HartshornUtils.emptyConcurrentMap();

    @Bound
    public SimpleConfigurationManager(Path path) {
        this.path = path;
        this.fileKey = this.fileKey();

        try {
            if (!this.cache.containsKey(this.fileKey)) {
                FileInputStream fileInputStream = new FileInputStream(this.path.toFile());
                Yaml yaml = new Yaml();
                Map<String, Object> localCache = yaml.load(fileInputStream);
                if (localCache == null) {
                    localCache = HartshornUtils.emptyMap();
                }
                this.cache.put(this.fileKey, localCache);
            }
        }
        catch (FileNotFoundException e) {
            throw new ApplicationException(e).runtime();
        }
    }

    protected String fileKey() {
        String fileName = this.path.getFileName().toString();
        String root = this.path.getParent().getFileName().toString();
        return root + ':' + fileName + '/';
    }

    @Override
    public <T> Exceptional<T> get(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> next = new HashMap<>(this.cache().get(this.fileKey));
        for (int i = 0; i < keys.length; i++) {
            String s = keys[i];
            Object value = next.getOrDefault(s, null);
            if (value == null) return Exceptional.empty();
            else if (value instanceof Map) {
                //noinspection unchecked
                next = (Map<String, Object>) value;
                continue;
            }
            else if (i == keys.length - 1) {
                //noinspection unchecked
                return Exceptional.of((T) value);
            }
            else {
                return Exceptional.of(new EndOfPropertyException(key, s));
            }
        }
        return Exceptional.empty();
    }
}
