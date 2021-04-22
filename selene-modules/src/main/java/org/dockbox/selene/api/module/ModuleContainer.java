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

import org.dockbox.selene.api.module.annotations.Module;

public class ModuleContainer {

    private final ModuleContext context;

    public ModuleContainer(ModuleContext context) {
        this.context = context;
    }

    public String id() {
        return this.module().id();
    }

    public String name() {
        return this.module().name();
    }

    public String description() {
        return this.module().description();
    }

    public String[] authors() {
        return this.module().authors();
    }

    public String[] dependencies() {
        return this.module().dependencies();
    }

    public String source() {
        return this.context.getSource();
    }

    public Class<?> type() {
        return this.context.getType();
    }

    public Module module() {
        return this.context.getModule();
    }

    public void status(Class<?> clazz, ModuleStatus status) {
        this.context.addStatus(clazz, status);
    }
}
