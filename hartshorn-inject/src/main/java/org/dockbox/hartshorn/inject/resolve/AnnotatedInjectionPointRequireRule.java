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

package org.dockbox.hartshorn.inject.resolve;

import org.dockbox.hartshorn.inject.annotations.Required;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A rule that determines whether an {@link InjectionPoint} is required to be present. This uses
 * the {@link Required} annotation to determine whether the injection point is required.
 *
 * @see Required
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class AnnotatedInjectionPointRequireRule implements RequireInjectionPointRule {

    @Override
    public boolean isRequired(InjectionPoint injectionPoint) {
        Option<Required> required = injectionPoint.injectionPoint().annotations().get(Required.class);
        return required.present() && required.get().value();
    }
}
