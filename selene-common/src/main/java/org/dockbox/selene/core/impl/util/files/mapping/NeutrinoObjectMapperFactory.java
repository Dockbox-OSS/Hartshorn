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

/*
 * This file is part of Neutrino, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package org.dockbox.selene.core.impl.util.files.mapping;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.dockbox.selene.core.impl.util.files.process.SettingProcessor;
import org.dockbox.selene.core.impl.util.files.util.ClassConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ninja.leaping.configurate.objectmapping.ObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;

public final class NeutrinoObjectMapperFactory implements ObjectMapperFactory {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builds an {@link NeutrinoObjectMapperFactory}
     */
    public static class Builder {

        @Nullable private Function<Setting, String> commentProcessor;
        private ClassConstructor<SettingProcessor> settingProcessorClassConstructor = Class::newInstance;

        public Builder setCommentProcessor(@Nullable Function<Setting, String> commentProcessor) {
            this.commentProcessor = commentProcessor;
            return this;
        }

        public Builder setSettingProcessorClassConstructor(ClassConstructor<SettingProcessor> settingProcessorClassConstructor) {
            this.settingProcessorClassConstructor = Preconditions.checkNotNull(settingProcessorClassConstructor);
            return this;
        }

        public NeutrinoObjectMapperFactory build(boolean setAsDefault) {
            if (null == this.commentProcessor) {
                this.commentProcessor = Setting::comment;
            }

            return new NeutrinoObjectMapperFactory(setAsDefault, this.commentProcessor, this.settingProcessorClassConstructor);
        }

    }
    private static NeutrinoObjectMapperFactory INSTANCE;

    public static ObjectMapperFactory getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new NeutrinoObjectMapperFactory(false, Setting::comment, Class::newInstance);
        }

        return INSTANCE;
    }

    // --

    private final ClassConstructor<SettingProcessor> processorClassConstructor;
    private final Function<Setting, String> commentProcessor;
    private final LoadingCache<Class<?>, NeutrinoObjectMapper<?>> mapperCache = CacheBuilder.newBuilder().weakKeys()
            .maximumSize(500).build(new CacheLoader<Class<?>, NeutrinoObjectMapper<?>>() {
                @Override
                public NeutrinoObjectMapper<?> load(@Nonnull Class<?> key) throws Exception {
                    return new NeutrinoObjectMapper<>(key, NeutrinoObjectMapperFactory.this.commentProcessor, NeutrinoObjectMapperFactory.this.processorClassConstructor);
                }
            });

    private NeutrinoObjectMapperFactory(boolean setInstance, Function<Setting, String> commentProcessor, ClassConstructor<SettingProcessor> processorClassConstructor) {
        this.commentProcessor = commentProcessor;
        this.processorClassConstructor = processorClassConstructor;
        if (null == INSTANCE || setInstance) {
            INSTANCE = this;
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> NeutrinoObjectMapper<T> getMapper(@NotNull Class<T> type) throws ObjectMappingException {
        Preconditions.checkNotNull(type, "type");
        try {
            return (NeutrinoObjectMapper<T>) this.mapperCache.get(type);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ObjectMappingException) {
                //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
                throw (ObjectMappingException) e.getCause();
            } else {
                throw new ObjectMappingException(e);
            }
        }
    }

}
