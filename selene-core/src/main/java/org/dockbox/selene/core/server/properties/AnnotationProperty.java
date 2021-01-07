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

package org.dockbox.selene.core.server.properties;

import java.lang.annotation.Annotation;

public final class AnnotationProperty<T extends Annotation> implements InjectorProperty<Class<T>> {

    public static final String KEY = "SeleneInternalAnnotationProperty";
    private final Class<T> annotationType;

    private AnnotationProperty(Class<T> annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Class<T> getObject() {
        return this.annotationType;
    }

    public static <A extends Annotation> AnnotationProperty<A> of(Class<A> annotation) {
        return new AnnotationProperty<>(annotation);
    }
}
