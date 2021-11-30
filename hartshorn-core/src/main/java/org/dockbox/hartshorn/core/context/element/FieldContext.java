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

package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.annotations.Property;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.exceptions.Except;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.Getter;

public class FieldContext<T> extends AnnotatedMemberContext<Field> implements TypedElementContext<T> {

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
            final Exceptional<Property> property = this.annotation(Property.class);
            if (property.present() && !"".equals(property.get().setter())) {
                final String setter = property.get().setter();
                final Exceptional<? extends MethodContext<?, ?>> method = this.declaredBy().method(setter, HartshornUtils.asList(this.type()));
                final MethodContext<?, Object> methodContext = (MethodContext<?, Object>) method.orThrowUnchecked(() -> new ApplicationException("Setter for field '" + this.name() + "' (" + setter + ") does not exist!"));
                this.setter = (o, v) -> methodContext.invoke(instance, v);
            } else {
                this.setter = (o, v) -> {
                    try {
                        this.field.set(o, v);
                    }
                    catch (final IllegalAccessException ex) {
                        Except.unchecked(new ApplicationException("Cannot access field " + this.name()));
                    }
                };
            }
        }
        this.setter.accept(instance, (T) value);
    }

    public Exceptional<T> getStatic() {
        return this.get(null);
    }

    public Exceptional<T> get(final Object instance) {
        if (this.getter == null) {
            final Exceptional<Property> property = this.annotation(Property.class);
            if (property.present() && !"".equals(property.get().getter())) {
                final String getter = property.get().getter();
                final Exceptional<? extends MethodContext<?, ?>> method = this.declaredBy().method(getter);
                final MethodContext<?, Object> methodContext = (MethodContext<?, Object>) method.orThrowUnchecked(() -> new ApplicationException("Getter for field '" + this.name() + "' (" + getter + ") does not exist!"));
                this.getter = o -> methodContext.invoke(instance).map(v -> (T) v);
            } else {
                this.getter = o -> Exceptional.of(() -> (T) this.field.get(o));
            }
        }
        return this.getter.apply(instance).orElse(() -> this.type().defaultOrNull());
    }

    @Override
    public String name() {
        return this.field().getName();
    }

    @Override
    public String qualifiedName() {
        return "%s#%s[%s]".formatted(this.declaredBy().qualifiedName(), this.name(), this.type().qualifiedName());
    }

    @Override
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
