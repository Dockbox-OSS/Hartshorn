package org.dockbox.hartshorn.hsl.condition;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.hsl.callable.module.NativeModule;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    public void customizers(final Collection<CodeCustomizer> customizers) {
        this.customizers.addAll(customizers);
    }

    public void customizer(final CodeCustomizer customizer) {
        this.customizers.add(customizer);
    }

    public void module(final String name, final NativeModule module) {
        this.externalModules.put(name, module);
    }

    public void modules(final Map<String, NativeModule> modules) {
        this.externalModules.putAll(modules);
    }

    public void global(final String name, final Object value) {
        this.globalVariables.put(name, value);
    }

    public void global(final Map<String, Object> values) {
        this.globalVariables.putAll(values);
    }

    public void imports(final String name, final Class<?> type) {
        this.imports.put(name, type);
    }

    public void imports(final Class<?> type) {
        this.imports(type.getSimpleName(), type);
    }

    public void imports(final Map<String, Class<?>> imports) {
        this.imports.putAll(imports);
    }

    public Map<String, Object> globalVariables() {
        return this.globalVariables;
    }

    public Map<String, Class<?>> imports() {
        return this.imports;
    }

    public Set<CodeCustomizer> customizers() {
        return this.customizers;
    }

    public Map<String, NativeModule> externalModules() {
        return this.externalModules;
    }
}
