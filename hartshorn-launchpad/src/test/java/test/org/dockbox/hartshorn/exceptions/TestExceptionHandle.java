/*
 * Copyright 2019-2024 the original author or authors.
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

package test.org.dockbox.hartshorn.exceptions;

import org.dockbox.hartshorn.inject.LoggingExceptionHandler;

public class TestExceptionHandle extends LoggingExceptionHandler {

    private boolean stacktrace;
    private String message;
    private Throwable exception;

    @Override
    public void handle(String message, Throwable throwable) {
        this.message = message;
        this.exception = throwable;
    }

    @Override
    public TestExceptionHandle printStackTraces(boolean stacktraces) {
        this.stacktrace = stacktraces;
        return this;
    }

    public void reset() {
        this.message = null;
        this.exception = null;
        this.stacktrace = false;
    }

    public boolean stacktrace() {
        return this.stacktrace;
    }

    public String message() {
        return this.message;
    }

    public Throwable exception() {
        return this.exception;
    }
}
