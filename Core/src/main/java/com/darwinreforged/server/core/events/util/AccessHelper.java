package com.darwinreforged.server.core.events.util;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.lang.invoke.MethodHandles.Lookup;

/**
 Access helper.

 @author Andy Li
 @since 1.3 */
@SuppressWarnings("JavaReflectionMemberAccess")
final class AccessHelper {
    /**
     * Default lookup object.
     */
    private static final Lookup DEFAULT_LOOKUP = MethodHandles.lookup();

    /**
     * MethodHandle to {@link AccessibleObject}#trySetAccessible().
     */
    private static final MethodHandle TRY_SET_ACCESSIBLE_METHODHANDLE;

    /**
     Indicate whether we should try to bypass access control using {@code setAccessible(true)}.
     For testing purpose only.
     */
    static boolean trySuppressAccessControl = true;

    /** Indicate whether we should pretend we are running on Java 1.8. For testing purpose only. */
    static boolean pretendJava8 = false;

    /** Indicate whether we should pretend we are running on Java 9. For testing purpose only. */
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

    /**
     Makes a direct method handle to the {@code method}.

     @param lookup
     the {@linkplain Lookup lookup object} used in method handle creation
     @param method
     the method

     @return a method handle which can invoke the reflected method

     @throws SecurityException
     if access checking fails and cannot be bypassed
     @see Lookup#unreflect(Method) Lookup#unreflect(Method)
     */
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

    /**
     Makes a direct method handle to the {@code method}.

     @param lookup
     the {@linkplain Lookup lookup object} used in method handle creation
     @param method
     the method

     @return a method handle which can invoke the reflected method

     @throws IllegalAccessException
     if access checking fails and cannot be bypassed
     @see Lookup#unreflect(Method) Lookup#unreflect(Method)
     */
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

    /**
     Narrow lookup class lookup.

     @param lookup
     the lookup
     @param method
     the method

     @return the lookup
     */
    static Lookup narrowLookupClass(Lookup lookup, Method method) {
        Class<?> lookupClass = lookup.lookupClass();
        Class<?> targetClass = method.getDeclaringClass();
        if (lookupClass != targetClass && isSamePackageMember(lookupClass, targetClass)) {
            // targetClass and lookupClass are nestmate, the lookupMode should not change
            return lookup.in(targetClass);
        }
        return lookup;
    }

    /**
     Sets the {@code accessible} flag for the specified reflected object to true if possible.

     @param object
     the object

     @return {@code true} if the {@code accessible} flag is set to {@code true};         {@code false} if access cannot be enabled.

     @throws UnsupportedOperationException
     if the current runtime doesn't have trySetAccessible() method
     @throws SecurityException
     if the request is denied by the security manager
     */
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

    /**
     Checks if the current runtime version is Java 9 or higher.

     @return {@code true} if the current runtime version is at least Java 9, {@code false} otherwise
     */
    public static boolean isAtLeastJava9() {
        return TRY_SET_ACCESSIBLE_METHODHANDLE != null;
    }

    /**
     Checks if two classes is in the same package.

     @param cls1
     the cls 1
     @param cls2
     the cls 2

     @return {@code true} if they're in the same package, {@code false} otherwise
     */
    public static boolean isSamePackage(Class<?> cls1, Class<?> cls2) {
        return cls1 == cls2 || Objects.equals(cls1.getPackage().getName(), cls2.getPackage().getName());
    }

    /**
     Checks if two classes are nestmate.

     @param cls1
     the cls 1
     @param cls2
     the cls 2

     @return {@code true} if they're nestmate, {@code false} otherwise
     */
    public static boolean isSamePackageMember(Class<?> cls1, Class<?> cls2) {
        return isSamePackage(cls1, cls2) && getOutermostEnclosingClass(cls1) == getOutermostEnclosingClass(cls2);
    }

    /**
     Returns the outermost enclosing class of the specified class.

     @param cls
     the cls

     @return the outermost enclosing class
     */
    public static Class<?> getOutermostEnclosingClass(Class<?> cls) {
        Class<?> enclosingClass;
        while ((enclosingClass = cls.getEnclosingClass()) != null) cls = enclosingClass;
        return cls;
    }

    /**
     Returns true if an annotation for the specified type is present on the specified method or its super method.

     @param <T>
     the type of the annotation to search for
     @param method
     the method
     @param annotationClass
     the annotation class

     @return {@code true} if present, {@code false} otherwise

     @throws SecurityException
     if a security manager denied access to the method's super method
     @see #getAnnotationRecursively(Method, Class) #getAnnotationRecursively(Method, Class)
     */
    public static <T extends Annotation> boolean isAnnotationPresentRecursively(Method method, Class<T> annotationClass)
            throws SecurityException {
        return getAnnotationRecursively(method, annotationClass) != null;
    }

    /**
     Searches the method's annotations for the specified type recursively.

     @param <T>
     the type of the annotation to search for
     @param method
     the method
     @param annotationClass
     the annotation class

     @return the annotation if present, {@code null} otherwise

     @throws SecurityException
     if a security manager denied access to the method's super method
     */
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

    /**
     Computes all supertypes(including superclasses, interfaces, etc) of the specified class.

     @param current
     the current
     @param set
     the set

     @return the set
     */
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

    /**
     Returns an list containing all the methods of the specified class.

     @param cls
     the cls

     @return the methods recursively

     @throws SecurityException
     if a security manager denied access
     */
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

    /**
     Returns the default {@linkplain Lookup lookup object}.

     @return the lookup
     */
    static Lookup defaultLookup() {
        return DEFAULT_LOOKUP;
    }

    /**
     Checks if the specified {@linkplain Lookup lookup object} is the
     {@linkplain #defaultLookup() default lookup object}.

     @param lookup
     the lookup

     @return {@code true} if the {@code lookup} parameter is the same object         as {@linkplain #defaultLookup() default lookup object}, {@code false} otherwise

     @see #defaultLookup() #defaultLookup()
     */
    static boolean isDefaultLookup(Lookup lookup) {
        return lookup == DEFAULT_LOOKUP;
    }

    private AccessHelper() {}
}
