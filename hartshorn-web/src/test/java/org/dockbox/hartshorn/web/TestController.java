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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.web.annotations.RequestBody;
import org.dockbox.hartshorn.web.annotations.RequestHeader;
import org.dockbox.hartshorn.web.annotations.RestController;
import org.dockbox.hartshorn.web.annotations.http.HttpGet;
import org.dockbox.hartshorn.web.annotations.http.HttpPost;

@RestController
public class TestController {

    @HttpGet("/get")
    public String get() {
        return "JUnit GET";
    }

    @HttpPost("/post")
    public String post(@RequestBody final String body) {
        return body;
    }

    @HttpGet("/header")
    public String header(@RequestHeader("http-demo") final String httpDemo) {
        return httpDemo;
    }

    @HttpGet("/inject")
    public boolean inject(final ApplicationContext context) {
        return context != null;
    }
}
