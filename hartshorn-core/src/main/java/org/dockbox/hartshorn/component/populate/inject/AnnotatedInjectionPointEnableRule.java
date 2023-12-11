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

import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A rule that determines whether an {@link InjectionPoint} should be enabled. This uses the {@link Enable}
 * annotation to determine whether the injection point should be enabled.
 *
 * @see Enable
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class AnnotatedInjectionPointEnableRule implements EnableInjectionPointRule {

    @Override
    public boolean shouldEnable(InjectionPoint injectionPoint) {
        Option<Enable> enableAnnotation = injectionPoint.injectionPoint().annotations().get(Enable.class);
        return enableAnnotation.absent() || enableAnnotation.get().value();
    }
}