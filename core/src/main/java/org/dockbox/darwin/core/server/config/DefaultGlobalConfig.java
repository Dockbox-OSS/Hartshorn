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

package org.dockbox.darwin.core.server.config;

import com.google.inject.Inject;

import org.dockbox.darwin.core.i18n.Languages;
import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.server.config.GlobalConfig;
import org.dockbox.darwin.core.util.files.ConfigManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DefaultGlobalConfig implements GlobalConfig {

    private final Map<String, Object> configMap;

    @Inject
    public DefaultGlobalConfig(ConfigManager cm) {
        configMap = cm.getConfigContents(Server.getServer());
    }

    @NotNull
    @Override
    public Languages getDefaultLanguage() {
        String cfg = getSetting(ConfigKeys.GLOBAL_CONFIG.getKey());
        return cfg == null ? Languages.EN_US : Languages.valueOf(cfg.toUpperCase());
    }

    @Override
    public boolean getStacktracesAllowed() {
        String cfg = getSetting(ConfigKeys.ALLOW_STACKTRACES.getKey());
        return Boolean.parseBoolean(cfg);
    }

    @NotNull
    @Override
    public ExceptionLevels getExceptionLevel() {
        String cfg = getSetting(ConfigKeys.EXCEPTION_LEVEL.getKey());
        try {
            return ExceptionLevels.valueOf(cfg.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return ExceptionLevels.MINIMAL;
        }
    }

    @Override
    public String getSetting(@NotNull String key) {
        String[] steps = key.split("\\.");
        Object next = configMap.get(steps[0]);
        for (int i = 0; i < steps.length; i++) {
            String step = steps[i];
            if (next instanceof Map && i < steps.length-1) {
                next = ((Map<?, ?>) next).get(step);
            } else {
                if (next == null) return null;
                else return String.valueOf(next);
            }
        }
        return null;
    }
}
