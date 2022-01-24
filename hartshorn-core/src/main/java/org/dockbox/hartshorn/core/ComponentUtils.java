/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.core;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public abstract class ComponentUtils {

    public static PropertyDescriptor[] getPropertyDescriptors(final Class<?> clazz) {
        return TypeContext.of(clazz).propertyDescriptors();
    }

    @Nullable
    public static PropertyDescriptor getPropertyDescriptor(final Class<?> clazz, final String propertyName) {
        return TypeContext.of(clazz).propertyDescriptor(propertyName);
    }

    @Nullable
    public static PropertyDescriptor findPropertyForMethod(final Method method) {
        return findPropertyForMethod(method, method.getDeclaringClass());
    }

    @Nullable
    public static PropertyDescriptor findPropertyForMethod(final Method method, final Class<?> clazz) {
        final PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
        for (final PropertyDescriptor pd : pds) {
            if (method.equals(pd.getReadMethod()) || method.equals(pd.getWriteMethod())) {
                return pd;
            }
        }
        return null;
    }

    public static Class<?> findPropertyType(final String propertyName, @Nullable final Class<?>... beanClasses) {
        if (beanClasses != null) {
            for (final Class<?> beanClass : beanClasses) {
                final PropertyDescriptor pd = getPropertyDescriptor(beanClass, propertyName);
                if (pd != null) {
                    return pd.getPropertyType();
                }
            }
        }
        return Object.class;
    }

    public static void copyProperties(final Object source, final Object target) throws ApplicationException {
        copyProperties(source, target, null, (String[]) null);
    }

    public static void copyProperties(final Object source, final Object target, final Class<?> editable) throws ApplicationException {
        copyProperties(source, target, editable, (String[]) null);
    }

    public static void copyProperties(final Object source, final Object target, final String... ignoreProperties) throws ApplicationException {
        copyProperties(source, target, null, ignoreProperties);
    }

    private static void copyProperties(final Object source, final Object target, @Nullable final Class<?> editable,
                                       @Nullable final String... ignoreProperties) throws ApplicationException {
//        Class<?> actualEditable = target.getClass();
//        if (editable != null) {
//            if (!editable.isInstance(target)) {
//                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
//                        "] not assignable to Editable class [" + editable.getName() + "]");
//            }
//            actualEditable = editable;
//        }
//        final PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
//        final List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);
//
//        for (final PropertyDescriptor targetPd : targetPds) {
//            final Method writeMethod = targetPd.getWriteMethod();
//            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
//                final PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
//                if (sourcePd != null) {
//                    final Method readMethod = sourcePd.getReadMethod();
//                    if (readMethod != null) {
//                        final ResolvableType sourceResolvableType = ResolvableType.forMethodReturnType(readMethod);
//                        final ResolvableType targetResolvableType = ResolvableType.forMethodParameter(writeMethod, 0);
//
//                        // Ignore generic types in assignable check if either ResolvableType has unresolvable generics.
//                        final boolean isAssignable =
//                                (sourceResolvableType.hasUnresolvableGenerics() || targetResolvableType.hasUnresolvableGenerics() ?
//                                        ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType()) :
//                                        targetResolvableType.isAssignableFrom(sourceResolvableType));
//
//                        if (isAssignable) {
//                            try {
//                                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
//                                    readMethod.setAccessible(true);
//                                }
//                                final Object value = readMethod.invoke(source);
//                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
//                                    writeMethod.setAccessible(true);
//                                }
//                                writeMethod.invoke(target, value);
//                            }
//                            catch (final Throwable ex) {
//                                throw new ApplicationException(
//                                        "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}
