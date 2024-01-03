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

import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedGenericTypeView;

import java.util.List;
import java.util.SequencedCollection;

/**
 * Represents a single injection point for a component. This represents an element that can be injected into inside
 * a component. Common examples of injection points are fields and methods.
 *
 * @param <T> the type of the component that this injection point is for
 *
 * @see InjectionPoint
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface ComponentInjectionPoint<T> {

    /**
     * Processes the given objects to inject into the component instance that is provided by the given context.
     * The given objects to inject are guaranteed to be compatible with the injection point, as they are resolved
     * based on the {@link #injectionPoints()} of this injection point.
     *
     * @param context the context that provides the component instance
     * @param objectsToInject the objects to inject into the component instance
     */
    void processObjects(PopulateComponentContext<T> context, List<Object> objectsToInject) throws ApplicationException;

    /**
     * Returns an {@link ElementAnnotationsIntrospector} that can be used to introspect the annotations of the
     * injection point. This allows for the retrieval of additional metadata that is not available through the
     * {@link #injectionPoints()}.
     *
     * @return an introspector that can be used to introspect the annotations of the injection point
     */
    ElementAnnotationsIntrospector annotations();

    /**
     * Returns a collection of {@link InjectionPoint injection points} that are used to resolve the objects to
     * inject into the component instance. The objects to inject are guaranteed to be in the same order as is
     * required by the {@link #processObjects(PopulateComponentContext, List)} method.
     *
     * <p>The returned {@link InjectionPoint} may represent the current {@link ComponentInjectionPoint} itself,
     * or it may represent a nested injection point. For example, a method may injection point may return its
     * parameters as injection points.
     *
     * @return a collection of injection points that are used to resolve the objects to inject into the component
     *         instance
     */
    SequencedCollection<InjectionPoint> injectionPoints();

    /**
     * Returns the qualified name of the injection point. This is the name of the element that is being injected
     * into the component instance. For example, for a field, this would be the name of the field. For a method,
     * this would be the name of the method.
     *
     * @return the qualified name of the injection point
     */
    String qualifiedName();

    /**
     * Returns the declaration of the injection point. This is the element that is being injected into the component
     * instance.
     *
     * @return the declaration of the injection point
     */
    AnnotatedGenericTypeView<?> declaration();
}
