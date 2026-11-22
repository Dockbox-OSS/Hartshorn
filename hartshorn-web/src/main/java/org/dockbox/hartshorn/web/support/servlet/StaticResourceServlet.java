/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web.support.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.launchpad.resources.ResourceLookup;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.option.Option;

import java.io.IOException;
import java.net.URI;

/**
 * A servlet that serves static resources from the given {@link ResourceLookup}. All requested
 * resources are resolved through the resource lookup, prefixed with the given
 * {@code staticLocation}. For example, with static location {@code static/} a resource
 * {@code hello.txt} will be resolved as {@code static/hello.txt}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class StaticResourceServlet extends HttpServlet implements Reportable {

    private final ResourceLookup resourceLookup;
    private final String staticLocation;

    public StaticResourceServlet(ResourceLookup resourceLookup, String staticLocation) {
        this.resourceLookup = resourceLookup;
        this.staticLocation = StringUtilities.trimWith('/', staticLocation);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String resourcePath = req.getPathInfo();
        if (resourcePath == null || resourcePath.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Option<URI> resourceUri =
            this.resourceLookup.lookup(this.staticLocation + resourcePath).stream()
                .collect(Option.collector());

        if (resourceUri.absent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        else {
            resp.setContentType(this.getServletContext().getMimeType(resourcePath));
            resourceUri.get().toURL().openStream().transferTo(resp.getOutputStream());
        }
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("staticLocation").writeString(this.staticLocation);
    }
}
