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

package org.dockbox.selene.api;

import com.google.common.collect.Multimap;

import org.dockbox.selene.api.annotations.UseBootstrap;
import org.dockbox.selene.api.config.GlobalConfig;
import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.InjectableBootstrap;
import org.dockbox.selene.di.annotations.InjectPhase;
import org.dockbox.selene.di.annotations.Required;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import lombok.Getter;

/**
 * The global bootstrapping component which instantiates all configured services and provides access
 * to server information.
 */
public abstract class SeleneBootstrap extends InjectableBootstrap {

    @Getter
    private String version;
    private final Set<Method> postBootstrapActivations = SeleneUtils.emptyConcurrentSet();

    @Override
    public void create(String prefix, Class<?> activationSource, List<Annotation> activators, Multimap<InjectPhase, InjectConfiguration> configs) {
        activators.add(new UseBootstrap() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return UseBootstrap.class;
            }
        });
        super.create(prefix, activationSource, activators, configs);

        final GlobalConfig globalConfig = this.getContext().get(GlobalConfig.class);
        Except.useStackTraces(globalConfig.getStacktracesAllowed());
        Except.with(globalConfig.getExceptionLevel());
        this.version = globalConfig.getVersion();
    }

    public static boolean isConstructed() {
        return instance() != null;
    }

    public static SeleneBootstrap instance() {
        return (SeleneBootstrap) InjectableBootstrap.instance();
    }

    /**
     * Initiates a {@link Selene} instance. Collecting integrated services and registering them to the
     * appropriate instances where required.
     */
    @Override
    public void init() {
        Selene.log().info("Initialising Selene " + this.getVersion());
        for (Method postBootstrapActivation : this.postBootstrapActivations) {
            this.getContext().invoke(postBootstrapActivation);
        }
        // Ensure all services requiring a platform implementation have one present
        Reflect.annotatedTypes(SeleneInformation.PACKAGE_PREFIX, Required.class).forEach(type -> {
            if (Reflect.subTypes(SeleneInformation.PACKAGE_PREFIX, type).isEmpty()) {
                this.handleMissingBinding(type);
            }
        });

    }

    protected void handleMissingBinding(Class<?> type) {
        throw new IllegalStateException("No implementation exists for [" + type.getCanonicalName() + "], this will cause functionality to misbehave or not function!");
    }

    void addPostBootstrapActivation(Method method, Class<?> type) {
        final Service annotation = type.getAnnotation(Service.class);
        final Class<? extends Annotation> activator = annotation.activator();
        if (Service.class.equals(activator)) return;

        if (this.getContext().hasActivator(activator))
            this.postBootstrapActivations.add(method);
    }

}
