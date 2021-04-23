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

package org.dockbox.selene.api;

import org.dockbox.selene.api.config.Environment;
import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.api.exceptions.ExceptionLevels;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesGlobalConfig implements GlobalConfig {

    private static final String STACKTRACES = "stacktraces";
    private static final String LEVEL = "level";
    private static final String ENVIRONMENT = "environment";
    private static final String LOGGING_CHANNEL = "logging-channel";

    private final Properties properties;

    public PropertiesGlobalConfig() {
        this.properties = new Properties();
        this.setDefaultProperties();
        try {
            InputStream propertiesResource = this.getClass().getClassLoader().getResourceAsStream("selene.properties");
            this.properties.load(propertiesResource);
        } catch (Throwable e) {
            Except.handle(e);
        }
    }

    @Override
    public boolean getStacktracesAllowed() {
        return Boolean.parseBoolean(this.properties.getProperty(STACKTRACES));
    }

    @Override
    public ExceptionLevels getExceptionLevel() {
        return ExceptionLevels.valueOf(this.properties.getProperty(LEVEL).toUpperCase());
    }

    @Override
    public Environment getEnvironment() {
        return Environment.valueOf(this.properties.getProperty(ENVIRONMENT).toUpperCase());
    }

    @Override
    public String getDiscordLoggingCategoryId() {
        return this.properties.getProperty(LOGGING_CHANNEL);
    }

    private void setDefaultProperties() {
        this.properties.setProperty(STACKTRACES, "true");
        this.properties.setProperty(LEVEL, "friendly");
        this.properties.setProperty(ENVIRONMENT, "development");
        this.properties.setProperty(LOGGING_CHANNEL, "-1");
    }
}
