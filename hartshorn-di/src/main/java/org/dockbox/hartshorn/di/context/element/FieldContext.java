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

package org.dockbox.hartshorn.di.context.element;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.Getter;

@SuppressWarnings("unchecked")
public class FieldContext<T> extends AnnotatedMemberContext<Field> implements ModifierCarrier {

    private static final Map<Field, FieldContext<?>> cache = HartshornUtils.emptyConcurrentMap();

    @Getter private final Field field;

    private TypeContext<?> declaredBy;
    private TypeContext<T> type;

    private Function<Object, Exceptional<T>> getter;
    private BiConsumer<Object, T> setter;

    private FieldContext(final Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public static Exceptional<FieldContext<?>> of(final TypeContext<?> type, final String field) {
        return type.field(field);
    }

    public static FieldContext<?> of(final Field field) {
        if (cache.containsKey(field))
            return cache.get(field);

        return new FieldContext<>(field);
    }

    public void set(final Object instance, final Object value) {
        if (this.setter == null) {
                this.setter = (o, v) -> {
                    try {
                        this.field.set(o, v);
                    }
                    catch (final IllegalAccessException ex) {
                        throw new ApplicationException("Cannot access field " + this.name()).runtime();
                    }
                };
        }
        this.setter.accept(instance, (T) value);
    }

    public Exceptional<T> getStatic() {
        return this.get(null);
    }

    public Exceptional<T> get(final Object instance) {
        if (this.getter == null) {
            this.getter = o -> Exceptional.of(() -> (T) this.field.get(o));
        }
        return this.getter.apply(instance);
    }

    public String name() {
        return this.field().getName();
    }

    public TypeContext<T> type() {
        if (this.type == null) {
            this.type = (TypeContext<T>) TypeContext.of(this.field().getType());
        }
        return this.type;
    }

    public TypeContext<?> declaredBy() {
        if (this.declaredBy == null) {
            this.declaredBy = TypeContext.of(this.field().getDeclaringClass());
        }
        return this.declaredBy;
    }

    @Override
    protected Field element() {
        return this.field();
    }

    public boolean isStatic() {
        return this.has(AccessModifier.STATIC);
    }

    public boolean isTransient() {
        return this.has(AccessModifier.TRANSIENT);
    }
}
