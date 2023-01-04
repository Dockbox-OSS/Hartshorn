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

package org.dockbox.hartshorn.web.annotations.http;

import org.dockbox.hartshorn.util.introspect.annotations.Extends;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Extends(HttpRequest.class)
@HttpRequest(method = HttpMethod.GET, value = "")
public @interface HttpGet {
    String value();
    MediaType produces() default MediaType.APPLICATION_JSON;
    MediaType consumes() default MediaType.APPLICATION_JSON;
}
