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

package org.dockbox.selene.common.command.test;

import org.dockbox.selene.common.command.DefaultCommandBus;
import org.dockbox.selene.common.command.values.AbstractArgumentElement;
import org.dockbox.selene.common.command.values.AbstractArgumentValue;
import org.dockbox.selene.common.command.values.AbstractFlagCollection;
import org.dockbox.selene.api.util.SeleneUtils;

import java.util.List;

public class TestCommandBus extends DefaultCommandBus
{


    @Override
    protected AbstractArgumentElement<?> wrapElements(List<AbstractArgumentElement<?>> elements) {
        return elements.get(0);
    }

    @Override
    protected TestArgumentValue generateArgumentValue(String type, String permission, String key) {
        return new TestArgumentValue(permission, key, type);
    }

    @Override
    protected AbstractFlagCollection<?> createEmptyFlagCollection() {
        return new AbstractFlagCollection<Object>() {
            @Override
            public void addNamedFlag(String name) {

            }

            @Override
            public void addNamedPermissionFlag(String name, String permission) {

            }

            @Override
            public void addValueBasedFlag(String name, AbstractArgumentValue<?> value) {

            }

            @Override
            public List<AbstractArgumentElement<?>> buildAndCombines(AbstractArgumentElement<?> element) {
                return SeleneUtils.asUnmodifiableList(element);
            }
        };
    }

    @Override
    public void apply() {

    }
}
