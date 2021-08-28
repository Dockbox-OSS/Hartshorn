/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.api;

import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.api.annotations.UseBootstrap;
import org.dockbox.hartshorn.api.config.GlobalConfig;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.InjectableBootstrap;
import org.dockbox.hartshorn.di.Modifier;
import org.dockbox.hartshorn.di.annotations.context.LogExclude;
import org.dockbox.hartshorn.di.annotations.inject.InjectPhase;
import org.dockbox.hartshorn.di.annotations.inject.Required;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The global bootstrapping component which instantiates all configured services and provides access
 * to server information.
 */
@LogExclude
public abstract class HartshornBootstrap extends InjectableBootstrap {

    private final Set<MethodContext<?, ?>> postBootstrapActivations = HartshornUtils.emptyConcurrentSet();

    /**
     * Returns the active instance of {@link HartshornBootstrap}, if any.
     *
     * @return The active instance or <code>null</code>
     */
    public static HartshornBootstrap instance() {
        return (HartshornBootstrap) InjectableBootstrap.instance();
    }

    @Override
    public void create(final Collection<String> prefixes, final Class<?> activationSource, final List<Annotation> activators, final Multimap<InjectPhase, InjectConfiguration> configs, final Modifier... modifiers) {
        activators.add(new UseBootstrap() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return UseBootstrap.class;
            }
        });
        super.create(prefixes, activationSource, activators, configs, modifiers);

        final GlobalConfig globalConfig = this.context().get(GlobalConfig.class);
        Except.useStackTraces(globalConfig.stacktraces());
        Except.with(globalConfig.level());
    }

    /**
     * Initiates a {@link Hartshorn} instance. Collecting integrated services and registering them to the
     * appropriate instances where required.
     */
    @Override
    public void init() {
        Hartshorn.log().info("Initialising Hartshorn v" + Hartshorn.VERSION);
        for (final MethodContext<?, ?> postBootstrapActivation : this.postBootstrapActivations) {
            this.context().invoke(postBootstrapActivation);
        }
        // Ensure all services requiring a platform implementation have one present
        this.context().environment().types(Required.class).forEach(type -> {
            if (this.context().environment().children(type).isEmpty()) {
                this.handleMissingBinding(type);
            }
        });
    }

    /**
     * Indicates the behavior to use when a {@link Required} type has no active binding.
     *
     * @param type
     *         The required type
     */
    protected void handleMissingBinding(final TypeContext<?> type) {
        throw new IllegalStateException("No implementation exists for [" + type.qualifiedName() + "], this will cause functionality to misbehave or not function!");
    }

    @Override
    public Logger log() {
        return Hartshorn.log();
    }

    /**
     * Registers the given method as a activation action which should run when bootstrapping
     * completes. This requires all activators for the declaring class to be present.
     *
     * @param method
     *         The method to activate
     */
    void addPostBootstrapActivation(final MethodContext<?, ?> method) {
        Objects.requireNonNull(method);
        final TypeContext<?> type = method.parent();
        final Exceptional<ComponentContainer> container = this.context().locator().container(type);

        if (container.present()) {
            final ComponentContainer componentContainer = container.get();
            final List<Class<? extends Annotation>> activators = componentContainer.activators();

            if (componentContainer.hasActivator() && activators.stream().allMatch(this.context()::hasActivator))
                this.postBootstrapActivations.add(method);
        }
    }

}
