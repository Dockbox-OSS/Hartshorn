/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

@Service(activators = UseCommands.class)
public class CommandParameterValidator implements LifecycleObserver {

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        final MethodContext<?, CommandParameterValidator> preload = TypeContext.of(this).method("onStarted", ApplicationContext.class).get();
        final ParameterContext<?> parameter = preload.parameters().get(0);
        if (!"applicationContext".equals(parameter.name())) {
            applicationContext.log().warn("Parameter names are obfuscated, this will cause commands with @Parameter to be unable to inject arguments.");
            applicationContext.log().warn("   Add -parameters to your compiler args to keep parameter names.");
            applicationContext.log().warn("   See: https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html for more information.");
        }
    }

    @Override
    public void onExit(final ApplicationContext applicationContext) {
        // Nothing happens
    }
}
