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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.data.mapping.JsonInclusionRule;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

import java.net.URI;

import javax.inject.Singleton;
import javax.servlet.Servlet;

@Singleton
public interface HttpWebServer {

    public static String WEB_INF = "/WEB-INF/";
    public static String STATIC_CONTENT = WEB_INF + "static/";

    void start(int port) throws ApplicationException;

    HttpWebServer register(Servlet servlet, String pathSpec);

    ParameterLoader<HttpRequestParameterLoaderContext> loader();

    HttpWebServer skipBehavior(JsonInclusionRule modifier);

    JsonInclusionRule skipBehavior();

    HttpWebServer listStaticDirectories(boolean listDirectories);

    HttpWebServer staticContent(URI location);

    void stop() throws ApplicationException;
}
