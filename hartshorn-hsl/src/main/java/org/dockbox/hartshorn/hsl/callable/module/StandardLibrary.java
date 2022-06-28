package org.dockbox.hartshorn.hsl.callable.module;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.modules.MathLibrary;
import org.dockbox.hartshorn.hsl.modules.SystemLibrary;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum StandardLibrary {
    MATH("math", MathLibrary.class),
    SYSTEM("system", SystemLibrary.class),
    ;

    private final String name;
    private final Class<?> libraryClass;

    StandardLibrary(final String name, final Class<?> libraryClass) {
        this.name = name;
        this.libraryClass = libraryClass;
    }

    public String libaryName() {
        return this.name;
    }

    public Class<?> libraryClass() {
        return this.libraryClass;
    }

    public NativeModule asModule(final ApplicationContext context) {
        return new ApplicationBoundNativeModule(this.libraryClass, context);
    }

    public static Map<String, NativeModule> asModules(final ApplicationContext context) {
        final Map<String, NativeModule> modules = new ConcurrentHashMap<>();
        for (final StandardLibrary library : StandardLibrary.values()) {
            modules.put(library.libaryName(), library.asModule(context));
        }
        return modules;
    }
}
