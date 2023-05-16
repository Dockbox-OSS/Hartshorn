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

package org.dockbox.hartshorn.component.contextual;

import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ModifierCarrierView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
public class StaticBindingServicePreProcessor extends ComponentPreProcessor implements ExitingComponentProcessor {

    @Override
    public <T> void process(final ApplicationContext context, final ComponentProcessingContext<T> processingContext) {
        final StaticComponentContext staticComponentContext = context.first(StaticComponentContext.CONTEXT_KEY).get();

        try {
            final TypeView<T> type = processingContext.type();
            final List<FieldView<T, ?>> fields = type.fields().annotatedWith(StaticBinds.class);
            this.process(context, staticComponentContext, TypeUtils.adjustWildcards(fields, List.class));
            final List<MethodView<T, ?>> methods = type.methods().annotatedWith(StaticBinds.class);
            this.process(context, staticComponentContext, TypeUtils.adjustWildcards(methods, List.class));
        }
        catch (final ApplicationException e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    private <T, E extends AnnotatedElementView
            & ModifierCarrierView
            & GenericTypeView<T>>
    void process(final ApplicationContext applicationContext, final StaticComponentCollector context, final List<E> elements) throws ApplicationException {
        if (elements.isEmpty()) return;

        final ViewContextAdapter adapter = applicationContext.get(ViewContextAdapter.class);
        final ConditionMatcher conditionMatcher = applicationContext.get(ConditionMatcher.class);
        for (final E element : elements) {
            if (!element.modifiers().isStatic()) {
                throw new ApplicationException("Bean service pre-processor can only process static fields and methods");
            }
            if (conditionMatcher.match(element)) {
                this.process(adapter, element, context);
            }
        }
    }

    private <T, E extends AnnotatedElementView
            & ModifierCarrierView
            & GenericTypeView<T>>
    void process(final ViewContextAdapter adapter, final E element, final StaticComponentCollector context) throws ApplicationException {
        final StaticBinds staticBinds = element.annotations().get(StaticBinds.class).get();
        final String id = staticBinds.id();

        final T beanInstance = adapter.load(element)
                .orElseThrow(() -> new ApplicationException("Bean service pre-processor can only process static fields and methods"));

        final TypeView<T> type = element.genericType();
        context.register(beanInstance, type.type(), id);
    }

    @Override
    public Integer order() {
        return (Integer.MIN_VALUE / 2) - 512;
    }

    @Override
    public void exit(final ApplicationContext context) {
        final ApplicationEnvironment environment = context.environment();
        final StaticComponentContext staticComponentContext = context.first(StaticComponentContext.CONTEXT_KEY).get();
        if (environment instanceof ObservableApplicationEnvironment observable) {
            for (final StaticComponentObserver observer : observable.observers(StaticComponentObserver.class))
                observer.onStaticComponentsCollected(context, staticComponentContext);
        }
    }
}
