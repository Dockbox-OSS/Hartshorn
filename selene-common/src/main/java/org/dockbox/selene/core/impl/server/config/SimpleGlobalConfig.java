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

package org.dockbox.selene.core.impl.server.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.files.FileManager;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.server.IntegratedExtension;
import org.dockbox.selene.core.server.config.Environment;
import org.dockbox.selene.core.server.config.ExceptionLevels;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.server.properties.InjectableType;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.nio.file.Path;

@Singleton
@ConfigSerializable
public class SimpleGlobalConfig implements GlobalConfig, InjectableType {

    @Inject
    private transient FileManager fileManager;
    @Inject
    private transient IntegratedExtension integratedExtension;

    @Setting("default-language")
    @Comment("The default language for all players and the console.")
    private Language defaultLanguage = Language.EN_US;

    @Setting("allow-stacktraces")
    @Comment("Whether or not stacktraces should print if a exception is thrown.")
    private boolean stacktracesAllowed = true;

    @Setting("friendly-exceptions")
    @Comment("Indicates whether or not to provide a clear user-friendly view of exceptions.")
    private boolean friendlyExceptions = true;

    @Setting("environment-level")
    @Comment("Indicates the environment Selene is running in. The development environment usually allows for additional debugging options.")
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
        Extension extension = SeleneUtils.REFLECTION.getExtension(this.integratedExtension.getClass());
        if (null == extension) {
            throw new IllegalStateException("Integrated extension not annotated as such.");
        }

        Path configPath = this.fileManager.getConfigFile(extension);
        GlobalConfig globalConfig = this.fileManager
                .getFileContent(configPath, SimpleGlobalConfig.class)
                .orNull();
        this.copyValues(globalConfig);
    }

    @NotNull
    @Override
    public Environment getEnvironment() {
        return this.environment;
    }
}
