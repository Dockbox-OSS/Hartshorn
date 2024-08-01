/*
 * Copyright 2019-2024 the original author or authors.
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
import java.util.SequencedCollection;
import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentRequiredException;
import org.dockbox.hartshorn.component.ComponentResolutionException;
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
 * <p>The exact implementation used to resolve individual objects is left to the implementing class. If the
 * implementation yields {@code null}, and the injection point is indicated to be required by the active
 * {@link RequireInjectionPointRule}s, then a {@link ComponentRequiredException} will be thrown.
 *
 * @see RequireInjectionPointRule
 * @see ComponentRequiredException
 * @see ComponentPopulationStrategy
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
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

    /**
     * Resolves the objects to inject into the given injection point. This is done by resolving the objects to
     * inject for each of the {@link InjectionPoint injection points} that are returned by the
     * {@link ComponentInjectionPoint#injectionPoints()} method.
     *
     * <p>If any of the objects to inject cannot be resolved (due to being null, or because of an exception thrown
     * while resolving), and the injection point is indicated to be required by the active {@link RequireInjectionPointRule}s,
     * then a {@link ComponentRequiredException} will be thrown. Unresolved objects that are not required will be ignored.
     *
     * @param context the context that provides the component instance
     * @param injectionPoint the injection point to resolve the objects to inject for
     * @return a list of objects to inject into the injection point
     */
    protected List<Object> resolveInjectedObjects(PopulateComponentContext<?> context, ComponentInjectionPoint<?> injectionPoint) {
        SequencedCollection<InjectionPoint> injectionPoints = injectionPoint.injectionPoints();

        List<Object> objectsToInject = new ArrayList<>();
        for(InjectionPoint point : injectionPoints) {
            Object object;
            try {
                object = this.resolveInjectedObject(point, context);
            }
            catch(ApplicationException | ApplicationRuntimeException e) {
                if (this.shouldRequire(point)) {
                    throw new ComponentResolutionException("Could not resolve value for injection point " + point.injectionPoint().qualifiedName(), e);
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

    /**
     * Indicates whether this strategy is applicable to the given injection point. If {@code true}, the strategy
     * may be used to resolve the objects to inject into the injection point.
     *
     * @param injectionPoint the injection point to check
     * @return {@code true} if this strategy is applicable to the given injection point, {@code false} otherwise
     */
    protected abstract boolean isApplicable(ComponentInjectionPoint<?> injectionPoint);

    /**
     * Resolves the object to inject for the given injection point. If the object cannot be resolved, {@code null}
     * may be returned, or an exception may be thrown.
     *
     * @param injectionPoint the injection point to resolve the object for
     * @param context the context that provides the component instance
     * @return the object to inject, or {@code null} if the object cannot be resolved
     * @throws ApplicationException if the object cannot be resolved
     * @throws ApplicationRuntimeException if the object cannot be resolved
     */
    protected abstract Object resolveInjectedObject(InjectionPoint injectionPoint, PopulateComponentContext<?> context) throws ApplicationException, ApplicationRuntimeException;

    /**
     * Indicates whether the given injection point should be required. This is determined by the active
     * {@link RequireInjectionPointRule}s.
     *
     * @param injectionPoint the injection point to check
     * @return {@code true} if the injection point should be required, {@code false} otherwise
     */
    protected boolean shouldRequire(InjectionPoint injectionPoint) {
        return this.requiresComponentRules.stream()
                .map(rule -> rule.isRequired(injectionPoint))
                .reduce(true, Boolean::logicalAnd);
    }
}
