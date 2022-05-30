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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.DefaultCarrierContext;
import org.dockbox.hartshorn.util.Result;

import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestErrorImpl extends DefaultCarrierContext implements RequestError {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final int statusCode;
    private final PrintWriter writer;
    private final Result<Throwable> cause;
    private String message;
    private boolean yieldDefaults;

    public RequestErrorImpl(final ApplicationContext applicationContext, final HttpServletRequest request, final HttpServletResponse response, final int statusCode, final PrintWriter writer, final String message, final Throwable cause) {
        super(applicationContext);
        this.request = request;
        this.response = response;
        this.statusCode = statusCode;
        this.writer = writer;
        this.message = message;
        this.cause = Result.of(cause, cause);
    }

    @Override
    public RequestErrorImpl message(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public RequestErrorImpl yieldDefaults(final boolean yieldDefaults) {
        this.yieldDefaults = yieldDefaults;
        return this;
    }

    @Override
    public HttpServletRequest request() {
        return this.request;
    }

    @Override
    public HttpServletResponse response() {
        return this.response;
    }

    @Override
    public int statusCode() {
        return this.statusCode;
    }

    @Override
    public PrintWriter writer() {
        return this.writer;
    }

    @Override
    public Result<Throwable> cause() {
        return this.cause;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public boolean yieldDefaults() {
        return this.yieldDefaults;
    }
}
