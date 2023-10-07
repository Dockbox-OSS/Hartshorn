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

package org.dockbox.hartshorn.hsl.modules;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.interpreter.SimpleVisitorInterpreter;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard libraries for HSL runtimes. These libraries can be loaded by the {@link ScriptRuntime},
 * making them accessible to the {@link SimpleVisitorInterpreter}, but are
 * not guaranteed to be.
 *
 * <p>The libraries in this class are sorted alphabetically by name, and should never contain duplicate
 * names.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public enum StandardLibrary {
    MATH("math", MathLibrary.class),
    SYSTEM("system", SystemLibrary.class),
    ;

    private final String name;
    private final Class<?> libraryClass;

    StandardLibrary(String name, Class<?> libraryClass) {
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
    public NativeModule asModule(ApplicationContext context) {
        return new ApplicationBoundNativeModule(this.libraryClass, context);
    }

    /**
     * Get the {@link NativeModule} instances for all libraries. This is lazily loaded, and only created
     * when it is requested.
     * @param context The application context.
     * @return The {@link NativeModule} instances for all libraries.
     */
    public static Map<String, NativeModule> asModules(ApplicationContext context) {
        Map<String, NativeModule> modules = new ConcurrentHashMap<>();
        for (StandardLibrary library : StandardLibrary.values()) {
            modules.put(library.libaryName(), library.asModule(context));
        }
        return modules;
    }
}
