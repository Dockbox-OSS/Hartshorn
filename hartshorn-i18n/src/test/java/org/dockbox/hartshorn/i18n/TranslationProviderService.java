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

import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.i18n.annotations.TranslationProvider;

import java.nio.file.Path;
import java.util.Locale;

@Service
public class TranslationProviderService {

    @TranslationProvider
    public TranslationBundle dutch(final ApplicationContext context) {
        final TranslationBundle bundle = context.get(TranslationBundle.class);
        bundle.primaryLanguage(new Locale("nl", "NL"));
        bundle.register("lang.name", "Nederlands");
        return bundle;
    }

    @TranslationProvider
    public TranslationBundle english(final ApplicationContext context) {
        final TranslationBundle bundle = context.get(TranslationBundle.class);
        final Path path = context.get(ClasspathResourceLocator.class).resource("i18n/en_us.yml").get();
        bundle.register(path, Locale.US, FileFormats.YAML);
        return bundle;
    }
}
