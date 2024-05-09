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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.SimpleApplicationContext;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentPostConstructorImpl;
import org.dockbox.hartshorn.component.ScopeAwareComponentProvider;
import org.dockbox.hartshorn.component.populate.MethodsAndFieldsInjectionPointResolver;
import org.dockbox.hartshorn.component.processing.ComponentFinalizingPostProcessor;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.Initializer;

public class HartshornApplicationConfigurer {

    Customizer<StandardApplicationBuilder.Configurer> applicationBuilder = Customizer.useDefaults();
    Customizer<StandardApplicationContextConstructor.Configurer> applicationContextConstructor = Customizer.useDefaults();
    Customizer<ComponentFinalizingPostProcessor.Configurer> postProcessor = Customizer.useDefaults();
    Customizer<ContextualApplicationEnvironment.Configurer> environment = Customizer.useDefaults();
    Customizer<SimpleApplicationContext.Configurer> applicationContext = Customizer.useDefaults();
    Customizer<MethodsAndFieldsInjectionPointResolver.Configurer> injectionPointResolver = Customizer.useDefaults();
    Customizer<ScopeAwareComponentProvider.Configurer> componentProvider = Customizer.useDefaults();
    Customizer<ComponentPostConstructorImpl.Configurer> componentPostConstructor = Customizer.useDefaults();

    public HartshornApplicationConfigurer mainClass(Class<?> mainClass) {
        this.applicationBuilder = this.applicationBuilder.compose(builder -> builder.mainClass(mainClass));
        return this;
    }

    public HartshornApplicationConfigurer mainClass(Initializer<Class<?>> mainClass) {
        this.applicationBuilder = this.applicationBuilder.compose(builder -> builder.mainClass(mainClass));
        return this;
    }

    public HartshornApplicationConfigurer inferMainClass() {
        this.applicationBuilder = this.applicationBuilder.compose(StandardApplicationBuilder.Configurer::inferMainClass);
        return this;
    }
}
