/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.inject.annotations.OnInitialized;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.provider.failure.ComponentResolutionFailureStrategy;
import org.dockbox.hartshorn.inject.provider.failure.ExceptionOnComponentResolutionFailureStrategy;
import org.dockbox.hartshorn.inject.provider.failure.NoopComponentResolutionFailureStrategy;
import org.dockbox.hartshorn.inject.provider.selection.HighestPriorityProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.provider.selection.ProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.util.Objects;
import java.util.Set;

/**
 * A key that can be used to identify a component. This contains required metadata to identify a
 * component, such as its type, name, scope and whether post-construction is allowed on
 * provisioning.
 *
 * <p>Component keys contain a {@link ParameterizableType} that describes the type of the component.
 * This type can
 * be parameterized. Therefore, key instances differentiate between e.g. {@code List<String>} and
 * {@code List<Integer>}.
 *
 * <p>Keys are immutable, to build a new key based on an existing key, use {@link #mutable()}.
 *
 * @param <T> the type of the component
 *
 * @see ComponentProvider#get(ComponentKey)
 * @see ComponentProvider#get(ComponentKey, ComponentRequestContext)
 * @see ComponentKey#builder(Class)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public final class ComponentKey<T> implements Reportable {

    private final ProviderSelectionStrategy selectionStrategy;
    private final ComponentResolutionFailureStrategy failureStrategy;
    private final ParameterizableType type;
    private final CompositeQualifier qualifier;
    private final Scope scope;
    private final boolean postConstructionAllowed;
    private final Tristate strict;

    private ComponentKey(
        ProviderSelectionStrategy selectionStrategy,
        ComponentResolutionFailureStrategy failureStrategy,
        ParameterizableType type,
        CompositeQualifier qualifier,
        Scope scope,
        boolean postConstructionAllowed,
        Tristate strict
    ) {
        this.selectionStrategy = selectionStrategy;
        this.failureStrategy = failureStrategy;
        this.type = type;
        this.qualifier = qualifier;
        this.scope = scope;
        this.postConstructionAllowed = postConstructionAllowed;
        this.strict = strict;
    }

    /**
     * Creates a new builder for a component key of the given type. If the type is parameterized,
     * the key will be for the raw type.
     *
     * @param type the type of the component
     * @param <T> the type of the component
     *
     * @return a new builder
     */
    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>(ParameterizableType.create(type));
    }

    /**
     * Creates a new builder for a component key of the given type. If the type is parameterized,
     * the key will retain its parameterization.
     *
     * @param type the type of the component
     * @param <T> the type of the component
     *
     * @return a new builder
     */
    public static <T> Builder<T> builder(TypeView<T> type) {
        return new Builder<>(ParameterizableType.create(type));
    }

    /**
     * Creates a new builder for a component key of the given type. If the type is parameterized,
     * the key will retain its parameterization.
     *
     * @param type the type of the component
     *
     * @return a new builder
     */
    public static Builder<?> builder(ParameterizableType type) {
        return new Builder<>(type);
    }

    /**
     * Creates a new collector component key of the given type. If the type is parameterized, the
     * key will be for the raw type. The collector key is used to collect all components of the
     * given type.
     *
     * @param type the type of the component
     * @param <T> the type of the component
     *
     * @return a new component key
     */
    public static <T> ComponentKey<ComponentCollection<T>> collect(Class<T> type) {
        return TypeUtils.unchecked(collect(ParameterizableType.create(type)), ComponentKey.class);
    }

    /**
     * Creates a new collector component key of the given type. If the type is parameterized, the
     * key will retain its parameterization. The collector key is used to collect all components of
     * the given type.
     *
     * @param type the type of the component
     *
     * @return a new component key
     */
    public static ComponentKey<ComponentCollection<?>> collect(ParameterizableType type) {
        ParameterizableType collectionType = ParameterizableType.builder(ComponentCollection.class)
            .parameters(type)
            .build();
        return TypeUtils.unchecked(ComponentKey.of(collectionType), ComponentKey.class);
    }

    /**
     * Creates a new component key of the given type. If the type is parameterized, the key will be
     * for the raw type.
     *
     * @param type the type of the component
     * @param <T> the type of the component
     *
     * @return a new component key
     */
    public static <T> ComponentKey<T> of(Class<T> type) {
        return ComponentKey.builder(type).build();
    }

    /**
     * Creates a new component key of the given type. If the type is parameterized, the key will
     * retain its parameterization.
     *
     * @param type the type of the component
     * @param <T> the type of the component
     *
     * @return a new component key
     */
    public static <T> ComponentKey<T> of(TypeView<T> type) {
        return ComponentKey.builder(type).build();
    }

    /**
     * Creates a new component key of the given type. If the type is parameterized, the key will
     * retain its parameterization.
     *
     * @param type the type of the component
     *
     * @return a new component key
     */
    public static ComponentKey<?> of(ParameterizableType type) {
        return ComponentKey.builder(type).build();
    }

    /**
     * Creates a new named component key of the given type. If the type is parameterized, the key
     * will be for the raw type.
     *
     * @param key the type of the component
     * @param name the name of the component
     * @param <T> the type of the component
     *
     * @return a new component key
     */
    public static <T> ComponentKey<T> of(Class<T> key, String name) {
        Builder<T> builder = ComponentKey.builder(key);
        if (StringUtilities.notEmpty(name)) {
            return builder.name(name).build();
        }
        return builder.build();
    }

    /**
     * Creates a new named component key of the given type. If the type is parameterized, the key
     * will retain its parameterization.
     *
     * @param type the type of the component
     * @param named the name of the component
     * @param <T> the type of the component
     *
     * @return a new component key
     */
    public static <T> ComponentKey<T> of(TypeView<T> type, String named) {
        return ComponentKey.of(type.type(), named);
    }

    /**
     * Creates a new key builder based on this key. The builder will have the same type, name, scope
     * and post-construction values as this key. The builder can be used to create a new key with
     * different values.
     *
     * @return a new builder
     */
    public Builder<T> mutable() {
        return new Builder<>(this);
    }

    /**
     * Creates a new view of this key. The view will have the same type and qualifiers as this key.
     * Views are not attached to a scope, and do not indicate whether post-construction callbacks
     * are allowed. This method is useful for comparing keys, or for use in maps.
     *
     * <p>Views always retain the parameterization of the key.
     *
     * @return a new view
     */
    public ComponentKeyView<T> view() {
        return new ComponentKeyView<>(this);
    }

    /**
     * Returns the qualified name of this key. The qualified name is the name of the type, followed
     * by the name of the component, followed by the name of the scope. If the component has no
     * name, the name is omitted. If the component has no explicit scope, the default scope is the
     * application scope of the component provider.
     *
     * @return the qualified name
     */
    public String qualifiedName() {
        String qualifier =
            StringUtilities.join(", ", this.qualifier.qualifiers(), QualifierKey::toString);
        String qualifierSuffix = StringUtilities.empty(qualifier) ? "" : ":" + qualifier;
        String scopeName = this.scope()
            .map(Scope::installableScopeType)
            .map(ScopeKey::name)
            .map(scope -> " @ " + scope)
            .orElse("");
        return "%s%s%s".formatted(this.type.toQualifiedString(), qualifierSuffix, scopeName);
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
            .field("type", this.type)
            .field("qualifier", this.qualifier)
            .field("scope", this.scope)
            .field("postConstructionAllowed", this.postConstructionAllowed)
            .field("strict", this.strict)
            .field("selectionStrategy", this.selectionStrategy)
            .field("failureStrategy", this.failureStrategy)
            .describe();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ComponentKey<?> otherKey)) {
            return false;
        }
        return SimpleComponentKeyMatcher.StrictComponentKeyMatcher.INSTANCE.matches(this, otherKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.qualifier, this.scope, this.postConstructionAllowed);
    }

    /**
     * Returns the raw type of the component, excluding any type parameters.
     *
     * @return the raw type of the component
     */
    public Class<T> type() {
        return TypeUtils.unchecked(this.type.type(), Class.class);
    }

    /**
     * Returns the parameterized type of the component, including any type parameters.
     *
     * @return the parameterized type of the component
     */
    public ParameterizableType parameterizedType() {
        return this.type;
    }

    /**
     * Returns the qualifier of the component. If the component has no explicit qualifier, an empty
     * qualifier is returned.
     *
     * @return the qualifier of the component
     */
    public CompositeQualifier qualifier() {
        return this.qualifier;
    }

    /**
     * Returns the scope of the component. If the component has no explicit scope, the default scope
     * of component provider should be used.
     *
     * @return the scope of the component
     */
    public Option<Scope> scope() {
        return Option.of(this.scope);
    }

    /**
     * Returns whether {@link OnInitialized} callbacks of the component should be activated after
     * initialization. If the key did not explicitly set this value, {@code true} is returned.
     *
     * @return whether post-construction should be activated on provisioning
     */
    public boolean postConstructionAllowed() {
        return this.postConstructionAllowed;
    }

    /**
     * Returns whether the lookup for this component should be strict. If the lookup is strict, the
     * type of the hierarchy has to match this key exactly. If the lookup is not strict, the type of
     * the hierarchy can be a sub-type of this key.
     *
     * <p>If strict-mode is not explicitly set, {@link Tristate#UNDEFINED} is returned. In this case
     * it remains
     * up to the component provider to decide whether strict-mode should be applied.
     *
     * @return whether the lookup for this component should be strict
     */
    public Tristate strict() {
        return this.strict;
    }

    /**
     * Returns the strategy that should be used to select a provider for this component. This often
     * selects a provider based on the priority of the key.
     *
     * @return the strategy that should be used to select a provider for this component
     */
    public ProviderSelectionStrategy selectionStrategy() {
        return this.selectionStrategy;
    }

    /**
     * Returns the strategy that should be used to handle the scenario where no component is found
     * for this key.
     *
     * @return the strategy that should be used to handle component resolution failures
     */
    public ComponentResolutionFailureStrategy failureStrategy() {
        return this.failureStrategy;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("type").writeDelegate(this.type);
        collector.property("qualifier").writeDelegate(this.qualifier);
        if (this.scope != null) {
            collector.property("scope").writeDelegate(this.scope.installableScopeType());
        }
        collector.property("postConstructionAllowed").writeBoolean(this.postConstructionAllowed);
    }

    /**
     * A builder for {@link ComponentKey}s. The builder can be used to create a new key based on an
     * existing key, or to create a new key from scratch.
     *
     * @param <T> the type of the component
     *
     * @see ComponentKey
     * @see ComponentKey#builder(Class)
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static final class Builder<T> {

        private final ParameterizableType type;
        private final CompositeQualifier qualifier = new CompositeQualifier();
        private ProviderSelectionStrategy selectionStrategy =
            HighestPriorityProviderSelectionStrategy.INSTANCE;
        private ComponentResolutionFailureStrategy failureStrategy =
            ExceptionOnComponentResolutionFailureStrategy.INSTANCE;
        // If not provided, defaults to application scope
        private Scope scope = null;
        private boolean postConstructionAllowed = true;
        // If not provided, defaults to InjectorConfiguration#isStrictMode
        private Tristate strict = Tristate.UNDEFINED;

        private Builder(ComponentKey<T> key) {
            this.type = key.type;
            this.qualifier.addAll(key.qualifier);
            this.selectionStrategy = key.selectionStrategy;
            this.failureStrategy = key.failureStrategy;
            this.scope = key.scope;
            this.postConstructionAllowed = key.postConstructionAllowed;
            this.strict = key.strict;
        }

        private Builder(ParameterizableType type) {
            this.type = type;
        }

        private <U> Builder<U> copyProperties(Builder<U> builder) {
            builder.qualifiers(this.qualifier.qualifiers())
                .selectionStrategy(this.selectionStrategy)
                .failureStrategy(this.failureStrategy)
                .scope(this.scope)
                .postConstructionAllowed(this.postConstructionAllowed);
            if (this.strict != Tristate.UNDEFINED) {
                builder.strict(this.strict.booleanValue());
            }
            return builder;
        }

        /**
         * Sets the type of the component. If the type is parameterized, the key will be for the raw
         * type.
         *
         * @param type the type of the component
         * @param <U> the type of the component
         *
         * @return a new builder
         */
        public <U> Builder<U> type(Class<U> type) {
            return this.copyProperties(builder(type));
        }

        /**
         * Sets the type of the component. If the type is parameterized, the key will retain its
         * parameterization.
         *
         * @param type the type of the component
         * @param <U> the type of the component
         *
         * @return a new builder
         */
        public <U> Builder<U> type(TypeView<U> type) {
            return this.copyProperties(builder(type));
        }

        /**
         * Sets the type of the component. If the type is parameterized, the key will retain its
         * parameterization.
         *
         * @param type the type of the component
         *
         * @return a new builder
         */
        public Builder<?> type(ParameterizableType type) {
            return this.copyProperties(builder(type));
        }

        /**
         * Sets the strategy that should be used to select a provider for this component. This often
         * selects a provider based on the priority of the key.
         *
         * @param strategy the strategy that should be used to select a provider for this component
         *
         * @return this builder
         */
        public Builder<T> selectionStrategy(ProviderSelectionStrategy strategy) {
            this.selectionStrategy = strategy;
            return this;
        }

        /**
         * Sets the strategy that should be used to handle the scenario where no component is found
         * for this key. The default strategy is
         * {@link ExceptionOnComponentResolutionFailureStrategy}.
         *
         * @param failureStrategy the strategy that should be used to handle component resolution
         * failures
         *
         * @return this builder
         */
        public Builder<T> failureStrategy(ComponentResolutionFailureStrategy failureStrategy) {
            this.failureStrategy = failureStrategy;
            return this;
        }

        /**
         * Sets the {@link #failureStrategy(ComponentResolutionFailureStrategy) failure strategy} to
         * ignore lookup failures, thus making it possible to result in {@code null} values from a
         * lookup.
         *
         * @return this builder
         */
        public Builder<T> optional() {
            return this.failureStrategy(NoopComponentResolutionFailureStrategy.INSTANCE);
        }

        /**
         * Sets the {@link #failureStrategy(ComponentResolutionFailureStrategy) failure strategy} to
         * throw an exception when a lookup fails, thus ensuring the result is always non-null.
         *
         * @return this builder
         */
        public Builder<T> required() {
            return this.failureStrategy(ExceptionOnComponentResolutionFailureStrategy.INSTANCE);
        }

        /**
         * Sets the name of the component. This is a convenience method for setting a qualifier with
         * the given name.
         *
         * @param name the name of the component
         *
         * @return this builder
         */
        public Builder<T> name(String name) {
            return this.qualifier(QualifierKey.of(name));
        }

        /**
         * Adds a qualifier to the component. Qualifiers are used to differentiate between
         * components of the same type.
         *
         * @param qualifier the qualifier to add
         *
         * @return this builder
         */
        public Builder<T> qualifier(QualifierKey<?> qualifier) {
            this.qualifier.add(qualifier);
            return this;
        }

        /**
         * Adds multiple qualifiers to the component. Qualifiers are used to differentiate between
         * components of the same type.
         *
         * @param qualifiers the qualifiers to add
         *
         * @return this builder
         */
        public Builder<T> qualifiers(QualifierKey<?>... qualifiers) {
            this.qualifier.addAll(qualifiers);
            return this;
        }

        /**
         * Adds multiple qualifiers to the component. Qualifiers are used to differentiate between
         * components of the same type.
         *
         * @param qualifiers the qualifiers to add
         *
         * @return this builder
         */
        public Builder<T> qualifiers(Set<QualifierKey<?>> qualifiers) {
            this.qualifier.addAll(qualifiers);
            return this;
        }

        /**
         * Removes all qualifiers, making it so a lookup will only match by type.
         *
         * @return this builder
         */
        public Builder<T> withoutQualifiers() {
            this.qualifier.clear();
            return this;
        }

        /**
         * Sets the scope of the component. The scope determines the lifecycle of the component.
         *
         * @param scope the scope of the component
         *
         * @return this builder
         */
        public Builder<T> scope(Scope scope) {
            this.scope = scope;
            return this;
        }

        /**
         * Sets whether {@link OnInitialized} callbacks of the component should be activated after
         * initialization.
         *
         * @param postConstructionAllowed whether post-construction should be activated on
         * provisioning
         *
         * @return this builder
         */
        public Builder<T> postConstructionAllowed(boolean postConstructionAllowed) {
            this.postConstructionAllowed = postConstructionAllowed;
            return this;
        }

        /**
         * Sets whether the lookup for this component should be strict. If the lookup is strict, the
         * type of the hierarchy has to match this key exactly. If the lookup is not strict, the
         * type of the hierarchy can be a sub-type of this key.
         *
         * <p>If strict-mode is not explicitly set, {@link Tristate#UNDEFINED} is returned. In this
         * case it remains
         * up to the component provider to decide whether strict-mode should be applied.
         *
         * @param strict whether the lookup for this component should be strict
         *
         * @return this builder
         *
         * @see #strict()
         * @see #fuzzy()
         */
        public Builder<T> strict(boolean strict) {
            this.strict = Tristate.valueOf(strict);
            return this;
        }

        /**
         * Sets that strict matching should be used for this component key. If strict matching is
         * used, the type of the hierarchy has to match this key exactly.
         *
         * <p>Inverse of {@link #fuzzy()}, for convenience.
         *
         * @return this builder
         */
        public Builder<T> strict() {
            return this.strict(true);
        }

        /**
         * Sets that fuzzy matching should be used for this component key. If fuzzy matching is
         * used, the type of the hierarchy can be a sub-type of this key.
         *
         * <p>Inverse of {@link #strict()}, for convenience.
         *
         * @return this builder
         */
        public Builder<T> fuzzy() {
            return this.strict(false);
        }

        /**
         * Builds a collector key builder for the component type of this key. The collector key
         * builder can be used to create a new key for a collection of components of the given
         * type.
         *
         * @return a new collector key builder
         */
        public Builder<ComponentCollection<T>> collector() {
            ParameterizableType collectionType =
                ParameterizableType.builder(ComponentCollection.class)
                    .parameters(this.type)
                    .build();
            Builder<?> builder = builder(collectionType)
                .qualifiers(this.qualifier.qualifiers())
                .scope(this.scope)
                .postConstructionAllowed(this.postConstructionAllowed);
            return TypeUtils.unchecked(builder, Builder.class);
        }

        /**
         * Builds a new key based on the values of this builder.
         *
         * @return a new key
         */
        public ComponentKey<T> build() {
            return new ComponentKey<>(
                this.selectionStrategy,
                this.failureStrategy,
                this.type,
                this.qualifier,
                this.scope,
                this.postConstructionAllowed,
                this.strict
            );
        }

        /**
         * Builds a new view of the key based on the values of this builder. The view will have the
         * same type and qualifiers as the key. Views are not attached to a scope, and do not
         * indicate whether the component should be enabled. This method is useful for comparing
         * keys, or for use in maps.
         *
         * <p>Views always retain the parameterization of the key.
         *
         * @return a new view
         */
        public ComponentKeyView<T> view() {
            return new ComponentKeyView<>(this.type, this.qualifier);
        }
    }
}
