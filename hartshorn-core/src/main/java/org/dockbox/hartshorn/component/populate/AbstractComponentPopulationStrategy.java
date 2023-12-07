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
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentRequiredException;
import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.component.populate.inject.RequireInjectionPointRule;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;

/**
 * A base implementation of {@link ComponentPopulationStrategy} which provides the basic functionality for
 * populating components. This includes the ability to indicate whether the strategy is applicable to a given
 * injection point, and the ability to resolve the objects to be injected into the injection point if it is
 * applicable.
 *
 * <p>
 */
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
    public <T> void populate(PopulateComponentContext<T> context, ComponentInjectionPoint<T> injectionPoint) throws ApplicationException {
        if (this.isApplicable(injectionPoint)) {
            List<Object> objectsToInject = this.resolveInjectedObjects(context, injectionPoint);
            injectionPoint.processObjects(context, objectsToInject);
        }
    }

    protected abstract boolean isApplicable(ComponentInjectionPoint<?> injectionPoint);

    private List<Object> resolveInjectedObjects(PopulateComponentContext<?> context, ComponentInjectionPoint<?> injectionPoint) {
        Set<InjectionPoint> injectionPoints = injectionPoint.injectionPoints();

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

    protected abstract Object resolveInjectedObject(InjectionPoint injectionPoint, PopulateComponentContext<?> context) throws ApplicationException, ApplicationRuntimeException;

    private boolean shouldRequire(InjectionPoint injectionPoint) {
        return this.requiresComponentRules.stream()
                .map(rule -> rule.isRequired(injectionPoint))
                .reduce(true, Boolean::logicalAnd);
    }
}
