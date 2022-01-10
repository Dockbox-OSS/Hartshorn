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

package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.core.services.parameter.RuleBasedParameterLoader;
import org.dockbox.hartshorn.web.processing.rules.BodyRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.HeaderRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.RequestQueryParameterRule;
import org.dockbox.hartshorn.web.processing.rules.ServletRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.ServletResponseParameterRule;

import javax.inject.Named;

@ComponentBinding(value = ParameterLoader.class, named = @Named("mvc_webserver"))
public class MvcParameterLoader extends RuleBasedParameterLoader<MvcParameterLoaderContext> {

    public MvcParameterLoader() {
        this.add(new ViewModelParameterRule());
        this.add(new BodyRequestParameterRule());
        this.add(new HeaderRequestParameterRule());
        this.add(new RequestQueryParameterRule());
        this.add(new ServletRequestParameterRule());
        this.add(new ServletResponseParameterRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterContext<T> parameter, final int index, final MvcParameterLoaderContext context, final Object... args) {
        return context.applicationContext().get(parameter.type());
    }
}
