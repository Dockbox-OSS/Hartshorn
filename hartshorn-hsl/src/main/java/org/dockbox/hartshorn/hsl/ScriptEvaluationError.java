/*
 * Copyright 2019-2025 the original author or authors.
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
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.hsl.runtime.Phase;

/**
 * Represents an error that occurred during the evaluation of a script. This error can occur during
 * any {@link Phase phase} of the evaluation process, and will include as much information as
 * possible about the location of the error.
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

    private ScriptEvaluationError(Builder builder) {
        super(builder.message, builder.cause);
        this.phase = builder.phase;
        this.at = builder.at;
        this.line = builder.line;
        this.column = builder.column;
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

    /**
     * Creates a new builder for a {@link ScriptEvaluationError} at the given phase.
     *
     * @param phase the phase at which the error occurred
     *
     * @return a new builder for a {@link ScriptEvaluationError}
     */
    public static Builder builder(Phase phase) {
        return new Builder(phase).virtualPosition();
    }

    /**
     * Builder for {@link ScriptEvaluationError}.
     *
     * @since 0.4.12
     *
     * @author Guus Lieben
     */
    public static class Builder {

        private final Phase phase;
        private int line;
        private int column;
        private ASTNode at;
        private String message;
        private Throwable cause;

        private Builder(Phase phase) {
            this.phase = phase;
        }

        /**
         * Sets the node at which the error occurred. If the node is not {@code null}, the line and
         * column will be set to the line and column of the node.
         *
         * @param at the node at which the error occurred
         *
         * @return this builder
         */
        public Builder at(ASTNode at) {
            this.at = at;
            if (at != null) {
                return this.position(at.line(), at.column());
            }
            return this;
        }

        /**
         * Sets the position of the error to be virtual, meaning that it does not have a specific
         * line or column.
         *
         * @return this builder
         */
        public Builder virtualPosition() {
            this.line = -1;
            this.column = -1;
            return this;
        }

        /**
         * Sets the position of the error.
         *
         * @param line the line number of the error
         * @param column the column number of the error
         *
         * @return this builder
         */
        public Builder position(int line, int column) {
            this.line = line;
            this.column = column;
            return this;
        }

        /**
         * Sets the message of the error.
         *
         * @param message the message of the error
         *
         * @return this builder
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Sets the message of the error using a {@link DiagnosticMessage} and optional arguments.
         * Requires that the phase of the message matches the phase of the builder.
         *
         * @param message the diagnostic message
         * @param args the arguments for the diagnostic message
         *
         * @return this builder
         *
         * @throws IllegalArgumentException if the phase of the message does not match the phase of
         * the builder
         */
        public Builder message(DiagnosticMessage message, Object... args) {
            if (message.phase() != null && message.phase() != this.phase) {
                throw new IllegalArgumentException("Diagnostic message phase " + message.phase() +
                    " does not match builder phase " + this.phase);
            }
            this.message = message.format(args);
            return this;
        }

        /**
         * Sets the message of the error using a {@link FormattedDiagnostic}. Requires that the
         * phase of the diagnostic matches the phase of the builder.
         *
         * @param diagnostic the formatted diagnostic
         *
         * @return this builder
         *
         * @throws IllegalArgumentException if the phase of the diagnostic does not match the phase
         * of the builder
         */
        public Builder message(FormattedDiagnostic diagnostic) {
            return this.message(diagnostic.message(), diagnostic.arguments());
        }

        /**
         * Sets the cause of the error.
         *
         * @param cause the cause of the error
         *
         * @return this builder
         */
        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        /**
         * Builds the {@link ScriptEvaluationError} instance.
         *
         * @return the built {@link ScriptEvaluationError} instance
         */
        public ScriptEvaluationError build() {
            return new ScriptEvaluationError(this);
        }
    }
}
