/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl.server.config;

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.server.config.ConfigKeys;
import org.dockbox.selene.core.server.config.ExceptionLevels;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DefaultGlobalConfig implements GlobalConfig {

    private final Map<String, Object> configMap = new HashMap<>();

//    @Inject
//    public DefaultGlobalConfig(ConfigManager cm) {
//        this.configMap = cm.getConfigContents(Server.getServer());
//    }

    @NotNull
    @Override
    public Language getDefaultLanguage() {
        String cfg = this.getSetting(ConfigKeys.GLOBAL_CONFIG.getKey());
        return cfg == null ? Language.EN_US : Language.valueOf(cfg.toUpperCase());
    }

    @Override
    public boolean getStacktracesAllowed() {
        String cfg = this.getSetting(ConfigKeys.ALLOW_STACKTRACES.getKey());
//        return Boolean.parseBoolean(cfg);
        return true;
    }

    @NotNull
    @Override
    public ExceptionLevels getExceptionLevel() {
        String cfg = this.getSetting(ConfigKeys.EXCEPTION_LEVEL.getKey());
        try {
            return ExceptionLevels.valueOf(cfg.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return ExceptionLevels.FRIENDLY;
        }
    }

    @Override
    public String getSetting(@NotNull String key) {
        String[] steps = key.split("\\.");
        Object next = this.configMap.get(steps[0]);
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
