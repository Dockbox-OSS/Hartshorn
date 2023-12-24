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

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;
import org.dockbox.hartshorn.i18n.services.SimpleTranslationKeyGenerator;
import org.dockbox.hartshorn.i18n.services.TranslationKeyGenerator;

import jakarta.inject.Singleton;

/**
 * Default providers for internationalization services.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseTranslations.class)
public class TranslationConfiguration {

    @Binds
    @Singleton
    public TranslationService translationService(ApplicationContext applicationContext, TranslationBundle bundle) {
        return new BundledTranslationService(applicationContext, bundle);
    }

    @Binds
    @Singleton
    public TranslationBundle translationBundle(ApplicationContext applicationContext) {
        return new DefaultTranslationBundle(applicationContext);
    }

    @Binds
    @Singleton
    public TranslationKeyGenerator translationKeyGenerator(ComponentLocator componentLocator) {
        return new SimpleTranslationKeyGenerator(componentLocator);
    }
}
