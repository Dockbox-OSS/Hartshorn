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

package org.dockbox.hartshorn.inject.graph;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.QualifierKey;
import org.dockbox.hartshorn.inject.binding.AliasBindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.graph.declaration.AliasableDependencyContext;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;

import java.util.HashSet;
import java.util.Set;

/**
 * A configurable dependency context that supports aliasing.
 *
 * @param <T> the type of the dependency
 *
 * @see AliasableDependencyContext
 * @see ConfigurableDependencyContext
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class AliasableConfigurableDependencyContext<T> extends ConfigurableDependencyContext<T> implements AliasableDependencyContext<T> {

    private final Set<Class<? super T>> aliasTypes;
    private final Set<ComponentKey<? super T>> aliasKeys;
    private final Set<QualifierKey<T>> aliasQualifiers;

    protected AliasableConfigurableDependencyContext(AliasableConfigurableDependencyContextBuilder<T> builder) {
        super(builder);
        this.aliasTypes = Set.copyOf(builder.aliasTypes);
        this.aliasKeys = Set.copyOf(builder.aliasKeys);
        this.aliasQualifiers = Set.copyOf(builder.aliasQualifiers);
    }

    /**
     * Creates a new builder for a {@link AliasableConfigurableDependencyContext}.
     *
     * @param componentKey the component key of the dependency
     * @param <T> the type of the dependency
     *
     * @return the builder
     */
    public static <T> AliasableConfigurableDependencyContextBuilder<T> builder(ComponentKey<T> componentKey) {
        return new AliasableConfigurableDependencyContextBuilder<>(componentKey);
    }


    @Override
    public void configure(BindingFunction<T> function) throws ComponentConfigurationException {
        super.configure(function);
        if (function instanceof AliasBindingFunction<T> aliasBindingFunction) {
            this.aliasTypes.forEach(aliasBindingFunction::alias);
            this.aliasKeys.forEach(aliasBindingFunction::alias);
            this.aliasQualifiers.forEach(aliasBindingFunction::alias);
        }
        else if (!this.aliasTypes.isEmpty() || !this.aliasKeys.isEmpty() || !this.aliasQualifiers.isEmpty()) {
            throw new ComponentConfigurationException("Attempted to configure aliases on a binding that does not support aliasing");
        }
    }

    @Override
    public Set<Class<? super T>> aliasTypes() {
        return Set.copyOf(this.aliasTypes);
    }

    @Override
    public Set<ComponentKey<? super T>> aliasKeys() {
        return Set.copyOf(this.aliasKeys);
    }

    @Override
    public Set<QualifierKey<T>> aliasQualifiers() {
        return Set.copyOf(this.aliasQualifiers);
    }

    @Override
    protected void customizeDescriber(ObjectDescriber<?> describer) {
        super.customizeDescriber(describer);
        describer.field("aliasTypes", this.aliasTypes);
        describer.field("aliasKeys", this.aliasKeys);
        describer.field("aliasQualifiers", this.aliasQualifiers);
    }

    /**
     * A builder for {@link AliasableConfigurableDependencyContext} instances.
     *
     * @param <T> the type of the component that is auto-configured
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public static class AliasableConfigurableDependencyContextBuilder<T> extends AutoConfiguringDependencyContextBuilder<T> {

        private final Set<Class<? super T>> aliasTypes = new HashSet<>();
        private final Set<ComponentKey<? super T>> aliasKeys = new HashSet<>();
        private final Set<QualifierKey<T>> aliasQualifiers = new HashSet<>();

        protected AliasableConfigurableDependencyContextBuilder(ComponentKey<T> componentKey) {
            super(componentKey);
        }

        /**
         * Adds a single alias type.
         *
         * @param aliasType the alias type to add
         * @return the builder instance
         */
        public AliasableConfigurableDependencyContextBuilder<T> aliasType(Class<? super T> aliasType) {
            this.aliasTypes.add(aliasType);
            return this;
        }

        /**
         * Adds multiple alias types.
         *
         * @param aliasTypes the alias types to add
         * @return the builder instance
         */
        public AliasableConfigurableDependencyContextBuilder<T> aliasTypes(Set<Class<? super T>> aliasTypes) {
            this.aliasTypes.addAll(aliasTypes);
            return this;
        }

        /**
         * Adds a single alias key.
         *
         * @param aliasKey the alias key to add
         * @return the builder instance
         */
        public AliasableConfigurableDependencyContextBuilder<T> aliasKey(ComponentKey<? super T> aliasKey) {
            this.aliasKeys.add(aliasKey);
            return this;
        }

        /**
         * Adds multiple alias keys.
         *
         * @param aliasKeys the alias keys to add
         * @return the builder instance
         */
        public AliasableConfigurableDependencyContextBuilder<T> aliasKeys(Set<ComponentKey<? super T>> aliasKeys) {
            this.aliasKeys.addAll(aliasKeys);
            return this;
        }

        /**
         * Adds a single alias qualifier.
         *
         * @param aliasQualifier the alias qualifier to add
         * @return the builder instance
         */
        public AliasableConfigurableDependencyContextBuilder<T> aliasQualifier(QualifierKey<T> aliasQualifier) {
            this.aliasQualifiers.add(aliasQualifier);
            return this;
        }

        /**
         * Adds multiple alias qualifiers.
         *
         * @param aliasQualifiers the alias qualifiers to add
         * @return the builder instance
         */
        public AliasableConfigurableDependencyContextBuilder<T> aliasQualifiers(Set<QualifierKey<T>> aliasQualifiers) {
            this.aliasQualifiers.addAll(aliasQualifiers);
            return this;
        }

        @Override
        public ConfigurableDependencyContext<T> build() {
            return new AliasableConfigurableDependencyContext<>(this);
        }
    }
}
