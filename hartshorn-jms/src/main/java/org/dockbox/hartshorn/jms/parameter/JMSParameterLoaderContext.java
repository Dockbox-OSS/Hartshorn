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

package org.dockbox.hartshorn.jms.parameter;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.util.reflect.ExecutableElementContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import javax.jms.Message;

public class JMSParameterLoaderContext extends ParameterLoaderContext {

    private final Message message;

    public JMSParameterLoaderContext(final Message message,
                                     final ExecutableElementContext<?, ?> executable,
                                     final TypeContext<?> type,
                                     final Object instance,
                                     final ApplicationContext applicationContext) {
        super(executable, type, instance, applicationContext);
        this.message = message;
    }

    public Message message() {
        return this.message;
    }
}
