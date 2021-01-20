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

package org.dockbox.selene.core.impl.files;

import org.dockbox.selene.core.annotations.entity.Ignore;
import org.dockbox.selene.core.files.FileType;
import org.dockbox.selene.core.impl.files.util.XStreamUtils;
import org.dockbox.selene.core.impl.files.util.XStreamUtils.XStreamBuilder;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.util.Reflect;
import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.nio.file.Path;

public abstract class DefaultXStreamManager extends DefaultAbstractFileManager {

    protected DefaultXStreamManager() {
        super(FileType.XML);
    }

    @Override
    public <T> Exceptional<T> read(Path file, Class<T> type) {
        Reflect.rejects(type, DefaultXStreamManager.class, true);
        return Exceptional.of(() -> this.prepareXStream(type).read(type, file.toFile()));
    }

    @Override
    public <T> Exceptional<Boolean> write(Path file, T content) {
        @SuppressWarnings("unchecked") Class<T> type = (Class<T>) content.getClass();
        Reflect.rejects(type, DefaultXStreamManager.class, true);

        if (null != content) {
            try {
                this.prepareXStream(type).write(content, file.toFile());
                return Exceptional.of(true);
            } catch (IOException e) {
                return Exceptional.of(false, e);
            }
        }
        return Exceptional.of(false);
    }

    private XStreamBuilder prepareXStream(Class<?> type) {
        XStreamBuilder builder = XStreamUtils.create();
        this.omitIgnoredFields(type, builder);
        this.aliasPropertyFields(type, builder);
        return builder;
    }

    private void omitIgnoredFields(Class<?> type, XStreamBuilder builder) {
        Reflect.forEachFieldIn(type, (declaringType, field) -> {
            if (field.isAnnotationPresent(Ignore.class))
                builder.omitField(declaringType, field.getName());
        });
    }

    private void aliasPropertyFields(Class<?> type, XStreamBuilder builder) {
        Reflect.forEachFieldIn(type, (declaringType, field) -> {
            @NonNls String alias = Reflect.getFieldPropertyName(field);
            if (!field.getName().equals(alias)) builder.aliasField(alias, declaringType, field.getName());
        });
    }
}
