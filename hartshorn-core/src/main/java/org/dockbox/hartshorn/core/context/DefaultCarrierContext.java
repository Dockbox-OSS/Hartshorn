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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.domain.Exceptional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The default implementation of {@link CarrierContext}. This implementation stores the active
 * {@link ApplicationContext} directly as a field.
 */
@RequiredArgsConstructor
public class DefaultCarrierContext extends DefaultContext implements CarrierContext {

    @Getter private final ApplicationContext applicationContext;

    @Override
    public <C extends Context> Exceptional<C> first(final Class<C> context) {
        return super.first(this.applicationContext(), context);
    }
}
