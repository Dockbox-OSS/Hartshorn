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

package org.dockbox.selene.common.server.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.dockbox.selene.api.annotations.entity.Accessor;
import org.dockbox.selene.api.annotations.entity.Extract;
import org.dockbox.selene.api.annotations.entity.Extract.Behavior;
import org.dockbox.selene.api.annotations.entity.Metadata;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.server.IntegratedModule;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.config.Environment;
import org.dockbox.selene.api.server.config.ExceptionLevels;
import org.dockbox.selene.api.server.config.GlobalConfig;
import org.dockbox.selene.api.server.properties.InjectableType;
import org.dockbox.selene.api.server.properties.InjectorProperty;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@Singleton
@Extract(Behavior.KEEP)
@Metadata(alias = "config")
public class SimpleGlobalConfig implements GlobalConfig, InjectableType
{

    @Inject
    @Extract(Behavior.SKIP)
    private transient FileManager fileManager;
    @Inject
    @Extract(Behavior.SKIP)
    private transient IntegratedModule integratedModule;

    @Accessor(getter = "getDefaultLanguage")
    private Language defaultLanguage = Language.EN_US;
    @Accessor(getter = "getStacktracesAllowed")
    private boolean stacktracesAllowed = true;
    private boolean friendlyExceptions = true;
    @Accessor(getter = "getEnvironment")
    private final Environment environment = Environment.DEVELOPMENT;
    @Accessor(getter = "getDiscordLoggingCategoryId")
    private String discordLoggingCategoryId = "0";

    @Extract(Behavior.SKIP)
    private transient boolean isConstructed;

    @NotNull
    @Override
    public Language getDefaultLanguage()
    {
        return this.defaultLanguage;
    }

    @Override
    public boolean getStacktracesAllowed()
    {
        return this.stacktracesAllowed;
    }

    @NotNull
    @Override
    public ExceptionLevels getExceptionLevel()
    {
        if (null == Selene.getServer()) return ExceptionLevels.NATIVE;
        return this.friendlyExceptions ? ExceptionLevels.FRIENDLY : ExceptionLevels.MINIMAL;
    }

    @NotNull
    @Override
    public Environment getEnvironment()
    {
        return this.environment;
    }

    @Override
    public String getDiscordLoggingCategoryId()
    {
        return this.discordLoggingCategoryId;
    }

    @Override
    public boolean canEnable()
    {
        return !this.isConstructed;
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties)
    {
        Module module = Reflect.getModule(this.integratedModule.getClass());
        if (null == module)
        {
            throw new IllegalStateException("Integrated module not annotated as such.");
        }

        Path configPath = this.fileManager.getConfigFile(module);
        SimpleGlobalConfig globalConfig = this.fileManager
                .read(configPath, SimpleGlobalConfig.class)
                .orNull();
        this.copyValues(globalConfig);
    }

    private void copyValues(SimpleGlobalConfig config)
    {
        SeleneUtils.shallowCopy(config, this);
        this.isConstructed = true;
    }
}
