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

package org.dockbox.hartshorn.util.introspect.view;

import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;

/**
 * Represents a view of an annotated element, such as a field or method. This view can be
 * used to introspect the element's annotations, as well as its name and qualified name.
 *
 * @author Guus Lieben
 * @since 22.5
 */
public interface AnnotatedElementView extends View {

    /**
     * Returns an {@link ElementAnnotationsIntrospector} for the element. This introspector
     * can be used to introspect the element's annotations.
     *
     * @return an introspector for the element's annotations
     */
    ElementAnnotationsIntrospector annotations();

    /**
     * Returns the simple name of the element. For example, if the element is a field, this
     * method will return the field's name, without further qualification.
     *
     * @return the simple name of the element
     */
    String name();

    /**
     * Returns the qualified name of the element. For example, if the element is a field,
     * this method will return the field's name, qualified by the declaring class.
     *
     * @return the qualified name of the element
     */
    String qualifiedName();

}
