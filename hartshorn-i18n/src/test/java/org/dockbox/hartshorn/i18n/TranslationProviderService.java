/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.annotations.TranslationProvider;
import org.dockbox.hartshorn.persistence.FileType;

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
        final Path path = Hartshorn.resource("i18n/en_us.yml").get();
        bundle.register(path, Locale.US, FileType.YAML);
        return bundle;
    }
}
