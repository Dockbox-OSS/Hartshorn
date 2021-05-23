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

package org.dockbox.selene.api.module.lookup;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.api.domain.SimpleTypedOwner;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.api.entity.annotations.Entity;
import org.dockbox.selene.api.module.Modules;
import org.dockbox.selene.api.module.SimpleModuleContext;
import org.dockbox.selene.api.module.annotations.Module;
import org.dockbox.selene.di.annotations.Binds;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Locale;

@Binds(OwnerLookup.class)
public class ModuleOwnerLookup implements OwnerLookup {
    @Override
    public TypedOwner lookup(Class<?> type) {
        return Exceptional.of(Modules.module(type))
                .map(TypedOwner.class::cast)
                .then(() -> {
                    // In case modules have not been scanned yet, typically only caused by preloads accessing headers
                    if (type.isAnnotationPresent(Module.class)) {
                        return new SimpleModuleContext(type, type.getAnnotation(Module.class));
                    } else if (type.isAnnotationPresent(Entity.class)) {
                        return SimpleTypedOwner.of(type.getAnnotation(Entity.class).value());
                    } else if (Selene.class.equals(type)) {
                        return SimpleTypedOwner.of(SeleneInformation.PROJECT_ID);
                    } else if (type.isAnnotationPresent(Service.class)) {
                        return SimpleTypedOwner.of(this.generateId(type));
                    } else {
                        return null;
                    }
                }).orNull();
    }

    private String generateId(Class<?> type) {
        String typeName = type.getSimpleName();
        if (typeName.endsWith("Service")) typeName = typeName.substring(0, typeName.length() - 7);
        final String[] parts = SeleneUtils.splitCapitals(typeName);
        return String.join("-", parts).toLowerCase(Locale.ROOT);
    }
}
