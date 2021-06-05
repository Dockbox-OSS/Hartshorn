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

package org.dockbox.hartshorn.di.types;

import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.di.properties.InjectorProperty;

import lombok.Getter;

@Getter
public class SampleWiredPopulatedType implements SampleInterface, InjectableType {

    private final String name;

    @Wired
    private SampleField field;
    private boolean enabled = false;

    @Wired
    public SampleWiredPopulatedType(String name) {
        this.name = name;
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        this.enabled = true;
    }

}
