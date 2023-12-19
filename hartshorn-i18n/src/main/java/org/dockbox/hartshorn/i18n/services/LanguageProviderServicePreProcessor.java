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

import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationBundle;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.i18n.annotations.TranslationProvider;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

public class LanguageProviderServicePreProcessor extends ComponentPreProcessor {

    @Override
    public <T> void process(ApplicationContext context, ComponentProcessingContext<T> processingContext) {
        List<MethodView<T, ?>> translationProviderMethods = processingContext.type().methods().annotatedWith(TranslationProvider.class);

        if (!translationProviderMethods.isEmpty()) {
            TranslationService translationService = context.get(TranslationService.class);
            ViewContextAdapter adapter = context.get(ViewContextAdapter.class);

            for (MethodView<T, ?> method : translationProviderMethods) {
                Object value;
                try {
                    value = adapter.invoke(method).orNull();
                }
                catch (Throwable throwable) {
                    throw new IllegalStateException("Failed to invoke translation provider method " + method, throwable);
                }

                if (value != null) {
                    if (value instanceof TranslationBundle bundle) {
                        translationService.add(bundle);
                    }
                    else if (value instanceof Message message) {
                        translationService.add(message);
                    }
                }
            }
        }
    }
}
