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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;

/**
 * Simplified view of a {@link ComponentKey}. This view can be used to compare keys, or to use as a key in a
 * map. Essentially, a view represents a component key without its dynamic properties, such as its scope and
 * provider strategy. Scopes are not included in the view, as they are not part of the key's identity, and
 * are often only used to select an appropriate {@link ComponentProvider}.
 *
 * @param type The fully parameterized type of the component key
 * @param qualifier The qualifier of the component key
 *
 * @param <T> the type of the component
 *
 * @see ComponentKey#view()
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public record ComponentKeyView<T>(
        ParameterizableType type,
        CompositeQualifier qualifier
) {

    public ComponentKeyView(ComponentKey<T> key) {
        this(key.parameterizedType(), key.qualifier());
    }

    /**
     * Returns the name of the component. If the component has no name, {@code null} is returned.
     *
     * @return the name of the component, or {@code null} if the component has no name
     *
     * @deprecated explicit names have been replaced with qualifiers. Use {@link #qualifier()} instead.
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
    public String name() {
        return null;
    }

    /**
     * Returns whether this view matches the given key. A view matches if both the type and qualifiers are
     * equal.
     *
     * @param componentKey the key to match
     * @return whether this view matches the given key
     */
    public boolean matches(ComponentKey<?> componentKey) {
        return this.matches(componentKey.view());
    }

    /**
     * Returns whether this view matches the given view. A view matches if both the type and qualifiers are
     * equal.
     *
     * @param componentKeyView the view to match
     * @return whether this view matches the given view
     */
    public boolean matches(ComponentKeyView<?> componentKeyView) {
        return this.equals(componentKeyView);
    }
}
