/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.reflect.view;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.Property;
import org.dockbox.hartshorn.util.introspect.IllegalIntrospectionException;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionModifierCarrierView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ReflectionFieldView<Parent, FieldType> extends ReflectionAnnotatedElementView implements FieldView<Parent, FieldType>, ReflectionModifierCarrierView {

    private final Field field;
    private final Introspector introspector;

    private Function<Object, Attempt<FieldType, Throwable>> getter;
    private BiConsumer<Object, FieldType> setter;

    public ReflectionFieldView(final Introspector introspector, final Field field) {
        super(introspector);
        if (!field.trySetAccessible()) {
            introspector.applicationContext().log().warn("Unable to set field {} accessible", field);
        }
        this.field = field;
        this.introspector = introspector;
    }

    @Override
    public Field field() {
        return this.field;
    }

    @Override
    public void set(final Object instance, final Object value) {
        if (value != null && !this.type().isInstance(value)) {
            throw new IllegalIntrospectionException(this, "Cannot set field " + this.field.getName() + " to value of type " + value.getClass().getName() + ", expected " + this.type().name());
        }

        if (this.setter == null) {
            final Option<Property> property = this.annotations().get(Property.class);
            if (property.present() && !"".equals(property.get().setter())) {
                final String setter = property.get().setter();
                final Option<MethodView<Parent, ?>> method = this.declaredBy().methods().named(setter, List.of(this.type().type()));
                final MethodView<Parent, ?> methodView = method.orElseThrow(() -> new IllegalIntrospectionException(this, "Setter for field '" + this.name() + "' (" + setter + ") does not exist!"));
                this.setter = (o, v) -> methodView.invoke(this.declaredBy().cast(instance), v);
            } else {
                this.setter = (o, v) -> {
                    try {
                        this.field.set(o, v);
                    }
                    catch (final IllegalAccessException e) {
                        throw new IllegalIntrospectionException(this, e.getMessage());
                    }
                };
            }
        }
        this.setter.accept(instance, (FieldType) value);
    }

    @Override
    public Attempt<FieldType, Throwable> get(final Parent instance) {
        if (this.getter == null) {
            final Option<Property> property = this.annotations().get(Property.class);
            if (property.present() && !"".equals(property.get().getter())) {
                final String getter = property.get().getter();
                final Option<MethodView<Parent, ?>> method = this.declaredBy().methods().named(getter);
                final MethodView<Parent, ?> methodContext = method.orElseThrow(() -> new IllegalIntrospectionException(this, "Getter for field '" + this.name() + "' (" + getter + ") does not exist!"));
                this.getter = o -> methodContext.invoke(instance).map(this.type()::cast);
            } else {
                this.getter = o -> Attempt.of(() -> {
                    try {
                        return this.type().cast(this.field.get(o));
                    }
                    catch (final IllegalAccessException e) {
                        throw new IllegalIntrospectionException(this, e.getMessage());
                    }
                }, Throwable.class);
            }
        }
        return this.getter.apply(instance).orCompute(() -> this.type().defaultOrNull());
    }

    @Override
    public Attempt<FieldType, Throwable> getStatic() {
        return this.get(null);
    }

    @Override
    public String name() {
        return this.field.getName();
    }

    @Override
    public String qualifiedName() {
        return "%s#%s[%s]".formatted(this.declaredBy().qualifiedName(), this.name(), this.type().qualifiedName());
    }

    @Override
    public TypeView<FieldType> type() {
        return (TypeView<FieldType>) this.introspector.introspect(this.field.getType());
    }

    @Override
    public TypeView<FieldType> genericType() {
        return (TypeView<FieldType>) this.introspector.introspect(this.field.getGenericType());
    }

    @Override
    public TypeView<Parent> declaredBy() {
        return (TypeView<Parent>) this.introspector.introspect(this.field.getDeclaringClass());
    }

    @Override
    public boolean isProtected() {
        return Modifier.isProtected(this.field.getModifiers());
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(this.field.getModifiers());
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(this.field.getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.field.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.field.getModifiers());
    }

    @Override
    public boolean isTransient() {
        return Modifier.isTransient(this.field.getModifiers());
    }

    @Override
    protected AnnotatedElement annotatedElement() {
        return this.field;
    }

    @Override
    public Attempt<FieldType, Throwable> getWithContext() {
        final Parent parent = this.introspector.applicationContext().get(this.declaredBy().type());
        return this.get(parent);
    }

    @Override
    public int modifiers() {
        return this.field.getModifiers();
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        collector.property("name").write(this.name());
        collector.property("elementType").write("field");
        collector.property("type").write(this.genericType());
        collector.property("declaredBy").write(this.declaredBy());
    }
}
