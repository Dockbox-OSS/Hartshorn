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
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServicePreProcessor;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.introspect.AccessModifier;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ModifierCarrierView;
import org.dockbox.hartshorn.util.introspect.view.ObtainableView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;

public class BeanServicePreProcessor implements ServicePreProcessor, ExitingComponentProcessor {

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final ComponentProcessingContext<T> processingContext) {
        final TypeView<T> type = processingContext.type();
        final boolean hasBeanFields = !type.fields().annotatedWith(Bean.class).isEmpty();
        final boolean hasBeanMethods = !type.methods().annotatedWith(Bean.class).isEmpty();
        return hasBeanFields || hasBeanMethods;
    }

    @Override
    public <T> void process(final ApplicationContext context, final ComponentProcessingContext<T> processingContext) {
        final BeanContext beanContext = context.first(BeanContext.class).get();
        try {
            final TypeView<T> type = processingContext.type();
            final List<FieldView<T, ?>> fields = type.fields().annotatedWith(Bean.class);
            this.process(context, beanContext, fields);
            final List<MethodView<T, ?>> methods = type.methods().annotatedWith(Bean.class);
            this.process(context, beanContext, methods);
        }
        catch (final ApplicationException e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    private <E extends AnnotatedElementView
            & ObtainableView<?>
            & ModifierCarrierView
            & GenericTypeView<?>>
    void process(final ApplicationContext applicationContext, final BeanContext context, final List<E> elements) throws ApplicationException {
        final ConditionMatcher conditionMatcher = applicationContext.get(ConditionMatcher.class);
        for (final E element : elements) {
            if (!element.has(AccessModifier.STATIC)) {
                throw new ApplicationException("Bean service pre-processor can only process static fields and methods");
            }
            if (conditionMatcher.match(element)) {
                this.process(element, context);
            }
        }
    }

    private <T, E extends AnnotatedElementView
            & ObtainableView<?>
            & ModifierCarrierView
            & GenericTypeView<?>>
    void process(final E element, final BeanContext context) throws ApplicationException {
        final Bean bean = element.annotations().get(Bean.class).get();
        final String id = bean.id();
        final Object beanInstance = element.getWithContext()
                .orThrow(() -> new ApplicationException("Bean service pre-processor can only process static fields and methods"));
        final TypeView<?> type = element.genericType();
        //noinspection unchecked - We know the type is correct
        context.register((T) beanInstance, (Class<T>) type.type(), id);
    }

    @Override
    public Integer order() {
        return (Integer.MIN_VALUE / 2) - 512;
    }

    @Override
    public void exit(final ApplicationContext context) {
        final ApplicationEnvironment environment = context.environment();
        final BeanContext beanContext = context.first(BeanContext.class).get();
        if (environment instanceof ObservableApplicationEnvironment observable) {
            for (final BeanObserver observer : observable.observers(BeanObserver.class))
                observer.onBeansCollected(context, beanContext);
        }
    }
}
