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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.DefaultCarrierContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
public class RequestErrorImpl extends DefaultCarrierContext implements RequestError {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final int statusCode;
    private final PrintWriter writer;
    private final Exceptional<Throwable> cause;
    @Setter private String message;
    @Setter private boolean yieldDefaults;

    public RequestErrorImpl(final ApplicationContext applicationContext, final HttpServletRequest request, final HttpServletResponse response, final int statusCode, final PrintWriter writer, final String message, final Throwable cause) {
        super(applicationContext);
        this.request = request;
        this.response = response;
        this.statusCode = statusCode;
        this.writer = writer;
        this.message = message;
        this.cause = Exceptional.of(cause, cause);
    }
}
