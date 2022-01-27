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

import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.util.parameter.RuleBasedParameterLoader;
import org.dockbox.hartshorn.util.reflect.ParameterContext;

import javax.jms.JMSException;
import javax.jms.TextMessage;

public class JMSConsumerParameterLoader extends RuleBasedParameterLoader<JMSParameterLoaderContext> {

    public JMSConsumerParameterLoader() {
        this.add(new JMSMessageParameterLoader());
    }

    @Override
    protected <T> T loadDefault(final ParameterContext<T> parameter, final int index, final JMSParameterLoaderContext context, final Object... args) {
        if (context.message() instanceof TextMessage textMessage) {
            final ObjectMapper objectMapper = context.applicationContext().get(ObjectMapper.class);
            try {
                return objectMapper.read(textMessage.getText(), parameter.type()).rethrowUnchecked().orNull();
            }
            catch (final JMSException e) {
                context.applicationContext().handle(e);
            }
        }
        return null;
    }
}
