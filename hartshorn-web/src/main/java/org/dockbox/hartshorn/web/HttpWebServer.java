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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.config.IncludeRule;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoader;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

import java.net.URI;

import jakarta.inject.Singleton;
import jakarta.servlet.Servlet;

@Singleton
public interface HttpWebServer {

    String WEB_INF = "/WEB-INF/";
    String STATIC_CONTENT = WEB_INF + "static/";

    void start(int port) throws ApplicationException;

    HttpWebServer register(Servlet servlet, String pathSpec);

    ParameterLoader<HttpRequestParameterLoaderContext> loader();

    HttpWebServer skipBehavior(IncludeRule modifier);

    IncludeRule skipBehavior();

    HttpWebServer listStaticDirectories(boolean listDirectories);

    HttpWebServer staticContent(URI location);

    void stop() throws ApplicationException;

    int port();
}
