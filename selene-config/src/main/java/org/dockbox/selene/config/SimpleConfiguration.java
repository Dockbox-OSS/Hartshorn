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

package org.dockbox.selene.config;

import org.dockbox.selene.di.annotations.AutoWired;
import org.dockbox.selene.di.exceptions.ApplicationException;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.util.SeleneUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SimpleConfiguration implements Configuration, InjectableType {

    private final Path path;
    private Map<String, Object> cache;

    @AutoWired
    public SimpleConfiguration(Path path) {
        this.path = path;
    }

    @Override
    public <T> T get(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> next = new HashMap<>(this.cache);
        for (int i = 0; i < keys.length; i++) {
            String s = keys[i];
            Object value = next.getOrDefault(s, null);
            if (value == null) return null;
            else if (value instanceof Map) {
                //noinspection unchecked
                next = (Map<String, Object>) value;
                continue;
            }
            else if (i == keys.length -1) {
                //noinspection unchecked
                return (T) value;
            } else {
                throw new EndOfPropertyException(key, s);
            }
        }
        return null;
    }

    @Override
    public boolean canEnable() {
        return this.cache == null || this.cache.isEmpty();
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) throws ApplicationException {
        try {
            FileInputStream fileInputStream = new FileInputStream(this.path.toFile());
            Yaml yaml = new Yaml();
            this.cache = yaml.load(fileInputStream);
            if (this.cache == null) {
                this.cache = SeleneUtils.emptyMap();
            }
        }
        catch (FileNotFoundException e) {
            throw new ApplicationException(e);
        }
    }
}
