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
import org.dockbox.selene.api.SeleneBootstrap;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.module.annotations.OwnedBy;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.util.Reflect;
import org.jetbrains.annotations.Nullable;

public class Modules {

    /**
     * Gets module.
     *
     * @param type
     *         the type
     *
     * @return the module
     */
    @Nullable
    public static ModuleContainer module(Class<?> type) {
        if (null == type) return null;
        if (type.equals(Selene.class)) {
            if (Reflect.serverAvailable() && SeleneBootstrap.getInstance().getInjector().getStaticBinding(Reflect.getServerClass()).present()) {
                return Modules.module(Provider.provide(Reflect.getServerClass()).getClass());
            }
        }

        if (type.isAnnotationPresent(OwnedBy.class)) {
            OwnedBy owner = type.getAnnotation(OwnedBy.class);
            return Modules.module(owner.value());
        }

        return Exceptional.of(Provider.provide(ModuleManager.class))
                .map(em -> em.getContainer(type).orNull())
                .orNull();
    }

}
