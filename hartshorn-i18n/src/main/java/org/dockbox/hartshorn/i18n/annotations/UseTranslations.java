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

package org.dockbox.hartshorn.i18n.annotations;

import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.i18n.services.LanguageProviderServicePreProcessor;
import org.dockbox.hartshorn.i18n.services.TranslationInjectPostProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Activator annotation to indicate that internationalization should be enabled.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ServiceActivator(processors = {
        LanguageProviderServicePreProcessor.class,
        TranslationInjectPostProcessor.class,
})
@UseConfigurations
public @interface UseTranslations {
}
