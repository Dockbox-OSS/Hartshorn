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

package org.dockbox.selene.common.files;

import org.dockbox.selene.api.annotations.entity.Ignore;
import org.dockbox.selene.api.files.FileType;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.persistence.PersistentCapable;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.common.files.util.XStreamUtils;
import org.dockbox.selene.common.files.util.XStreamUtils.XStreamBuilder;
import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.nio.file.Path;

public abstract class DefaultXStreamManager extends DefaultAbstractFileManager {

    protected DefaultXStreamManager() {
        super(FileType.XML);
    }

    @Override
    public <T> Exceptional<T> read(Path file, Class<T> type) {
        Exceptional<T> persistentCapable = correctPersistentCapable(file, type);
        if (persistentCapable.isPresent()) return persistentCapable;

        Reflect.rejects(type, DefaultXStreamManager.class, true);
        return Exceptional.of(
                () -> DefaultXStreamManager.prepareXStream(type).read(type, file.toFile()));
    }

    @Override
    public <T> Exceptional<Boolean> write(Path file, T content) {
        if (content instanceof PersistentCapable) return write(file, ((PersistentCapable<?>) content).toPersistentModel());

        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) content.getClass();
        Reflect.rejects(type, DefaultXStreamManager.class, true);

        if (null != content) {
            try {
                DefaultXStreamManager.prepareXStream(type).write(content, file.toFile());
                return Exceptional.of(true);
            }
            catch (IOException e) {
                return Exceptional.of(false, e);
            }
        }
        return Exceptional.of(false);
    }

    @Override
    public void requestFileType(FileType fileType) {
        if (FileType.XML != fileType) {
            throw new UnsupportedOperationException("XStream only supports XML");
        }
    }

    private static XStreamBuilder prepareXStream(Class<?> type) {
        XStreamBuilder builder = XStreamUtils.create();
        DefaultXStreamManager.omitIgnoredFields(type, builder);
        DefaultXStreamManager.aliasPropertyFields(type, builder);
        return builder;
    }

    private static void omitIgnoredFields(Class<?> type, XStreamBuilder builder) {
        Reflect.forEachFieldIn(
                type,
                (declaringType, field) -> {
                    if (field.isAnnotationPresent(Ignore.class))
                        builder.omitField(declaringType, field.getName());
                });
    }

    private static void aliasPropertyFields(Class<?> type, XStreamBuilder builder) {
        Reflect.forEachFieldIn(
                type,
                (declaringType, field) -> {
                    @NonNls String alias = Reflect.getFieldPropertyName(field);
                    if (!field.getName().equals(alias))
                        builder.aliasField(alias, declaringType, field.getName());
                });
    }
}
