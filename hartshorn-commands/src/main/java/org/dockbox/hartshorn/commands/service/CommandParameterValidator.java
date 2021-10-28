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

package org.dockbox.hartshorn.commands.service;

import org.dockbox.hartshorn.core.boot.annotations.PostBootstrap;
import org.dockbox.hartshorn.core.boot.annotations.UseBootstrap;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

@Service(activators = UseBootstrap.class)
public class CommandParameterValidator {

    @PostBootstrap
    public void preload(final ApplicationContext context) {
        final MethodContext<?, CommandParameterValidator> preload = TypeContext.of(this).method("preload", ApplicationContext.class).get();
        final ParameterContext<?> parameter = preload.parameters().get(0);
        if (!"context".equals(parameter.name())) {
            context.log().warn("Parameter names are obfuscated, this will cause commands with @Parameter to be unable to inject arguments.");
            context.log().warn("   Add -parameters to your compiler args to keep parameter names.");
            context.log().warn("   See: https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html for more information.");
        }
    }

}
