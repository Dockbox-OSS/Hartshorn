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

package org.dockbox.hartshorn.hsl.condition;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard context providing configurable collections containing all global variables, imports,
 * customizers, and external modules for an HSL runtime. This is most commonly used by the
 * {@link ExpressionCondition} to allow for customization of the runtime.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ExpressionConditionContext extends DefaultProvisionContext implements ConditionContext {

    private final Map<String, Object> globalVariables = new ConcurrentHashMap<>();
    private final Map<String, TypeView<?>> imports = new ConcurrentHashMap<>();
    private final Set<CodeCustomizer> customizers = ConcurrentHashMap.newKeySet();
    private final Map<String, NativeModule> externalModules = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;

    private ExecutionOptions executionOptions = new ExecutionOptions();
    private boolean includeApplicationContext;

    public ExpressionConditionContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean includeApplicationContext() {
        return this.includeApplicationContext;
    }

    @Override
    public ExpressionConditionContext includeApplicationContext(boolean includeApplicationContext) {
        this.includeApplicationContext = includeApplicationContext;
        return this;
    }

    /**
     * Add all given customizers to the context. This will not override any existing customizers.
     * @param customizers The customizers to add.
     */
    @Override
    public void customizers(Collection<CodeCustomizer> customizers) {
        this.customizers.addAll(customizers);
    }

    /**
     * Adds the given customizer to the context.
     * @param customizer The customizer to add.
     */
    @Override
    public void customizer(CodeCustomizer customizer) {
        this.customizers.add(customizer);
    }

    /**
     * Adds the given module to the context under the given alias. This will override
     * existing modules if the alias already exists in the context.
     * @param name The alias to use for the module.
     * @param module The module to add.
     */
    @Override
    public void module(String name, NativeModule module) {
        this.externalModules.put(name, module);
    }

    /**
     * Adds the given modules to the context under the given aliases. This will override
     * existing modules if the alias already exists in the context.
     * @param modules The modules to add, identified by their alias.
     */
    @Override
    public void modules(Map<String, NativeModule> modules) {
        this.externalModules.putAll(modules);
    }

    /**
     * Adds the variable as a global variable under the given alias. This will override
     * existing variables if the alias already exists in the context.
     * @param name The alias to use for the variable.
     * @param value The variable value to add.
     */
    @Override
    public void global(String name, Object value) {
        this.globalVariables.put(name, value);
    }

    /**
     * Adds the given variables to the context under the given aliases. This will override
     * existing variables if the alias already exists in the context.
     * @param values The variables to add, identified by their alias.
     */
    @Override
    public void global(Map<String, Object> values) {
        this.globalVariables.putAll(values);
    }

    /**
     * Adds the given class as an import under the given aliases to the context. This allows
     * it to be used in the executing runtime. This will override existing imports if the
     * alias already exists in the context.
     * @param name The alias to use for the import.
     * @param type The class to import.
     */
    @Override
    public void imports(String name, Class<?> type) {
        this.imports.put(name, this.applicationContext.environment().introspector().introspect(type));
    }

    @Override
    public void imports(String name, TypeView<?> type) {
        this.imports.put(name, type);
    }

    /**
     * Adds the given class as an import to the context. The class will be made available using
     * its simple name (e.g. {@code org.example.User} will be accessible using {@code User}). This
     * will override existing imports if there is another import with the same name or alias.
     *
     * @param type The class to import.
     */
    @Override
    public void imports(Class<?> type) {
        this.imports(type.getSimpleName(), type);
    }

    @Override
    public void imports(TypeView<?> type) {
        this.imports(type.name(), type);
    }

    /**
     * Adds the given imports to the context under the given aliases. This will override existing
     * imports if there is another import with the same name or alias.
     * @param imports The classes to import, identified by their alias.
     */
    @Override
    public void imports(Map<String, TypeView<?>> imports) {
        this.imports.putAll(imports);
    }

    /**
     * Gets all global variables stored in this context, identified by their alias.
     * @return The global variables.
     */
    @Override
    public Map<String, Object> globalVariables() {
        return this.globalVariables;
    }

    /**
     * Gets all imports stored in this context, identified by their alias.
     * @return The imports.
     */
    @Override
    public Map<String, TypeView<?>> imports() {
        return this.imports;
    }

    /**
     * Gets all customizers stored in this context.
     * @return The customizers.
     */
    @Override
    public Set<CodeCustomizer> customizers() {
        return this.customizers;
    }

    /**
     * Gets all modules stored in this context, identified by their alias.
     * @return The modules.
     */
    @Override
    public Map<String, NativeModule> externalModules() {
        return this.externalModules;
    }

    /**
     * Gets the interpreter options for this context, overriding any existing settings.
     * @param executionOptions The interpreter options.
     */
    @Override
    public void interpreterOptions(ExecutionOptions executionOptions) {
        this.executionOptions = executionOptions;
    }

    /**
     * Gets the interpreter options for this context.
     * @return The interpreter options.
     */
    @Override
    public ExecutionOptions interpreterOptions() {
        return this.executionOptions;
    }
}
