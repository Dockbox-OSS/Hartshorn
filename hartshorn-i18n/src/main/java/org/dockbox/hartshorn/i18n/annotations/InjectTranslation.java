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

package org.dockbox.hartshorn.i18n.annotations;

import org.dockbox.hartshorn.i18n.services.TranslationKeyGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method decorator to indicate the method should return the current translation
 * for the given {@link #key()}. If no key is provided, the method name will be
 * used to generate a key using the active {@link TranslationKeyGenerator}.
 *
 * <p>Methods should return {@link org.dockbox.hartshorn.i18n.Message}, or a type
 * convertable from {@link org.dockbox.hartshorn.i18n.Message}. Conversion is
 * performed using the {@link org.dockbox.hartshorn.util.introspect.convert.ConversionService},
 * if no converter is capable of converting the type, an exception will be thrown.
 *
 * @see TranslationKeyGenerator
 * @see org.dockbox.hartshorn.i18n.Message
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InjectTranslation {

    /**
     * The default value of the message. This is used as the default value of the message
     * if no translation is found for the current language.
     *
     * @return the default value of the message
     */
    String defaultValue();

    /**
     * The key of the message. If no key is provided, the method name will be used to
     * generate a key using the active {@link TranslationKeyGenerator}.
     *
     * @return the key of the message
     */
    String key() default "";
}
