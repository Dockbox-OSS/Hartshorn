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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.jms.annotations.JMSConfiguration;
import org.dockbox.hartshorn.jms.annotations.Subscriber;
import org.dockbox.hartshorn.jms.parameter.JMSConsumerParameterLoader;
import org.dockbox.hartshorn.jms.parameter.JMSParameterLoaderContext;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodPostProcessor;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;

public class JMSConsumerPostProcessor extends ServiceAnnotatedMethodPostProcessor<Subscriber> {

    @Override
    public Class<Subscriber> annotation() {
        return Subscriber.class;
    }

    @Override
    protected <T> void process(ApplicationContext context, ComponentKey<T> key, @Nullable T instance, MethodView<T, ?> method) {
        final JMSConfiguration configuration = method.annotations().get(JMSConfiguration.class).get();

        final JMSFactory jmsFactory = context.get(JMSFactory.class);

        final JMSDestinationContext destinationContext = JMSDestinationContext.of(configuration);
        final JMSConsumerParameterLoader parameterLoader = new JMSConsumerParameterLoader();

        final TypeView<T> typeView = context.environment().introspect(key.type());

        final JMSConsumer consumer = message -> {
            final T accessor = context.get(key);
            final JMSParameterLoaderContext loaderContext = new JMSParameterLoaderContext(message, method, typeView, accessor, context);
            final List<Object> arguments = parameterLoader.loadArguments(loaderContext, message);
            method.invoke(accessor, arguments);
        };

        final JMSConsumerTask consumerTask = jmsFactory.createConsumer(consumer, destinationContext);

        context.get(JMSThreadPoolProvider.class).executorService().submit(consumerTask);
    }

    @Override
    public Integer order() {
        return ProcessingOrder.LAST;
    }
}
