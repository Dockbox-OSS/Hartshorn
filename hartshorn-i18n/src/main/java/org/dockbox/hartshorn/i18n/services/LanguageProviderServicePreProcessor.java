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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationBundle;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.i18n.annotations.TranslationProvider;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.util.List;

public class LanguageProviderServicePreProcessor extends ComponentPreProcessor {

    @Override
    public <T> void process(final ApplicationContext context, final ComponentProcessingContext<T> processingContext) {
        final List<MethodView<T, ?>> translationProviderMethods = processingContext.type().methods().annotatedWith(TranslationProvider.class);
        if (!translationProviderMethods.isEmpty()) {
            final TranslationService translationService = context.get(TranslationService.class);
            for (final MethodView<T, ?> method : translationProviderMethods) {
                if (method.returnType().isChildOf(TranslationBundle.class)) {
                    final TranslationBundle bundle = (TranslationBundle) method.invokeWithContext().rethrowUnchecked().get();
                    translationService.add(bundle);
                }
                else if (method.returnType().isChildOf(Message.class)) {
                    final Message message = (Message) method.invokeWithContext().rethrowUnchecked().get();
                    translationService.add(message);
                }
            }
        }
    }
}
