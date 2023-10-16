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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
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

import java.util.List;
public class StaticBindingServicePreProcessor extends ComponentPreProcessor implements ExitingComponentProcessor {

    @Override
    public <T> void process(ApplicationContext context, ComponentProcessingContext<T> processingContext) {
        StaticComponentContext staticComponentContext = context.first(StaticComponentContext.CONTEXT_KEY).get();

        try {
            TypeView<T> type = processingContext.type();
            List<FieldView<T, ?>> fields = type.fields().annotatedWith(StaticBinds.class);
            this.process(context, staticComponentContext, TypeUtils.adjustWildcards(fields, List.class));
            List<MethodView<T, ?>> methods = type.methods().annotatedWith(StaticBinds.class);
            this.process(context, staticComponentContext, TypeUtils.adjustWildcards(methods, List.class));
        }
        catch (ApplicationException e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    private <T, E extends AnnotatedElementView
            & ModifierCarrierView
            & GenericTypeView<T>>
    void process(ApplicationContext applicationContext, StaticComponentCollector context, List<E> elements) throws ApplicationException {
        if (elements.isEmpty()) {
            return;
        }

        ViewContextAdapter adapter = applicationContext.get(ViewContextAdapter.class);
        ConditionMatcher conditionMatcher = applicationContext.get(ConditionMatcher.class);
        for (E element : elements) {
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
    void process(ViewContextAdapter adapter, E element, StaticComponentCollector context) throws ApplicationException {
        StaticBinds staticBinds = element.annotations().get(StaticBinds.class).get();
        String id = staticBinds.id();

        T beanInstance = adapter.load(element)
                .orElseThrow(() -> new ApplicationException("Bean service pre-processor can only process static fields and methods"));

        TypeView<T> type = element.genericType();
        context.register(beanInstance, type.type(), id);
    }

    @Override
    public int priority() {
        return ProcessingPriority.HIGH_PRECEDENCE - 512;
    }

    @Override
    public void exit(ApplicationContext context) {
        ApplicationEnvironment environment = context.environment();
        StaticComponentContext staticComponentContext = context.first(StaticComponentContext.CONTEXT_KEY).get();
        if (environment instanceof ObservableApplicationEnvironment observable) {
            for (StaticComponentObserver observer : observable.observers(StaticComponentObserver.class)) {
                observer.onStaticComponentsCollected(context, staticComponentContext);
            }
        }
    }
}
