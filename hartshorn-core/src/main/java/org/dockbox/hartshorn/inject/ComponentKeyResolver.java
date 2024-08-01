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

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedGenericTypeView;

/**
 * A resolver that determines the {@link ComponentKey} of a component. This is commonly used to determine the
 * {@link ComponentKey} of a binding declaration or injection point.
 *
 * @see ComponentKey
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface ComponentKeyResolver {

    /**
     * Resolves the {@link ComponentKey} of the given view. The key will be scoped to the default scope, which
     * is typically the application scope.
     *
     * @param view the view to resolve the key for
     * @return the key of the given view
     */
    default ComponentKey<?> resolve(AnnotatedGenericTypeView<?> view) {
        return this.resolve(view, null);
    }

    /**
     * Resolves the {@link ComponentKey} of the given view. The key will be scoped to the given scope.
     *
     * @param view the view to resolve the key for
     * @param scope the scope of the key
     * @return the key of the given view
     */
    ComponentKey<?> resolve(AnnotatedGenericTypeView<?> view, Scope scope);
}
