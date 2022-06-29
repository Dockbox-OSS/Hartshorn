package org.dockbox.hartshorn.hsl.callable.module;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.modules.MathLibrary;
import org.dockbox.hartshorn.hsl.modules.SystemLibrary;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard libraries for HSL runtimes. These libraries can be loaded by the {@link StandardRuntime},
 * making them accessible to the {@link org.dockbox.hartshorn.hsl.interpreter.Interpreter}, but are
 * not guaranteed to be.
 *
 * <p>The libraries in this class are sorted alphabetically by name, and should never contain duplicate
 * names.
 *
 * @author Guus Lieben
 * @since 22.4
 */
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

    /**
     * Get the name of this library. This is the name used to load the library when
     * using {@link org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement}s.
     * @return The name of this library.
     */
    public String libaryName() {
        return this.name;
    }

    /**
     * Get the class representing this library. This is typically only used to create
     * a new {@link NativeModule}.
     * @return The class of this library.
     */
    public Class<?> libraryClass() {
        return this.libraryClass;
    }

    /**
     * Get the {@link NativeModule} instance for this library. This is lazily loaded, and only created
     * when it is requested.
     * @param context The application context.
     * @return The {@link NativeModule} instance for this library.
     */
    public NativeModule asModule(final ApplicationContext context) {
        return new ApplicationBoundNativeModule(this.libraryClass, context);
    }

    /**
     * Get the {@link NativeModule} instances for all libraries. This is lazily loaded, and only created
     * when it is requested.
     * @param context The application context.
     * @return The {@link NativeModule} instances for all libraries.
     */
    public static Map<String, NativeModule> asModules(final ApplicationContext context) {
        final Map<String, NativeModule> modules = new ConcurrentHashMap<>();
        for (final StandardLibrary library : StandardLibrary.values()) {
            modules.put(library.libaryName(), library.asModule(context));
        }
        return modules;
    }
}
