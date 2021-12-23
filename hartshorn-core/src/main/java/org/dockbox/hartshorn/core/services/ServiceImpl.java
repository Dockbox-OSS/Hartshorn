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

package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.annotations.stereotype.Service;

import java.lang.annotation.Annotation;

public class ServiceImpl implements Service {
    @Override
    public String id() {
        return "";
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public Class<?> owner() {
        return Service.class;
    }

    @Override
    public boolean singleton() {
        return false;
    }

    @Override
    public Class<? extends Annotation>[] activators() {
        return new Class[0];
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Service.class;
    }
}
