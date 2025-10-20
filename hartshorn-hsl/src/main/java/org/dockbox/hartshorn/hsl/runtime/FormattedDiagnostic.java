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

package org.dockbox.hartshorn.hsl.runtime;

import java.util.ArrayList;
import java.util.SequencedCollection;

/**
 * A formatted diagnostic message, consisting of a {@link DiagnosticMessage} and its arguments.
 *
 * @param message The diagnostic message.
 * @param arguments The arguments to format the message with.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public record FormattedDiagnostic(DiagnosticMessage message, Object... arguments) {

    /**
     * Formats the diagnostic message with its arguments. Shorthand for
     * {@link DiagnosticMessage#format(Object...)} on the underlying message.
     *
     * @return The formatted diagnostic message.
     */
    public String format() {
        return this.message.format(this.arguments);
    }

    /**
     * Creates a new {@link FormattedDiagnostic} instance.
     *
     * @param message the diagnostic message
     * @param arguments the arguments to format the message with
     * @return the formatted diagnostic
     */
    public static FormattedDiagnostic of(DiagnosticMessage message, Object... arguments) {
        return new FormattedDiagnostic(message, arguments);
    }

    /**
     * Creates a new {@link Builder} for {@link FormattedDiagnostic}.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder for {@link FormattedDiagnostic}.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public static class Builder {
        private final SequencedCollection<Object> arguments = new ArrayList<>();
        private DiagnosticMessage message;

        /**
         * Sets the diagnostic message.
         *
         * @param message the diagnostic message
         * @return this builder
         */
        public Builder message(DiagnosticMessage message) {
            this.message = message;
            return this;
        }

        /**
         * Adds an argument to the diagnostic message. The order of arguments is preserved.
         *
         * @param argument the argument to add
         * @return this builder
         */
        public Builder argument(Object argument) {
            this.arguments.add(argument);
            return this;
        }

        /**
         * Builds the {@link FormattedDiagnostic} instance.
         *
         * @return the formatted diagnostic
         */
        public FormattedDiagnostic build() {
            return new FormattedDiagnostic(this.message, this.arguments.toArray());
        }
    }
}
