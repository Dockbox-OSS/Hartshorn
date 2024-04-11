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

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentRegistry;
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;
import org.dockbox.hartshorn.i18n.services.SimpleTranslationKeyGenerator;
import org.dockbox.hartshorn.i18n.services.TranslationKeyGenerator;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;

import jakarta.inject.Singleton;

/**
 * Sensible default configuration for internationalization services. The exposed {@link TranslationBundle}
 * may be used to configure application-level translations.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseTranslations.class)
public class TranslationConfiguration {

    /**
     * Configures the default translation service. The configured service delegates to the provided {@link
     * TranslationBundle} for translations. Any additional global translation bundles are not configured
     * at this level, but are expected to be already merged into a single bundle.
     *
     * @param applicationContext the application context
     * @param bundle the translation bundle to use
     * @return the translation service
     */
    @Binds
    @Singleton
    public TranslationService translationService(ApplicationContext applicationContext, TranslationBundle bundle) {
        return new BundledTranslationService(applicationContext, bundle);
    }

    /**
     * Configures the default translation bundle. The configured bundle merges any additional translation
     * bundles that are configured globally.
     *
     * @param objectMapper the object mapper that may be used to load additional resources from a resource
     * @param exceptionHandler the exception handler that may be used to handle exceptions that occur during resource loading
     * @param additionalBundles any additional translation bundles that are configured globally
     * @return the translation bundle
     */
    @Binds
    @Singleton
    public TranslationBundle translationBundle(
            ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
            ComponentCollection<TranslationBundle> additionalBundles
    ) {
        TranslationBundle translationBundle = new DefaultTranslationBundle(objectMapper, exceptionHandler);
        for(TranslationBundle bundle : additionalBundles) {
            translationBundle = translationBundle.merge(bundle);
        }
        return translationBundle;
    }

    /**
     * Configures the default translation key generator to allow generating translation keys for methods
     * which rely on implicit translation keys.
     *
     * @param componentRegistry the component registry to account for component IDs
     * @return the translation key generator
     */
    @Binds
    @Singleton
    public TranslationKeyGenerator translationKeyGenerator(ComponentRegistry componentRegistry) {
        return new SimpleTranslationKeyGenerator(componentRegistry);
    }
}
