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

package org.dockbox.selene.core.impl.objects.keys;

import org.dockbox.selene.core.objects.keys.Key;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class GenericKey<K, A> extends Key<K, A> {

    /**
     Instantiates a new Key using a given setter and getter.@param setter
     The setter, accepting two values. The first being the type to apply to, constrained using type parameter {@link K}.
     The second being the value to apply, constrained using type parameter {@link A}.

     @param getter
     The getter, accepting one value, and returning another. The accepting value being the type to retrieve from,
     constrained using type parameter {@link K}. The return value being the value retreived from the type, constrained
     using type parameter {@link A}.
     */
    public GenericKey(BiConsumer<K, A> setter, Function<K, A> getter) {
        super(setter, getter);
    }
}
