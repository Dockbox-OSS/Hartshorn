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
import org.dockbox.hartshorn.util.option.Option;

/**
 * Default implementation of {@link TranslationService}. By default, this uses the
 * provided {@link TranslationBundle} as its source. Any additional bundles that are
 * {@link #add(TranslationBundle) added} are merged according to the rules of the
 * {@link TranslationBundle#merge(TranslationBundle)} method.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public class BundledTranslationService implements TranslationService {

    private final ApplicationContext applicationContext;
    private TranslationBundle bundle;

    public BundledTranslationService(ApplicationContext applicationContext, TranslationBundle bundle) {
        this.applicationContext = applicationContext;
        this.bundle = bundle;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public TranslationBundle bundle() {
        return this.bundle;
    }

    @Override
    public Option<Message> get(String key) {
        return this.bundle.message(this.clean(key));
    }

    @Override
    public Message getOrCreate(String key, String value) {
        return this.bundle.message(this.clean(key))
                .orElseGet(() -> this.bundle.register(key, value));
    }

    @Override
    public void add(TranslationBundle bundle) {
        this.bundle = this.bundle.merge(bundle);
    }

    @Override
    public void add(Message message) {
        this.bundle.register(message);
    }

    /**
     * Sanitizes the provided key, by replacing all occurrences of {@code /}, {@code \},
     * {@code _} and {@code -} with {@code .}.
     *
     * @param key the key to sanitize
     * @return the sanitized key
     */
    private String clean(String key) {
        return key.replaceAll("[/\\\\_-]", ".");
    }
}
