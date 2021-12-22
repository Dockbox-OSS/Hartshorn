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

package org.dockbox.hartshorn.core.services;

public enum ServiceOrder {
    FIRST, EARLY, NORMAL, LATE, LAST;

    public static final ServiceOrder[] VALUES = ServiceOrder.values();

    /**
     * Indicates which service orders can be performed during phase 1. During this phase, component
     * processors are allowed to discard existing instances and return new ones. This can be used to
     * create proxy instances.
     */
    public static final ServiceOrder[] PHASE_1 = new ServiceOrder[] {FIRST, EARLY, NORMAL};

    /**
     * Indicates which service orders can be performed during phase 2. During this phase, component
     * processors are not allowed to discard existing instances and return new ones. This limits the
     * behavior of these processors to only return the same instance, albeit with different state.
     */
    public static final ServiceOrder[] PHASE_2 = new ServiceOrder[] {LATE, LAST};
}
