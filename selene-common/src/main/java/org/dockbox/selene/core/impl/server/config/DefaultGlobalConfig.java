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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.server.IntegratedExtension;
import org.dockbox.selene.core.server.ServerReference;
import org.dockbox.selene.core.server.config.Environment;
import org.dockbox.selene.core.server.config.ExceptionLevels;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.server.properties.InjectableType;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.dockbox.selene.core.util.extension.Extension;
import org.dockbox.selene.core.util.files.ConfigurateManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Singleton
@ConfigSerializable
public class DefaultGlobalConfig extends ServerReference implements GlobalConfig, InjectableType {

    @Inject
    private transient ConfigurateManager configurateManager;
    @Inject
    private transient IntegratedExtension integratedExtension;

    @Setting(
            value = "default-language",
            comment = "The default language for all players and the console."
    )
    private Language defaultLanguage = Language.EN_US;

    @Setting(
            value = "allow-stacktraces",
            comment = "Whether or not stacktraces should print if a exception is thrown."
    )
    private boolean stacktracesAllowed = true;

    @Setting(
            value = "friendly-exceptions",
            comment = "Indicates whether or not to provide a clear user-friendly view of exceptions."
    )
    private boolean friendlyExceptions = true;

    @Setting(
            value = "environment-level",
            comment = "Indicates the environment Selene is running in. The development environment usually allows for additional debugging options."
    )
    private Environment environment = Environment.DEVELOPMENT;

    @NotNull
    @Override
    public Language getDefaultLanguage() {
        return this.defaultLanguage;
    }

    @Override
    public boolean getStacktracesAllowed() {
        return this.stacktracesAllowed;
    }

    @NotNull
    @Override
    public ExceptionLevels getExceptionLevel() {
        return this.friendlyExceptions ? ExceptionLevels.FRIENDLY : ExceptionLevels.MINIMAL;
    }

    private boolean isConstructed;

    private void copyValues(GlobalConfig config) {
        if (null != config) {
            this.defaultLanguage = config.getDefaultLanguage();
            this.friendlyExceptions = ExceptionLevels.FRIENDLY == config.getExceptionLevel();
            this.stacktracesAllowed = config.getStacktracesAllowed();
        }
        this.isConstructed = true;
    }

    @Override
    public boolean canEnable() {
        return !this.isConstructed;
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Extension extension = super.getExtension(this.integratedExtension.getClass());
        if (null == extension) {
            throw new IllegalStateException("Integrated extension not annotated as such.");
        }

        Path configPath = this.configurateManager.getConfigFile(extension);
        GlobalConfig globalConfig = this.configurateManager
                .getFileContent(configPath, DefaultGlobalConfig.class)
                .orElse(null);
        this.copyValues(globalConfig);
    }

    @NotNull
    @Override
    public Environment getEnvironment() {
        return this.environment;
    }
}
