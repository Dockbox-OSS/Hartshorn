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

package org.dockbox.hartshorn.beans;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationManager;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationManager;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServicePreProcessor;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.reflect.AccessModifier;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementContext;
import org.dockbox.hartshorn.util.reflect.ModifierCarrier;
import org.dockbox.hartshorn.util.reflect.ObtainableElement;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.reflect.TypedElementContext;

import java.util.List;

public class BeanServicePreProcessor implements ServicePreProcessor, ExitingComponentProcessor {

    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        return !(key.type().fields(Bean.class).isEmpty() && key.type().methods(Bean.class).isEmpty());
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final BeanContext beanContext = context.first(BeanContext.class).get();
        try {
            this.process(context, beanContext, key.type().fields(Bean.class));
            this.process(context, beanContext, key.type().methods(Bean.class));
        }
        catch (final ApplicationException e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    private <E extends AnnotatedElementContext<?>
            & ObtainableElement<?>
            & ModifierCarrier
            & TypedElementContext<?>>
    void process(final ApplicationContext applicationContext, final BeanContext context, final List<E> elements) throws ApplicationException {
        final ConditionMatcher conditionMatcher = applicationContext.get(ConditionMatcher.class);
        for (final E element : elements) {
            if (!element.has(AccessModifier.STATIC)) {
                throw new ApplicationException("Bean service pre-processor can only process static fields and methods");
            }
            if (conditionMatcher.match(element)) {
                this.process(element, applicationContext, context);
            }
        }
    }

    private <T, E extends AnnotatedElementContext<?>
            & ObtainableElement<?>
            & ModifierCarrier
            & TypedElementContext<?>>
    void process(final E element, final ApplicationContext applicationContext, final BeanContext context) throws ApplicationException {
        final Bean bean = element.annotation(Bean.class).get();
        final String id = bean.id();
        final T beanInstance = (T) element.obtain(applicationContext).orThrow(() -> new ApplicationException("Bean service pre-processor can only process static fields and methods"));
        final TypeContext<T> type = (TypeContext<T>) element.genericType();
        context.register(beanInstance, type, id);
    }

    @Override
    public Integer order() {
        return (Integer.MIN_VALUE / 2) - 512;
    }

    @Override
    public void exit(final ApplicationContext context) {
        final ApplicationManager manager = context.environment().manager();
        final BeanContext beanContext = context.first(BeanContext.class).get();
        if (manager instanceof ObservableApplicationManager observable) {
            for (final BeanObserver observer : observable.observers(BeanObserver.class))
                observer.onBeansCollected(context, beanContext);
        }
    }
}