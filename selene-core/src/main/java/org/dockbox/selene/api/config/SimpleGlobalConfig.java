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

package org.dockbox.selene.api.config;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.entity.annotations.Accessor;
import org.dockbox.selene.api.entity.annotations.Extract;
import org.dockbox.selene.api.entity.annotations.Extract.Behavior;
import org.dockbox.selene.api.entity.annotations.Metadata;
import org.dockbox.selene.api.exceptions.ExceptionLevels;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.persistence.AbstractConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

@SuppressWarnings("FieldMayBeFinal")
@Singleton
@Extract(Behavior.KEEP)
@Metadata(alias = "config")
public class SimpleGlobalConfig extends AbstractConfiguration<SimpleGlobalConfig> implements GlobalConfig {

    @Accessor(getter = "getEnvironment")
    private final Environment environment = Environment.DEVELOPMENT;

    @Accessor(getter = "getDefaultLanguage")
    private Language defaultLanguage = Language.EN_US;

    @Accessor(getter = "getStacktracesAllowed")
    private boolean stacktracesAllowed = true;

    @SuppressWarnings("FieldCanBeLocal")
    private boolean friendlyExceptions = true;

    @Accessor(getter = "getDiscordLoggingCategoryId")
    private String discordLoggingCategoryId = "0";

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
        if (null == Selene.getServer()) return ExceptionLevels.NATIVE;
        return this.friendlyExceptions ? ExceptionLevels.FRIENDLY : ExceptionLevels.MINIMAL;
    }

    @NotNull
    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public String getDiscordLoggingCategoryId() {
        return this.discordLoggingCategoryId;
    }

    @Override
    protected Class<?> getModuleClass() {
        return Selene.class;
    }
}
