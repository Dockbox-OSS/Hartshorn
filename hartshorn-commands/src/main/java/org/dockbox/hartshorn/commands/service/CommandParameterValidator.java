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

@Service
@RequiresActivator(UseCommands.class)
public class CommandParameterValidator implements LifecycleObserver {

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        final MethodView<CommandParameterValidator, ?> method = applicationContext.environment()
                .introspect(this)
                .methods().named("onStarted", ApplicationContext.class)
                .get();
        final ParameterView<?> parameter = method.parameters().at(0).get();
        if (!"applicationContext".equals(parameter.name())) {
            applicationContext.log().warn("Parameter names are obfuscated, this will cause commands with @Parameter to be unable to inject arguments.");
            applicationContext.log().warn("   Add -parameters to your compiler args to keep parameter names.");
            applicationContext.log().warn("   See: https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html for more information.");
        }
    }
}
