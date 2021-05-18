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

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.module.annotations.Module;
import org.dockbox.selene.api.domain.TypedOwner;

public class ModuleContainer implements TypedOwner {

    private final ModuleContext context;
    private Object instance;

    public ModuleContainer(ModuleContext context) {
        this.context = context;
    }

    @Override
    public String id() {
        return this.context.id();
    }

    public String name() {
        return this.context.name();
    }

    public String[] dependencies() {
        return this.module().dependencies();
    }

    public Class<?> type() {
        return this.context.getType();
    }

    public Module module() {
        return this.context.getModule();
    }

    public Exceptional<Object> instance() {
        return Exceptional.of(this.instance);
    }

    protected void instance(Object instance) {
        this.instance = instance;
    }

    public void status(Class<?> clazz, ModuleStatus status) {
        this.context.addStatus(clazz, status);
    }
}
