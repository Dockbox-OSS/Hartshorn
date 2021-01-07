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

package org.dockbox.selene.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

class InternalMethodWrapper {
    final Method method;
    final String name;
    final Class<?>[] paramTypes;
    final Class<?> returnType;
    final boolean canOverride;

    InternalMethodWrapper(Method method) {
        this.method = method;
        this.name = method.getName();
        this.paramTypes = method.getParameterTypes();
        this.returnType = method.getReturnType();
        int modifiers = method.getModifiers();
        this.canOverride = !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, Arrays.hashCode(this.paramTypes), this.returnType);
    }

    // Uses custom identity function for overriden method handling
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (null == o || this.getClass() != o.getClass()) return false;
        InternalMethodWrapper that = (InternalMethodWrapper) o;
        //noinspection OverlyComplexBooleanExpression
        return Objects.equals(this.name, that.name)
            && Arrays.equals(this.paramTypes, that.paramTypes)
            // Java doesn't allow overloading with different return type,
            // but bytecode does. So let's check it anyway.
            && Objects.equals(this.returnType, that.returnType)
            // If either of the two methods are static or final, check declaring class as well
            && ((this.canOverride && that.canOverride)
            || Objects.equals(this.method.getDeclaringClass(), that.method.getDeclaringClass()));
    }
}
