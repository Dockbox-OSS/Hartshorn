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

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.server.Selene;


/**
 Low-level interface which can be used to access and retrieve values from a implementation of this interface using
 the {@link Key} type. This interface provides default implementations, expecting the implementation is the type to
 be modified.
 For more complex implementations these methods can be overridden.

 @param <T>
 The type which the {@link Key} can modify.
 */
@SuppressWarnings("unchecked")
public interface KeyHolder<T> {

    /**
     Apply a given value of type {@link A} using a given {@link Key} type to the implementation of this interface.

     @param <A>
     The type parameter of the value to apply, constrained by the type parameter of the given {@link Key}.
     @param key
     The key to apply, providing the constraints for the type to apply to and the type of the applied value.
     @param appliedValue
     The applied value.
     */
    default <A> void applyKey(Key<T, A> key, A appliedValue) {
        try {
            key.applyTo((T) this, appliedValue);
        } catch (ClassCastException e) {
            Selene.getServer().except("Attempted to apply " + key + " to non-supporting type " + this, e);
        }
    }

    /**
     Retrieves a value from the implementation of this interface using a given {@link Key}. The {@link Key}
     provides the constraints for the returned type using type parameters {@link A}.

     @param <A>
     The type parameter of the returned value, constrained by the type parameter of the given {@link Key}.
     @param key
     The key to use when retrieving a value, providing the constraints for the returned value.

     @return The value wrapped in a {@link Exceptional}, which will contain a {@link ClassCastException}
     if <em>this</em> does not match the constraint of the given {@link Key}.
     */
    default <A> Exceptional<A> getValue(Key<T, A> key) {
        return Exceptional.ofSupplier(() -> key.getFrom((T) this));
    }

}
