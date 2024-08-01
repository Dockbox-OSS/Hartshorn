/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.view;

import org.dockbox.hartshorn.util.introspect.IllegalIntrospectionException;
import org.dockbox.hartshorn.util.introspect.annotations.Property;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Field;

/**
 * Represents a view of a field. This view is used to access the field's value, and to set the field's value.
 *
 * @param <Parent> The type of the field's declaring class
 * @param <FieldType> The type of the field
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface FieldView<Parent, FieldType> extends ModifierCarrierView, AnnotatedGenericTypeView<FieldType> {

    /**
     * Returns the {@link Field} represented by this view, if available.
     *
     * @return the field represented by this view, if available
     */
    Option<Field> field();

    /**
     * Sets the value of the field represented by this view on the given instance. If the field is static, the instance
     * is ignored. If the field is annotated with {@link Property}, an attempt is made to use the field's setter method
     * as configured in {@link Property#setter()}, if available.
     *
     * @param instance the instance on which to set the field's value
     * @param value the value to set
     *
     * @throws IllegalIntrospectionException if the field is final, the given value does not match the field's type, or
     *        if the field is not accessible. Also thrown if the configured setter method does not exist.
     */
    void set(Object instance, Object value) throws Throwable;

    /**
     * Gets the value of the field represented by this view on the given instance. If the field is static, the instance
     * is ignored. If the field is annotated with {@link Property}, an attempt is made to use the field's getter method
     * as configured in {@link Property#getter()}, if available.
     *
     * @param instance the instance from which to get the field's value
     *
     * @return the value of the field represented by this view on the given instance
     *
     * @throws IllegalIntrospectionException if the field is not accessible, or the configured getter method does not exist
     */
    Option<FieldType> get(Object instance) throws Throwable;

    /**
     * Gets the value of the field represented by this view, as a static field. If the field is annotated with
     * {@link Property}, an attempt is made to use the field's getter method as configured in {@link Property#getter()},
     * if available.
     *
     * @return the value of the field represented by this view
     *
     * @throws IllegalIntrospectionException if the field is not accessible, not static, or the configured getter method
     *       does not exist
     */
    Option<FieldType> getStatic() throws Throwable;

    /**
     * Returns the element's declaring type.
     *
     * @return the element's declaring type
     */
    TypeView<Parent> declaredBy();
}
