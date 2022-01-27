/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.annotations.Property;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Getter;

public final class FieldContext<T> extends AnnotatedMemberContext<Field> implements TypedElementContext<T> {

    private static final Map<Field, FieldContext<?>> cache = new ConcurrentHashMap<>();

    @Getter private final Field field;
    @Getter(AccessLevel.PROTECTED) private final boolean accessible;

    private TypeContext<?> declaredBy;
    private TypeContext<T> type;

    private Function<Object, Exceptional<T>> getter;
    private BiConsumer<Object, T> setter;

    private FieldContext(final Field field) {
        this.field = field;
        this.accessible = this.field.trySetAccessible();
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
        // Silently fail if field is not accessible
        if (!this.accessible()) return;

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
                        ExceptionHandler.unchecked(new ApplicationException("Cannot access field " + this.name()));
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
        // Silently fail if field is not accessible
        if (!this.accessible())
            return Exceptional.of(new ApplicationException("Field '" + this.name() + "' is not accessible!"));

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
