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
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A simple implementation of the {@link InjectionPointDeclarationResolver} that resolves the declaration type
 * of an injection point based on its specific type (executable element, field, or parameter).
 *
 * @since 0.7.0
 * @author Guus Lieben
 */
public class SimpleInjectionPointDeclarationResolver implements InjectionPointDeclarationResolver {

    @Override
    public TypeView<?> resolve(InjectionPoint injectionPoint) {
        return switch(injectionPoint.injectionPoint()) {
            case ExecutableElementView<?> executableElementView -> executableElementView.declaredBy();
            case FieldView<?, ?> fieldView -> fieldView.declaredBy();
            case ParameterView<?> parameterView -> parameterView.declaredBy().declaredBy();
            default -> throw new IllegalStateException("Unexpected value: " + injectionPoint.injectionPoint());
        };
    }
}
