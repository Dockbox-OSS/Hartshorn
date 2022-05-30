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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.util.parameter.ParameterLoader;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

import jakarta.inject.Inject;
import jakarta.inject.Named;

public abstract class DefaultHttpWebServer implements HttpWebServer {

    @Inject
    @Named("http_webserver")
    private ParameterLoader<HttpRequestParameterLoaderContext> loader;

    @Override
    public ParameterLoader<HttpRequestParameterLoaderContext> loader() {
        return this.loader;
    }
}
