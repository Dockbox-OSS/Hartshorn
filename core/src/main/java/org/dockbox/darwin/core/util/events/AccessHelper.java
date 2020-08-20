package org.dockbox.darwin.core.util.events;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.lang.invoke.MethodHandles.Lookup;

@SuppressWarnings("JavaReflectionMemberAccess")
final class AccessHelper {
    private static final Lookup DEFAULT_LOOKUP = MethodHandles.lookup();

    private static final MethodHandle TRY_SET_ACCESSIBLE_METHODHANDLE;

    static boolean trySuppressAccessControl = true;

    static boolean pretendJava8 = false;

    static boolean pretendJava9 = false;

    static {
        MethodHandle methodhandle = null;
        try {
            Method method = AccessibleObject.class.getMethod("trySetAccessible");
            methodhandle = MethodHandles.lookup().unreflect(method);
        } catch (NoSuchMethodException ignore) {
            // Java 1.8 or lower doesn't have trySetAccessible() method
        } catch (ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
        TRY_SET_ACCESSIBLE_METHODHANDLE = methodhandle;
    }

    public static MethodHandle unreflectMethodHandle(Lookup lookup, Method method) throws SecurityException {
        try {
            return unreflectMethodHandle0(lookup, method);
        } catch (IllegalAccessException ex) {
            throw new SecurityException(String.format("Cannot access method [%s] with %s lookup [%s]",
                    method.toGenericString(),
                    AccessHelper.isDefaultLookup(lookup) ? "default" : "provided",
                    lookup), ex);
        }
    }

    static MethodHandle unreflectMethodHandle0(Lookup lookup, Method method) throws IllegalAccessException {
        boolean accessible = method.isAccessible();
        if ((isAtLeastJava9() && !pretendJava8) || pretendJava9) {  // Java 9+
            try {
                return lookup.unreflect(method);
            } catch (IllegalAccessException ex) {
                if (accessible) throw ex; // Already accessible but still failed? Giving up.

                boolean tryAgain = false;
                if (trySuppressAccessControl) try {
                    tryAgain = trySetAccessible(method);
                } catch (SecurityException ex2) {
                    try {
                        Throwable t = ex;
                        while (t.getCause() != null) t = t.getCause();
                        t.initCause(ex2);  // set the root cause of `ex` to `ex2`
                    } catch (IllegalStateException ex3) {
                        ex.addSuppressed(ex2);
                    }
                }

                if (!tryAgain) {
                    Lookup newLookup = narrowLookupClass(lookup, method);
                    if (lookup != newLookup) {
                        if (lookup.lookupModes() == newLookup.lookupModes()) {
                            lookup = newLookup;
                            tryAgain = true;
                        } else try { // lookupMode changed
                            return newLookup.unreflect(method);
                        } catch (IllegalAccessException ex2) {
                            ex2.addSuppressed(ex);
                            ex = ex2;
                        }
                    }
                }

                if (tryAgain) {
                    return lookup.unreflect(method);
                } else {
                    throw ex;
                }
            }
        } else {  // Java 1.8 or lower
            Exception suppressed = null;
            if (trySuppressAccessControl && !accessible) try {
                method.setAccessible(true);
                accessible = true;
            } catch (SecurityException ex) {
                // if the method is public, then setAccessible() is not necessary, we can safely ignore this;
                // if the method is not public, the SecurityException will be thrown
                // as a suppressed exception when calling lookup.unreflect().
                suppressed = ex;
            }

            if (!accessible) {
                Lookup newLookup = narrowLookupClass(lookup, method);
                if (lookup != newLookup) {
                    if (lookup.lookupModes() == newLookup.lookupModes()) {
                        lookup = newLookup;
                    } else try {  // lookupMode changed
                        return newLookup.unreflect(method);
                    } catch (IllegalAccessException ex) {
                        if (suppressed != null) suppressed.addSuppressed(ex);
                        else suppressed = ex;
                    }
                }
            }

            try {
                return lookup.unreflect(method);
            } catch (Throwable t) {
                if (suppressed != null) t.addSuppressed(suppressed);
                throw t;
            }
        }
    }

    static Lookup narrowLookupClass(Lookup lookup, Method method) {
        Class<?> lookupClass = lookup.lookupClass();
        Class<?> targetClass = method.getDeclaringClass();
        if (lookupClass != targetClass && isSamePackageMember(lookupClass, targetClass)) {
            // targetClass and lookupClass are nestmate, the lookupMode should not change
            return lookup.in(targetClass);
        }
        return lookup;
    }

    public static boolean trySetAccessible(AccessibleObject object)
            throws UnsupportedOperationException, SecurityException {
        if (!isAtLeastJava9()) throw new UnsupportedOperationException(
                new NoSuchMethodException(AccessibleObject.class.getName() + ".trySetAccessible()"));

        try {
            return (boolean) TRY_SET_ACCESSIBLE_METHODHANDLE.invoke(object);
        } catch (WrongMethodTypeException | ClassCastException ex) {
            throw new AssertionError(ex);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static boolean isAtLeastJava9() {
        return TRY_SET_ACCESSIBLE_METHODHANDLE != null;
    }

    public static boolean isSamePackage(Class<?> cls1, Class<?> cls2) {
        return cls1 == cls2 || Objects.equals(cls1.getPackage().getName(), cls2.getPackage().getName());
    }

    public static boolean isSamePackageMember(Class<?> cls1, Class<?> cls2) {
        return isSamePackage(cls1, cls2) && getOutermostEnclosingClass(cls1) == getOutermostEnclosingClass(cls2);
    }

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
            for (Class<?> supertype : computeAllSupertypes(declaringClass, new LinkedHashSet<>())) {
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
        Set<Class<?>> next = new LinkedHashSet<>();
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
            Set<MethodWrapper> set = new LinkedHashSet<>();
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
            List<Method> result = new ArrayList<>(set.size());
            for (MethodWrapper methodWrapper : set) result.add(methodWrapper.method);
            return result;
        } catch (Throwable e) {
            return new ArrayList<>();
        }
    }

    static Lookup defaultLookup() {
        return DEFAULT_LOOKUP;
    }

    static boolean isDefaultLookup(Lookup lookup) {
        return lookup == DEFAULT_LOOKUP;
    }

    private AccessHelper() {}
}
