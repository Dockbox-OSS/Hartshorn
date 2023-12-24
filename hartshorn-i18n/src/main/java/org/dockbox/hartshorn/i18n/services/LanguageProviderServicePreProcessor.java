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

package org.dockbox.hartshorn.i18n.services;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationBundle;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.i18n.annotations.TranslationProvider;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.util.List;

/**
 * A pre-processor that processes {@link TranslationProvider} annotations on components. This will register
 * the result of the annotated method as a {@link TranslationBundle} or {@link Message} with the
 * {@link TranslationService}. If the annotated method returns another type, an exception is thrown.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
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

                switch (value) {
                    case TranslationBundle bundle -> translationService.add(bundle);
                    case Message message -> translationService.add(message);
                    case null -> throw new IllegalStateException(
                            "Translation provider method " + method + " returned null. " +
                                    "Expected " + TranslationBundle.class.getSimpleName() + " or " + Message.class.getSimpleName());
                    default -> throw new IllegalStateException(
                            "Translation provider method " + method + " returned an invalid value. " +
                                    "Expected " + TranslationBundle.class.getSimpleName() + " or " + Message.class.getSimpleName() + ", " +
                                    "got " + value.getClass().getSimpleName());
                }
            }
        }
    }

    @Override
    public int priority() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
