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

import org.dockbox.hartshorn.context.CarrierContext;
import org.dockbox.hartshorn.util.Result;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestError extends CarrierContext {

    PrintWriter writer();
    HttpServletRequest request();
    HttpServletResponse response();

    int statusCode();
    String message();
    Result<Throwable> cause();
    boolean yieldDefaults();

    RequestError message(String message);
    RequestError yieldDefaults(boolean yieldDefaults);
}
