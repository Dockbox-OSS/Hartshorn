/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.inject.targets;

import org.dockbox.hartshorn.inject.InjectorConfiguration;
import org.dockbox.hartshorn.inject.annotations.Required;

/**
 * A rule that determines whether an {@link InjectionPoint} is required to be present. This uses the
 * {@link Required} annotation to determine whether the injection point is required.
 *
 * @author Guus Lieben
 * @see Required
 * @since 0.6.0
 */
public class AnnotatedInjectionPointRequireRule implements RequireInjectionPointRule {

    private final InjectorConfiguration injectorConfiguration;

    public AnnotatedInjectionPointRequireRule(InjectorConfiguration injectorConfiguration) {
        this.injectorConfiguration = injectorConfiguration;
    }

    @Override
    public boolean isRequired(InjectionPoint injectionPoint) {
        return injectionPoint.injectionPoint().annotations().get(Required.class)
                .map(Required::value)
                .orCompute(this.injectorConfiguration::requiredByDefault)
                .test(Boolean::booleanValue);
    }
}
