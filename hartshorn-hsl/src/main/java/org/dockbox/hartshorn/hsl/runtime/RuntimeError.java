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

package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.ast.ASTNode;

/**
 * Represents any unspecified error which may occur during the execution of
 * a script.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class RuntimeError extends RuntimeException {

    private final ASTNode at;

    public RuntimeError(final ASTNode at, final DiagnosticMessage index, final Object... args) {
        super(index.format(args));
        this.at = at;
    }

    public RuntimeError(final ASTNode at, final Throwable cause) {
        super(cause.getMessage(), cause);
        this.at = at;
    }

    /**
     * The token at which the error occurred.
     * @return The token at which the error occurred.
     */
    public ASTNode at() {
        return this.at;
    }
}
