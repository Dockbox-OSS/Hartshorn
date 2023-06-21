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

package org.dockbox.hartshorn.web.servlet;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;

@Service
@RequiresActivator(UseHttpServer.class)
public class StandardWebServletFactory implements WebServletFactory {

    @Override
    public WebServlet webServlet(final HttpWebServer starter, final RequestHandlerContext context) {
        return new WebServletImpl(starter, context);
    }
}