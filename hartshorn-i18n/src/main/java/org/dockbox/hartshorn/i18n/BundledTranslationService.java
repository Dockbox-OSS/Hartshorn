/*
 * Copyright 2019-2022 the original author or authors.
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
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.util.Exceptional;

import javax.inject.Inject;

@Component
public class BundledTranslationService implements TranslationService {

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private TranslationBundle bundle;

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public TranslationBundle bundle() {
        return this.bundle;
    }

    @Override
    public Exceptional<Message> get(final String key) {
        return this.bundle.message(this.clean(key));
    }

    @Override
    public Message getOrCreate(final String key, final String value) {
        return this.bundle.message(this.clean(key))
                .orElse(() -> this.bundle.register(key, value))
                .get();
    }

    @Override
    public void add(final TranslationBundle bundle) {
        this.bundle = this.bundle.merge(bundle);
    }

    @Override
    public void add(final Message message) {
        this.bundle.register(message);
    }

    private String clean(final String key) {
        return key.replaceAll("[/\\\\_-]", ".");
    }
}
