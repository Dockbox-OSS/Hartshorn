/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.interpreter.SimpleVisitorInterpreter;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Standard libraries for HSL runtimes. These libraries can be loaded by the {@link ScriptRuntime},
 * making them accessible to the {@link SimpleVisitorInterpreter}, but are
 * not guaranteed to be.
 *
 * <p>The libraries in this class are sorted alphabetically by name, and should never contain duplicate
 * names.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public enum StandardUtilitiesLibrary {
    MATH("math", context -> {
        return new UtilityClassNativeModule(Math.class, context.applicationContext());
    }),
    SYSTEM("system", context -> {
        return new InstanceNativeModule(context.applicationContext(), new SystemLibrary(context));
    }),
    ;

    private final String name;
    private final Function<ScriptContext, NativeModule> moduleProvider;

    StandardUtilitiesLibrary(String name, Function<ScriptContext, NativeModule> moduleProvider) {
        this.name = name;
        this.moduleProvider = moduleProvider;
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
     * Get the {@link NativeModule} instance for this library. This is lazily loaded, and only created
     * when it is requested.
     * @param context The application context.
     * @return The {@link NativeModule} instance for this library.
     */
    public NativeModule asModule(ScriptContext context) {
        return this.moduleProvider.apply(context);
    }

    /**
     * Get the {@link NativeModule} instances for all libraries. This is lazily loaded, and only created
     * when it is requested.
     * @param context The application context.
     * @return The {@link NativeModule} instances for all libraries.
     */
    public static Map<String, NativeModule> asModules(ScriptContext context) {
        Map<String, NativeModule> modules = new ConcurrentHashMap<>();
        for (StandardUtilitiesLibrary library : StandardUtilitiesLibrary.values()) {
            modules.put(library.libaryName(), library.asModule(context));
        }
        return modules;
    }
}
