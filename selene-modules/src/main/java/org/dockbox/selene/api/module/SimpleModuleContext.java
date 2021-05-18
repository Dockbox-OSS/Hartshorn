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

import java.util.Locale;
import java.util.Map;

public class SimpleModuleContext implements ModuleContext {

    // EntryStatus is typically reserved for multi-class registrations. For integrated modules this
    // will typically
    // only contain one value, however external modules multiple classes marked as @Module may be
    // present.
    private final Map<Class<?>, ModuleStatus> entryStatus = SeleneUtils.emptyConcurrentMap();
    private Class<?> moduleType;
    private Module module;
    private String id;
    private String name;

    public SimpleModuleContext(Class<?> moduleType, Module module) {
        this.type(moduleType);
        this.module(module);
        String typeName = moduleType.getSimpleName();
        if (typeName.endsWith("Module")) {
            typeName = typeName.substring(0, typeName.length()-6); // Remove module suffix
        }
        if ("".equals(module.name())) {
            this.name = String.join(" ", SeleneUtils.splitCapitals(typeName));
        }
        if ("".equals(module.id())) {
            this.id = String.join("-", SeleneUtils.splitCapitals(typeName)).toLowerCase(Locale.ROOT);
        }
    }

    @NotNull
    @Override
    public Class<?> type() {
        return this.moduleType;
    }

    @Override
    public void type(@NotNull Class<?> module) {
        this.moduleType = module;
    }

    @NotNull
    @Override
    public Module module() {
        return this.module;
    }

    @Override
    public void module(@NotNull Module module) {
        this.module = module;
    }

    @Override
    public void status(@NotNull Class<?> clazz, @NotNull ModuleStatus status) {
        if (0 > status.getIntValue())
            Selene.log().warn("Manually assigning deprecated status to [" + clazz.getCanonicalName() + "]! Deprecated statuses should only be assigned automatically on annotation presence!");

        if (clazz.isAnnotationPresent(Deprecated.class))
            this.entryStatus.put(clazz, ModuleStatus.of(-status.getIntValue()));
        else this.entryStatus.put(clazz, status);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Nullable
    @Override
    public ModuleStatus status(@NotNull Class<?> clazz) {
        return this.entryStatus.getOrDefault(clazz, null);
    }
}
