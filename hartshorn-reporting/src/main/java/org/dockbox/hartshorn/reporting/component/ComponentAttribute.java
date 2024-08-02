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

package org.dockbox.hartshorn.reporting.component;

import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.inject.annotations.Service;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;

/**
 * The attributes by which components can be grouped.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum ComponentAttribute {
    /**
     * Group components by their component stereotype, for example {@link Component},
     * {@link Configuration}, or {@link Service}.
     */
    STEREOTYPE,
    /**
     * Group components by their package.
     */
    PACKAGE,
    /**
     * Do not explicitly group components, but report them all in a single group.
     */
    NONE,
}
