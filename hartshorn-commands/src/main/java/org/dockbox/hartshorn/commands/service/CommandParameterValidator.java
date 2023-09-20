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

package org.dockbox.hartshorn.commands.service;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;

/**
 * A lifecycle observer that validates the parameter names of the {@link org.dockbox.hartshorn.commands.annotations.Command}
 * annotated methods. If the parameter names are obfuscated, the observer will log a warning. This is because obfuscated
 * parameter names will cause the {@link org.dockbox.hartshorn.commands.CommandParser} to be unable to inject arguments
 * into command methods.
 *
 * <p>To prevent this validator from failing, add the {@code -parameters} compiler argument to your project. This will
 * keep the parameter names intact, and allow the {@link org.dockbox.hartshorn.commands.CommandParser} to inject arguments
 * into command methods.
 *
 * @see <a href="https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html">Oracle Java Documentation: Obtaining Names of Method Parameters</a>
 *
 * @since 0.4.7
 * @author Guus Lieben
 */
@Service
@RequiresActivator(UseCommands.class)
public class CommandParameterValidator implements LifecycleObserver {

    @Override
    public void onStarted(ApplicationContext applicationContext) {
        // Use current class to get the method, as this class is guaranteed to have a method with an expected signature.
        MethodView<CommandParameterValidator, ?> method = applicationContext.environment()
                .introspect(this)
                .methods().named("onStarted", ApplicationContext.class)
                .get();

        ParameterView<?> parameter = method.parameters().at(0).get();
        if (!"applicationContext".equals(parameter.name())) {
            applicationContext.log().warn("Parameter names are obfuscated, this will cause commands with @Parameter to be unable to inject arguments.");
            applicationContext.log().warn("   Add -parameters to your compiler args to keep parameter names.");
            applicationContext.log().warn("   See: https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html for more information.");
        }
    }
}
