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

package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.runtime.Phase;

/**
 * Represents an error that occurred during the evaluation of a script. This error can occur during
 * any {@link Phase phase} of the evaluation process, and will include as much information as possible
 * about the location of the error.
 *
 * @see Phase
 * @see ASTNode
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ScriptEvaluationError extends RuntimeException {

    private final Phase phase;
    private final int line;
    private final int column;
    private final ASTNode at;

    public ScriptEvaluationError(String message, Phase phase, int line, int column) {
        this(null, message, phase, null, line, column);
    }

    public ScriptEvaluationError(String message, Phase phase, ASTNode at) {
        this(null, message, phase, at, at.line(), at.column());
    }

    public ScriptEvaluationError(Throwable cause, Phase phase, ASTNode at) {
        this(cause, cause.getMessage(), phase, at);
    }

    public ScriptEvaluationError(Throwable cause, String message, Phase phase, ASTNode at) {
        this(cause, message, phase, at, at.line(), at.column());
    }

    public ScriptEvaluationError(Throwable cause, String message, Phase phase, ASTNode at, int line, int column) {
        super(message, cause);
        this.phase = phase;
        this.at = at;
        this.line = line;
        this.column = column;
    }

    /**
     * Returns the node at which the error occurred, if available.
     *
     * @return the node at which the error occurred, or {@code null} if not available
     */
    public ASTNode at() {
        return this.at;
    }

    /**
     * Returns the line at which the error occurred. This is the line number in the script where the
     * error occurred, or -1 if the error occurred in a virtual node.
     *
     * @return the line at which the error occurred
     */
    public int line() {
        return this.line;
    }

    /**
     * Returns the column at which the error occurred. This is the column number in the script where
     * the error occurred, or -1 if the error occurred in a virtual node.
     *
     * @return the column at which the error occurred
     */
    public int column() {
        return this.column;
    }

    /**
     * Returns the phase at which the error occurred.
     *
     * @return the phase at which the error occurred
     */
    public Phase phase() {
        return this.phase;
    }
}
