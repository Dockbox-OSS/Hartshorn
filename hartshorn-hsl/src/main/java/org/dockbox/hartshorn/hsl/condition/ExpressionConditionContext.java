package org.dockbox.hartshorn.hsl.condition;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.hsl.callable.module.NativeModule;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard context providing configurable collections containing all global variables, imports,
 * customizers, and external modules for an HSL runtime. This is most commonly used by the
 * {@link ExpressionCondition} to allow for customization of the runtime.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class ExpressionConditionContext extends DefaultContext {

    private final Map<String, Object> globalVariables = new ConcurrentHashMap<>();
    private final Map<String, Class<?>> imports = new ConcurrentHashMap<>();
    private final Set<CodeCustomizer> customizers = ConcurrentHashMap.newKeySet();
    private final Map<String, NativeModule> externalModules = new ConcurrentHashMap<>();

    private boolean includeApplicationContext;

    public boolean includeApplicationContext() {
        return this.includeApplicationContext;
    }

    public ExpressionConditionContext includeApplicationContext(final boolean includeApplicationContext) {
        this.includeApplicationContext = includeApplicationContext;
        return this;
    }

    /**
     * Add all given customizers to the context. This will not override any existing customizers.
     * @param customizers The customizers to add.
     */
    public void customizers(final Collection<CodeCustomizer> customizers) {
        this.customizers.addAll(customizers);
    }

    /**
     * Adds the given customizer to the context.
     * @param customizer The customizer to add.
     */
    public void customizer(final CodeCustomizer customizer) {
        this.customizers.add(customizer);
    }

    /**
     * Adds the given module to the context under the given alias. This will override
     * existing modules if the alias already exists in the context.
     * @param name The alias to use for the module.
     * @param module The module to add.
     */
    public void module(final String name, final NativeModule module) {
        this.externalModules.put(name, module);
    }

    /**
     * Adds the given modules to the context under the given aliases. This will override
     * existing modules if the alias already exists in the context.
     * @param modules The modules to add, identified by their alias.
     */
    public void modules(final Map<String, NativeModule> modules) {
        this.externalModules.putAll(modules);
    }

    /**
     * Adds the variable as a global variable under the given alias. This will override
     * existing variables if the alias already exists in the context.
     * @param name The alias to use for the variable.
     * @param value The variable value to add.
     */
    public void global(final String name, final Object value) {
        this.globalVariables.put(name, value);
    }

    /**
     * Adds the given variables to the context under the given aliases. This will override
     * existing variables if the alias already exists in the context.
     * @param values The variables to add, identified by their alias.
     */
    public void global(final Map<String, Object> values) {
        this.globalVariables.putAll(values);
    }

    /**
     * Adds the given class as an import under the given aliases to the context. This allows
     * it to be used in the executing runtime. This will override existing imports if the
     * alias already exists in the context.
     * @param name The alias to use for the import.
     * @param type The class to import.
     */
    public void imports(final String name, final Class<?> type) {
        this.imports.put(name, type);
    }

    /**
     * Adds the given class as an import to the context. The class will be made available using
     * its simple name (e.g. {@code org.example.User} will be accessible using {@code User}). This
     * will override existing imports if there is another import with the same name or alias.
     * @param type
     */
    public void imports(final Class<?> type) {
        this.imports(type.getSimpleName(), type);
    }

    /**
     * Adds the given imports to the context under the given aliases. This will override existing
     * imports if there is another import with the same name or alias.
     * @param imports The classes to import, identified by their alias.
     */
    public void imports(final Map<String, Class<?>> imports) {
        this.imports.putAll(imports);
    }

    /**
     * Gets all global variables stored in this context, identified by their alias.
     * @return The global variables.
     */
    public Map<String, Object> globalVariables() {
        return this.globalVariables;
    }

    /**
     * Gets all imports stored in this context, identified by their alias.
     * @return The imports.
     */
    public Map<String, Class<?>> imports() {
        return this.imports;
    }

    /**
     * Gets all customizers stored in this context.
     * @return The customizers.
     */
    public Set<CodeCustomizer> customizers() {
        return this.customizers;
    }

    /**
     * Gets all modules stored in this context, identified by their alias.
     * @return The modules.
     */
    public Map<String, NativeModule> externalModules() {
        return this.externalModules;
    }
}
