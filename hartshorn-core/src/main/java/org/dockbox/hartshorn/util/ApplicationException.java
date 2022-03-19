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

package org.dockbox.hartshorn.util;

/**
 * Thrown when the application runs a validated problem. Typically, this means an
 * exception was caught and is rethrown as a {@link ApplicationException} with the
 * original exception as cause.
 */
public class ApplicationException extends Exception {

    public ApplicationException(final String message) {
        super(message);
    }

    public ApplicationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(final Throwable cause) {
        super(cause);
    }

    /**
     * Attempts to look up the first cause of the exception. If no cause is found,
     * the exception itself is returned.
     *
     * @return The first cause of the exception.
     */
    public Throwable unwrap() {
        Throwable root = this;
        while (root.getCause() instanceof ApplicationException && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }
}
