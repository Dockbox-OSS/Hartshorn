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

package org.dockbox.hartshorn.jms;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.AutomaticActivation;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.jms.annotations.Producer;
import org.dockbox.hartshorn.jms.annotations.UseJMS;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodInterceptorPostProcessor;

@AutomaticActivation
public class JMSProducerPostProcessor extends ServiceAnnotatedMethodInterceptorPostProcessor<Producer, UseJMS> {

    @Override
    public Class<UseJMS> activator() {
        return UseJMS.class;
    }

    @Override
    public Class<Producer> annotation() {
        return Producer.class;
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        return methodContext.method().parameterCount() == 1;
    }

    @Override
    public <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        final Producer producer = methodContext.method().annotation(Producer.class).get();
        final JMSSessionContext sessionContext = JMSSessionContext.of(producer);
        final JMSProducerTask producerTask = context.get(JMSFactory.class).createProducer(sessionContext);

        return interceptorContext -> {
            producerTask.send(interceptorContext.arg(0));
            return null;
        };
    }
}
