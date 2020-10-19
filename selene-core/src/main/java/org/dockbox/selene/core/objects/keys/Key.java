/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.objects.keys;

import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class Key<K, A> {

    private final BiConsumer<K, A> setter;
    private final Function<K, A> getter;

    protected Key(BiConsumer<K, A> setter, Function<K, A> getter) {
        this.setter = setter;
        this.getter = getter;
    }

    public void applyTo(K itemDataHolder, A appliedValue) {
        this.setter.accept(itemDataHolder, appliedValue);
    }

    public A getFrom(K itemDataHolder) {
        return this.getter.apply(itemDataHolder);
    }

}
