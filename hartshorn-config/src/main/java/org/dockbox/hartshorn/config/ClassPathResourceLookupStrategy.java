/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.persistence.FileType;

import java.net.URI;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassPathResourceLookupStrategy implements ResourceLookupStrategy {

    private static final Pattern PATTERN = Pattern.compile("\\$cp\\{(.+)}");

    @Override
    public boolean accepts(ApplicationContext context, String path, TypeContext<?> owner) {
        return PATTERN.asMatchPredicate().test(path);
    }

    @Override
    public Exceptional<URI> lookup(ApplicationContext context, String path, TypeContext<?> owner, FileType fileType) {
        return Exceptional.of(() -> {
            Matcher matcher = PATTERN.matcher(path);
            if (matcher.find()) {
                String resource = fileType.asFileName(matcher.group(1));
                return Hartshorn.resource(resource).map(Path::toUri).orNull();
            }
            return null;
        });
    }
}
