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

module Hartshorn.hartshorn.web.main {
    requires Hartshorn.hartshorn.config.main;
    requires Hartshorn.hartshorn.core.main;
    requires Hartshorn.hartshorn.events.main;
    requires Hartshorn.hartshorn.persistence.main;
    requires org.eclipse.jetty.http;
    requires org.eclipse.jetty.io;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;
    requires jakarta.servlet;
    requires jakarta.inject;
    requires lombok;
    requires org.eclipse.jetty.servlet;
    requires javax.servlet.api;

    exports org.dockbox.hartshorn.web.annotations;
    exports org.dockbox.hartshorn.web.annotations.http;
}
