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

package org.dockbox.hartshorn.i18n.services;

import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import jakarta.inject.Inject;

public class SimpleTranslationKeyGenerator implements TranslationKeyGenerator {

    private final ComponentLocator componentLocator;

    @Inject
    public SimpleTranslationKeyGenerator(ComponentLocator componentLocator) {
        this.componentLocator = componentLocator;
    }

    @Override
    public String key(TypeView<?> type, MethodView<?, ?> method) {
        Option<InjectTranslation> resource = method.annotations().get(InjectTranslation.class);

        // If the method has an explicit key, use that without further processing
        if (resource.present()) {
            String resourceKey = resource.get().key();
            if (StringUtilities.notEmpty(resourceKey)) {
                return resourceKey;
            }
        }

        String methodName = method.name();
        if (methodName.startsWith("get")) {
            methodName = methodName.substring(3);
        }
        String methodKey = String.join(".", StringUtilities.splitCapitals(methodName)).toLowerCase();

        TypeView<?> declaringType = method.declaredBy();
        Option<ComponentContainer<?>> container = this.componentLocator.container(declaringType.type());
        if (container.present()) {
            String containerKey = container.get().id();
            if (containerKey != null) {
                methodKey = containerKey + "." + methodKey;
            }
        }

        return methodKey;
    }
}
