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

package org.dockbox.hartshorn.hsl.condition;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ConditionContext extends Context {

    boolean includeApplicationContext();

    ConditionContext includeApplicationContext(final boolean includeApplicationContext);

    /**
     * Add all given customizers to the context. This will not override any existing customizers.
     * @param customizers The customizers to add.
     */
    void customizers(final Collection<CodeCustomizer> customizers);

    /**
     * Adds the given customizer to the context.
     * @param customizer The customizer to add.
     */
    void customizer(final CodeCustomizer customizer);

    /**
     * Adds the given module to the context under the given alias. This will override
     * existing modules if the alias already exists in the context.
     * @param name The alias to use for the module.
     * @param module The module to add.
     */
    void module(final String name, final NativeModule module);

    /**
     * Adds the given modules to the context under the given aliases. This will override
     * existing modules if the alias already exists in the context.
     * @param modules The modules to add, identified by their alias.
     */
    void modules(final Map<String, NativeModule> modules);

    /**
     * Adds the variable as a global variable under the given alias. This will override
     * existing variables if the alias already exists in the context.
     * @param name The alias to use for the variable.
     * @param value The variable value to add.
     */
    void global(final String name, final Object value);

    /**
     * Adds the given variables to the context under the given aliases. This will override
     * existing variables if the alias already exists in the context.
     * @param values The variables to add, identified by their alias.
     */
    void global(final Map<String, Object> values);

    /**
     * Adds the given class as an import under the given aliases to the context. This allows
     * it to be used in the executing runtime. This will override existing imports if the
     * alias already exists in the context.
     * @param name The alias to use for the import.
     * @param type The class to import.
     */
    void imports(final String name, final Class<?> type);

    void imports(final String name, final TypeView<?> type);

    /**
     * Adds the given class as an import to the context. The class will be made available using
     * its simple name (e.g. {@code org.example.User} will be accessible using {@code User}). This
     * will override existing imports if there is another import with the same name or alias.
     *
     * @param type The class to import.
     */
    void imports(final Class<?> type);

    void imports(final TypeView<?> type);

    /**
     * Adds the given imports to the context under the given aliases. This will override existing
     * imports if there is another import with the same name or alias.
     * @param imports The classes to import, identified by their alias.
     */
    void imports(final Map<String, TypeView<?>> imports);

    /**
     * Gets all global variables stored in this context, identified by their alias.
     * @return The global variables.
     */
    Map<String, Object> globalVariables();

    /**
     * Gets all imports stored in this context, identified by their alias.
     * @return The imports.
     */
    Map<String, TypeView<?>> imports();

    /**
     * Gets all customizers stored in this context.
     * @return The customizers.
     */
    Set<CodeCustomizer> customizers();

    /**
     * Gets all modules stored in this context, identified by their alias.
     * @return The modules.
     */
    Map<String, NativeModule> externalModules();

    void interpreterOptions(ExecutionOptions executionOptions);

    ExecutionOptions interpreterOptions();
}
