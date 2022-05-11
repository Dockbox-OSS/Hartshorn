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

package org.dockbox.hartshorn.data;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.data.annotations.UseConfigurations;
import org.dockbox.hartshorn.data.annotations.Value;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.component.processing.AutomaticActivation;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.FieldContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.reflect.NotPrimitiveException;
import org.dockbox.hartshorn.util.TypeConversionException;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;

import java.util.Collection;

/**
 * Looks up and populates fields annotated with {@link Value}.
 */
@AutomaticActivation
public class ConfigurationComponentPostProcessor implements ComponentPostProcessor<UseConfigurations> {

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext<T> processingContext) {
        return !key.type().fields(Value.class).isEmpty();
    }

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        TypeContext<?> instanceType = key.type();
        if (instance != null) instanceType = TypeContext.unproxy(context, instance);

        final ValueLookup valueLookup = context.get(ValueLookup.class);

        for (final FieldContext<?> field : instanceType.fields(Value.class)) {
            try {
                final Value annotation = field.annotation(Value.class).get();

                final String valueKey = annotation.value();
                final Exceptional<?> property;

                if (field.type().childOf(Collection.class)) {
                    final Collection<?> values = valueLookup.getValues(valueKey);
                    if (!field.type().is(Collection.class)) {
                        final Exceptional<? extends ConstructorContext<?>> constructor = field.type().constructor(Collection.class);
                        if (constructor.absent()) throw new IllegalStateException("No compatible constructor found to convert collection to " + field.type().qualifiedName());
                        else {
                            final ConstructorContext<?> constructorContext = constructor.get();
                            final Collection<?> collection = (Collection<?>) constructorContext.createInstance(values).orNull();
                            property = Exceptional.of(collection);
                        }
                    } else {
                        property = Exceptional.of(values);
                    }
                }
                else {
                    property = valueLookup.getValue(valueKey);
                }

                if (property.absent()) {
                    context.log().debug("Property {} for field {} is empty, but field has a default value, using default value (note this may be null)", valueKey, field.name());
                    continue;
                }

                Object value = property.get();
                context.log().debug("Populating value for configuration field '%s' in %s (key: %s), value is not logged.".formatted(field.name(), valueKey, field.type().name()));

                if ((!field.type().childOf(String.class)) && (value instanceof String stringValue)) {
                    value = TypeContext.toPrimitive(field.type(), stringValue);
                }
                field.set(instance, value);
            }
            catch (final TypeConversionException | NotPrimitiveException e) {
                context.log().warn("Could not prepare value field " + field.name() + " in " + instanceType.name());
                context.handle(e);
            }
        }

        return instance;
    }

    @Override
    public Class<UseConfigurations> activator() {
        return UseConfigurations.class;
    }
}
