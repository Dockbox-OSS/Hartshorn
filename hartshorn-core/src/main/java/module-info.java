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

module Hartshorn.hartshorn.core.main {
    requires lombok;
    requires org.slf4j;
    requires org.jetbrains.annotations;
    requires javassist;
    requires log4j.api;
    requires log4j.core;

    exports org.dockbox.hartshorn.core;
    exports org.dockbox.hartshorn.core.adapter;
    exports org.dockbox.hartshorn.core.annotations;
    exports org.dockbox.hartshorn.core.annotations.activate;
    exports org.dockbox.hartshorn.core.annotations.component;
    exports org.dockbox.hartshorn.core.annotations.context;
    exports org.dockbox.hartshorn.core.annotations.inject;
    exports org.dockbox.hartshorn.core.annotations.service;
    exports org.dockbox.hartshorn.core.binding;
    exports org.dockbox.hartshorn.core.boot;
    exports org.dockbox.hartshorn.core.context;
    exports org.dockbox.hartshorn.core.context.element;
    exports org.dockbox.hartshorn.core.domain;
    exports org.dockbox.hartshorn.core.domain.tuple;
    exports org.dockbox.hartshorn.core.exceptions;
    exports org.dockbox.hartshorn.core.function;
    exports org.dockbox.hartshorn.core.inject;
    exports org.dockbox.hartshorn.core.keys;
    exports org.dockbox.hartshorn.core.proxy;
    exports org.dockbox.hartshorn.core.services;
    exports org.dockbox.hartshorn.core.services.parameter;
}
