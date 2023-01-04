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

package test.org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.application.context.ApplicationContext;
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
