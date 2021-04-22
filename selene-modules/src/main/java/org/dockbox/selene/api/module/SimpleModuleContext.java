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

package org.dockbox.selene.api.module;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.module.annotations.Module;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SimpleModuleContext implements ModuleContext {

    // EntryStatus is typically reserved for multi-class registrations. For integrated modules this
    // will typically
    // only contain one value, however external modules multiple classes marked as @Module may be
    // present.
    private final Map<Class<?>, ModuleStatus> entryStatus = SeleneUtils.emptyConcurrentMap();
    private String source;
    private Class<?> moduleType;
    private Module module;

    public SimpleModuleContext(String source, Class<?> moduleType, Module module) {
        this.setSource(source);
        this.setType(moduleType);
        this.setModule(module);
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
    public Class<?> getType() {
        return this.moduleType;
    }

    @Override
    public void setType(@NotNull Class<?> module) {
        this.moduleType = module;
    }

    @NotNull
    @Override
    public Module getModule() {
        return this.module;
    }

    @Override
    public void setModule(@NotNull Module module) {
        this.module = module;
    }

    @Override
    public void addStatus(@NotNull Class<?> clazz, @NotNull ModuleStatus status) {
        if (0 > status.getIntValue())
            Selene.log().warn("Manually assigning deprecated status to [" + clazz.getCanonicalName() + "]! Deprecated statuses should only be assigned automatically on annotation presence!");

        if (clazz.isAnnotationPresent(Deprecated.class))
            this.entryStatus.put(clazz, ModuleStatus.of(-status.getIntValue()));
        else this.entryStatus.put(clazz, status);
    }

    @Nullable
    @Override
    public ModuleStatus getStatus(@NotNull Class<?> clazz) {
        return this.entryStatus.getOrDefault(clazz, null);
    }
}
