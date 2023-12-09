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

package org.dockbox.hartshorn.component.populate.inject;

/**
 * A rule that determines whether an {@link InjectionPoint} should be enabled.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface EnableInjectionPointRule {

    /**
     * Indicates whether the given {@link InjectionPoint} should be enabled.
     *
     * @param injectionPoint the injection point to check
     * @return {@code true} if the injection point should be enabled, {@code false} otherwise
     */
    boolean shouldEnable(InjectionPoint injectionPoint);

}
