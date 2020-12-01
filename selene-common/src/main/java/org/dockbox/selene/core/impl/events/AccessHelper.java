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

package org.dockbox.selene.core.impl.events;

import org.dockbox.selene.core.SeleneUtils;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("JavaReflectionMemberAccess")
public final class AccessHelper {
    private static final Lookup DEFAULT_LOOKUP = MethodHandles.lookup();

    public static Class<?> getOutermostEnclosingClass(Class<?> cls) {
        Class<?> enclosingClass;
        while ((enclosingClass = cls.getEnclosingClass()) != null) cls = enclosingClass;
        return cls;
    }

    public static <T extends Annotation> boolean isAnnotationPresentRecursively(Method method, Class<T> annotationClass)
            throws SecurityException {
        return getAnnotationRecursively(method, annotationClass) != null;
    }

    public static <T extends Annotation> T getAnnotationRecursively(Method method, Class<T> annotationClass)
            throws SecurityException {
        T result;
        if ((result = method.getAnnotation(annotationClass)) == null) {
            final String name = method.getName();
            final Class<?>[] params = method.getParameterTypes();

            Class<?> declaringClass = method.getDeclaringClass();
            for (Class<?> supertype : computeAllSupertypes(declaringClass, SeleneUtils.emptySet())) {
                try {
                    Method m = supertype.getDeclaredMethod(name, params);

                    // Static method doesn't override
                    if (Modifier.isStatic(m.getModifiers())) break;

                    if ((result = m.getAnnotation(annotationClass)) != null) break;
                } catch (NoSuchMethodException ignored) {
                    // Current class doesn't have this method
                }
            }
        }
        return result;
    }

    static Set<Class<?>> computeAllSupertypes(Class<?> current, Set<Class<?>> set) {
        Set<Class<?>> next = SeleneUtils.emptySet();
        Class<?> superclass = current.getSuperclass();
        if (superclass != Object.class && superclass != null) {
            set.add(superclass);
            next.add(superclass);
        }
        for (Class<?> interfaceClass : current.getInterfaces()) {
            set.add(interfaceClass);
            next.add(interfaceClass);
        }
        for (Class<?> cls : next) {
            computeAllSupertypes(cls, set);
        }
        return set;
    }

    public static List<Method> getMethodsRecursively(Class<?> cls) throws SecurityException {
        class MethodWrapper {
            final Method method;
            final String name;
            final Class<?>[] paramTypes;
            final Class<?> returnType;
            final boolean canOverride;

            MethodWrapper(Method method) {
                this.method = method;
                this.name = method.getName();
                this.paramTypes = method.getParameterTypes();
                this.returnType = method.getReturnType();
                int modifiers = method.getModifiers();
                this.canOverride = !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers);
            }

            // Uses custom identity function for overriden method handling
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                MethodWrapper that = (MethodWrapper) o;
                return Objects.equals(name, that.name) &&
                        Arrays.equals(paramTypes, that.paramTypes) &&
                        // Java language doesn't allow overloading with different return type,
                        // but bytecode does. So let's check it anyway.
                        Objects.equals(returnType, that.returnType) &&
                        // If either of the two methods are static or final, check declaring class as well
                        ((canOverride && that.canOverride) ||
                                Objects.equals(method.getDeclaringClass(), that.method.getDeclaringClass()));
            }

            @Override
            public int hashCode() {
                return Objects.hash(name, Arrays.hashCode(paramTypes), returnType);
            }
        }

        try {
            Set<MethodWrapper> set = SeleneUtils.emptySet();
            Class<?> current = cls;
            do {
                Method[] methods = current.getDeclaredMethods();
                for (Method m : methods) {
                    // if there's already a method that is overriding the current method, add() will return false
                    set.add(new MethodWrapper(m));
                }
            } while ((current = current.getSuperclass()) != Object.class && current != null);

            // Guava equivalent:       Lists.transform(set, w -> w.method);
            // Stream API equivalent:  set.stream().map(w -> w.method).collect(Collectors.toList());
            List<Method> result = SeleneUtils.emptyList();
            for (MethodWrapper methodWrapper : set) result.add(methodWrapper.method);
            return result;
        } catch (Throwable e) {
            return SeleneUtils.emptyList();
        }
    }

    public static Lookup defaultLookup() {
        return DEFAULT_LOOKUP;
    }

    private AccessHelper() {}
}
