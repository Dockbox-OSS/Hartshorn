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

package org.dockbox.hartshorn.component.populate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentRequiredException;
import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.component.populate.inject.RequireInjectionPointRule;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Attempt;

public abstract class AbstractComponentPopulationStrategy implements ComponentPopulationStrategy, ContextCarrier {

    private final Set<RequireInjectionPointRule> requiresComponentRules;
    private final ApplicationContext applicationContext;

    protected AbstractComponentPopulationStrategy(ApplicationContext applicationContext, Set<RequireInjectionPointRule> requiresComponentRules) {
        this.applicationContext = applicationContext;
        this.requiresComponentRules = requiresComponentRules;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public <T> void populate(PopulateComponentContext<T> context, AnnotatedElementView injectionPoint) throws ApplicationException {
        if (this.isApplicable(injectionPoint)) {
            List<Object> objectsToInject = this.resolveInjectedObjects(context, injectionPoint);
            this.processObjectInjection(context, injectionPoint, objectsToInject);
        }
    }

    private <T> void processObjectInjection(PopulateComponentContext<T> context, AnnotatedElementView injectionPoint, List<Object> objectsToInject) {
        if (injectionPoint instanceof MethodView<?, ?> executable) {
            MethodView<T, ?> method = TypeUtils.adjustWildcards(executable, MethodView.class);
            method.invoke(context.instance(), objectsToInject.toArray());
        }
        else if (injectionPoint instanceof FieldView<?,?> field) {
            if (objectsToInject.size() == 1) {
                this.processFieldInjection(field, context.instance(), objectsToInject.get(0));
            }
            else {
                throw new UnsupportedOperationException("Cannot inject multiple objects into a single field");
            }
        }
        else {
            throw new UnsupportedOperationException("Unsupported injection point type: " + injectionPoint.getClass().getName());
        }
    }

    private void processFieldInjection(FieldView<?, ?> field, Object instance, Object objectToInject) {
        if (objectToInject instanceof Collection<?> collection) {
            Attempt<?, Throwable> previousValue = field.get(instance);
            if (previousValue.present() && previousValue.get() instanceof Collection<?> existingCollection) {
                existingCollection.addAll(TypeUtils.adjustWildcards(collection, Collection.class));
                return;
            }
        }
        field.set(instance, objectToInject);
    }

    protected abstract boolean isApplicable(AnnotatedElementView injectionPoint);

    private List<Object> resolveInjectedObjects(PopulateComponentContext<?> context, AnnotatedElementView injectionPoint) throws ApplicationException {
        Set<InjectionPoint> injectionPoints = this.lookupInjectionPoints(injectionPoint);

        List<Object> objectsToInject = new ArrayList<>();
        for(InjectionPoint point : injectionPoints) {
            Object object;
            try {
                object = this.resolveInjectedObject(point, context);
            }
            catch(ApplicationException | ApplicationRuntimeException e) {
                if (this.shouldRequire(point)) {
                    throw new ComponentRequiredException("Could not resolve value for injection point " + point.injectionPoint().qualifiedName(), e);
                }
                else {
                    this.applicationContext().handle(e);
                    object = null;
                }
            }

            if (object == null && this.shouldRequire(point)) {
                throw new ComponentRequiredException("Injection point " + point.injectionPoint().qualifiedName() + " is required, but could not be provided");
            }
            objectsToInject.add(object);
        }
        return objectsToInject;
    }

    private Set<InjectionPoint> lookupInjectionPoints(AnnotatedElementView injectionPoint) {
        if (injectionPoint instanceof ExecutableElementView<?> executable) {
            return executable.parameters().all().stream()
                    .map(parameter -> new InjectionPoint(parameter.genericType(), parameter))
                    .collect(Collectors.toSet());
        }
        else if (injectionPoint instanceof FieldView<?,?> field) {
            return Set.of(new InjectionPoint(field.genericType(), field));
        }
        else {
            throw new UnsupportedOperationException("Unsupported injection point type: " + injectionPoint.getClass().getName());
        }
    }

    protected abstract Object resolveInjectedObject(InjectionPoint injectionPoint, PopulateComponentContext<?> context) throws ApplicationException, ApplicationRuntimeException;

    private boolean shouldRequire(InjectionPoint injectionPoint) {
        return this.requiresComponentRules.stream()
                .map(rule -> rule.isRequired(injectionPoint))
                .reduce(true, Boolean::logicalAnd);
    }
}
