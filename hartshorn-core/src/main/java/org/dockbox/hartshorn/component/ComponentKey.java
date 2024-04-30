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

package org.dockbox.hartshorn.component;

import java.util.Objects;
import java.util.Set;

import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.HighestPriorityProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.ProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A key that can be used to identify a component. This contains required metadata to identify a component, such as
 * its type, name, scope and whether it should be enabled on provisioning.
 *
 * <p>Component keys contain a {@link ParameterizableType} that describes the type of the component. This type can
 * be parameterized. Therefore, key instances differentiate between e.g. {@code List<String>} and {@code List<Integer>}.
 *
 * <p>Keys are immutable, to build a new key based on an existing key, use {@link #mutable()}.
 *
 * @see ComponentProvider#get(ComponentKey)
 * @see ComponentProvider#get(ComponentKey, ComponentRequestContext)
 * @see ComponentKey#builder(Class)
 *
 * @param <T> the type of the component
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public final class ComponentKey<T> implements Reportable {

    private final ProviderSelectionStrategy strategy;
    private final ParameterizableType type;
    private final CompositeQualifier qualifier;
    private final Scope scope;
    private final boolean enable;
    private final Tristate strict;

    private ComponentKey(
            ProviderSelectionStrategy strategy,
            ParameterizableType type,
            CompositeQualifier qualifier,
            Scope scope,
            boolean enable,
            Tristate strict
    ) {
        this.strategy = strategy;
        this.type = type;
        this.qualifier = qualifier;
        this.scope = scope;
        this.enable = enable;
        this.strict = strict;
    }

    /**
     * Creates a new builder for a component key of the given type. If the type is parameterized, the key will
     * be for the raw type.
     *
     * @param type the type of the component
     * @return a new builder
     * @param <T> the type of the component
     */
    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>(ParameterizableType.create(type));
    }

    /**
     * Creates a new builder for a component key of the given type. If the type is parameterized, the key will
     * retain its parameterization.
     *
     * @param type the type of the component
     * @return a new builder
     * @param <T> the type of the component
     */
    public static <T> Builder<T> builder(TypeView<T> type) {
        return new Builder<>(ParameterizableType.create(type));
    }

    /**
     * Creates a new builder for a component key of the given type. If the type is parameterized, the key will
     * retain its parameterization.
     *
     * @param type the type of the component
     * @return a new builder
     */
    public static Builder<?> builder(ParameterizableType type) {
        return new Builder<>(type);
    }

    public static <T> ComponentKey<ComponentCollection<T>> collect(Class<T> type) {
        return TypeUtils.adjustWildcards(collect(ParameterizableType.create(type)), ComponentKey.class);
    }

    public static ComponentKey<ComponentCollection<?>> collect(ParameterizableType type) {
        ParameterizableType collectionType = ParameterizableType.builder(ComponentCollection.class)
            .parameters(type)
            .build();
        return TypeUtils.adjustWildcards(ComponentKey.of(collectionType), ComponentKey.class);
    }

    /**
     * Creates a new component key of the given type. If the type is parameterized, the key will be for the raw type.
     *
     * @param type the type of the component
     * @return a new component key
     * @param <T> the type of the component
     */
    public static <T> ComponentKey<T> of(Class<T> type) {
        return ComponentKey.builder(type).build();
    }

    /**
     * Creates a new component key of the given type. If the type is parameterized, the key will retain its
     * parameterization.
     *
     * @param type the type of the component
     * @return a new component key
     * @param <T> the type of the component
     */
    public static <T> ComponentKey<T> of(TypeView<T> type) {
        return ComponentKey.builder(type).build();
    }

    /**
     * Creates a new component key of the given type. If the type is parameterized, the key will retain its
     * parameterization.
     *
     * @param type the type of the component
     * @return a new component key
     */
    public static ComponentKey<?> of(ParameterizableType type) {
        return ComponentKey.builder(type).build();
    }

    /**
     * Creates a new named component key of the given type. If the type is parameterized, the key will be for the raw type.
     *
     * @param key the type of the component
     * @param name the name of the component
     * @return a new component key
     * @param <T> the type of the component
     */
    public static <T> ComponentKey<T> of(Class<T> key, String name) {
        return ComponentKey.builder(key).name(name).build();
    }

    /**
     * Creates a new named component key of the given type. If the type is parameterized, the key will retain its
     * parameterization.
     *
     * @param type the type of the component
     * @param named the name of the component
     * @return a new component key
     * @param <T> the type of the component
     */
    public static <T> ComponentKey<T> of(TypeView<T> type, String named) {
        return ComponentKey.of(type.type(), named);
    }

    /**
     * Creates a new key builder based on this key. The builder will have the same type, name, scope and enable
     * values as this key. The builder can be used to create a new key with different values.
     *
     * @return a new builder
     */
    public Builder<T> mutable() {
        return new Builder<>(this);
    }

    /**
     * Creates a new view of this key. The view will have the same type and name as this key. Views are not attached
     * to a scope, and do not indicate whether the component should be enabled. This method is useful for comparing
     * keys, or for use in maps.
     *
     * <p>Views always retain the parameterization of the key.
     *
     * @return a new view
     */
    public ComponentKeyView<T> view() {
        return new ComponentKeyView<>(this);
    }

    /**
     * Returns the qualified name of this key. The qualified name is the name of the type, followed by the name of
     * the component, followed by the name of the scope. If the component has no name, the name is omitted. If the
     * component has no explicit scope, the default scope is {@link Scope#DEFAULT_SCOPE}.
     *
     * @param qualifyType whether the type should be qualified with its package name
     * @return the qualified name
     */
    public String qualifiedName(boolean qualifyType) {
        String qualifier = StringUtilities.join(", ", this.qualifier.qualifiers(), QualifierKey::toString);
        String qualifierSuffix = StringUtilities.empty(qualifier) ? "" : ":" + qualifier;
        String scopeName = this.scope.installableScopeType().name();
        String typeName = qualifyType ? this.type.toQualifiedString() : this.type.toString();
        return typeName + qualifierSuffix + " @ " + scopeName;
    }

    @Override
    public String toString() {
        return "ComponentKey<" + this.qualifiedName(false) + ">";
    }

    @Override
    public boolean equals(Object other) {
        if(this == other) {
            return true;
        }
        if(other == null || this.getClass() != other.getClass()) {
            return false;
        }
        ComponentKey<?> otherComponentKey = (ComponentKey<?>) other;
        return this.enable == otherComponentKey.enable
                && this.type.equals(otherComponentKey.type)
                && Objects.equals(this.qualifier, otherComponentKey.qualifier)
                && Objects.equals(this.scope, otherComponentKey.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.qualifier, this.scope, this.enable);
    }

    /**
     * Returns the raw type of the component, excluding any type parameters.
     *
     * @return the raw type of the component
     */
    public Class<T> type() {
        return TypeUtils.adjustWildcards(this.type.type(), Class.class);
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
     * Returns the name of the component. If the component has no name, {@code null} is returned.
     *
     * @return the name of the component, or {@code null} if the component has no name
     *
     * @deprecated explicit names have been replaced with qualifiers. Use {@link #qualifier()} instead.
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
    public String name() {
        return null;
    }

    /**
     * Returns the qualifier of the component. If the component has no explicit qualifier, an empty qualifier is
     * returned.
     *
     * @return the qualifier of the component
     */
    public CompositeQualifier qualifier() {
        return this.qualifier;
    }

    /**
     * Returns the scope of the component. If the component has no explicit scope, the default scope is
     * {@link Scope#DEFAULT_SCOPE}.
     *
     * @return the scope of the component
     */
    public Scope scope() {
        return this.scope;
    }

    /**
     * Returns whether the component should be enabled on provisioning. If the component has no explicit enable
     * value, {@code true} is returned.
     *
     * @return whether the component should be enabled on provisioning
     */
    public boolean enable() {
        return this.enable;
    }

    /**
     * Returns whether the lookup for this component should be strict. If the lookup is strict, the type of the
     * hierarchy has to match this key exactly. If the lookup is not strict, the type of the hierarchy can be a
     * sub-type of this key.
     *
     * <p>If strict-mode is not explicitly set, {@link Tristate#UNDEFINED} is returned. In this case it remains
     * up to the component provider to decide whether strict-mode should be applied.
     *
     * @return whether the component should be enabled on provisioning
     */
    public Tristate strict() {
        return this.strict;
    }

    /**
     * Returns the strategy that should be used to select a provider for this component. This often selects
     * a provider based on the priority of the key.
     *
     * @return the strategy that should be used to select a provider for this component
     */
    public ProviderSelectionStrategy strategy() {
        return this.strategy;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("type").writeDelegate(this.type);
        collector.property("qualifier").writeDelegate(this.qualifier);
        if (scope != null) {
            collector.property("scope").writeDelegate(this.scope.installableScopeType());
        }
        collector.property("postConstructionAllowed").writeBoolean(this.postConstructionAllowed);
    }

    /**
     * A builder for {@link ComponentKey}s. The builder can be used to create a new key based on an existing key,
     * or to create a new key from scratch.
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
        private ProviderSelectionStrategy strategy = HighestPriorityProviderSelectionStrategy.INSTANCE;
        private Scope scope = Scope.DEFAULT_SCOPE;
        private boolean enable = true;
        private Tristate strict = Tristate.UNDEFINED;

        private Builder(ComponentKey<T> key) {
            this.type = key.type;
            this.qualifier.addAll(key.qualifier);
            this.scope = key.scope;
            this.enable = key.enable;
        }

        private Builder(ParameterizableType type) {
            this.type = type;
        }

        public <U> Builder<U> type(Class<U> type) {
            return copyProperties(builder(type));
        }

        public <U> Builder<U> type(TypeView<U> type) {
            return copyProperties(builder(type));
        }

        public Builder<?> type(ParameterizableType type) {
            return copyProperties(builder(type));
        }

        private <U> Builder<U> copyProperties(Builder<U> builder) {
            return builder
                    .qualifiers(this.qualifier.qualifiers())
                    .scope(this.scope)
                    .enable(this.enable);
        }

        public Builder<T> strategy(ProviderSelectionStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder<T> name(String name) {
            return this.qualifier(QualifierKey.of(name));
        }

        public Builder<T> qualifier(QualifierKey<?> qualifier) {
            this.qualifier.add(qualifier);
            return this;
        }

        public Builder<T> qualifiers(QualifierKey<?>... qualifiers) {
            this.qualifier.addAll(qualifiers);
            return this;
        }

        public Builder<T> qualifiers(Set<QualifierKey<?>> qualifiers) {
            this.qualifier.addAll(qualifiers);
            return this;
        }

        public Builder<T> scope(Scope scope) {
            this.scope = scope;
            return this;
        }

        public Builder<T> enable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public Builder<T> strict(boolean strict) {
            this.strict = Tristate.valueOf(strict);
            return this;
        }

        public Builder<ComponentCollection<T>> collector() {
            ParameterizableType collectionType = ParameterizableType.builder(ComponentCollection.class)
                    .parameters(this.type)
                    .build();
            Builder<?> builder = builder(collectionType)
                    .qualifiers(this.qualifier.qualifiers())
                    .scope(this.scope)
                    .enable(this.enable);
            return TypeUtils.adjustWildcards(builder, Builder.class);
        }

        public ComponentKey<T> build() {
            return new ComponentKey<>(this.strategy, this.type, this.qualifier, this.scope, this.enable, this.strict);
        }

        public ComponentKeyView<T> view() {
            return new ComponentKeyView<>(this.type, this.qualifier);
        }
    }
}
