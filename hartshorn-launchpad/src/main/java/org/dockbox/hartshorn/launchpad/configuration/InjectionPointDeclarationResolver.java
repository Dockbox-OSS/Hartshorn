/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.targets.InjectionPoint;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * Functional interface for resolving the declaration type of an injection point.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public interface InjectionPointDeclarationResolver {

    /**
     * Resolves the declaration type of the given injection point.
     *
     * @param injectionPoint the injection point to resolve
     *
     * @return the type view representing the declaration type of the injection point
     */
    TypeView<?> resolve(InjectionPoint injectionPoint);
}
