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

package org.dockbox.selene.core.impl;

import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.util.extension.ExtensionContext;
import org.dockbox.selene.core.util.extension.status.ExtensionStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SimpleExtensionContext implements ExtensionContext {

    private ExtensionContext.ComponentType type;
    private String source;
    private Class<?> extensionClass;
    private Extension extension;

    // EntryStatus is typically reserved for multi-class registrations. For integrated extensions this will typically
    // only contain one value, however external extensions multiple classes marked as @Extension may be present.
    private final Map<Class<?>, ExtensionStatus> entryStatus = SeleneUtils.emptyConcurrentMap();

    public SimpleExtensionContext(ComponentType type, String source, Class<?> extensionClass, Extension extension) {
        this.setType(type);
        this.setSource(source);
        this.setExtensionClass(extensionClass);
        this.setExtension(extension);
    }

    @NotNull
    @Override
    public ComponentType getType() {
        return this.type;
    }

    @Override
    public void setType(@NotNull ExtensionContext.ComponentType type) {
        this.type = type;
    }

    @NotNull
    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public void setSource(@NotNull String source) {
        this.source = source;
    }

    @NotNull
    @Override
    public Class<?> getExtensionClass() {
        return this.extensionClass;
    }

    @Override
    public void setExtensionClass(@NotNull Class<?> extensionClass) {
        this.extensionClass = extensionClass;
    }

    @NotNull
    @Override
    public Extension getExtension() {
        return this.extension;
    }

    @Override
    public void setExtension(@NotNull Extension extension) {
        this.extension = extension;
    }

    @Override
    public void addStatus(@NotNull Class<?> clazz, @NotNull ExtensionStatus status) {
        if (status.getIntValue() < 0) Selene.log().warn("Manually assigning deprecated status to ["
                + clazz.getCanonicalName()
                + "]! Deprecated statuses should only be assigned automatically on annotation presence!");

        if (clazz.isAnnotationPresent(Deprecated.class))
            this.entryStatus.put(clazz, ExtensionStatus.Companion.of(-status.getIntValue()));
        else this.entryStatus.put(clazz, status);
    }

    @Nullable
    @Override
    public ExtensionStatus getStatus(@NotNull Class<?> clazz) {
        return this.entryStatus.getOrDefault(clazz, null);
    }
}
