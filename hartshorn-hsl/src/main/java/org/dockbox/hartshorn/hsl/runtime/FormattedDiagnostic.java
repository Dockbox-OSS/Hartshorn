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
import java.util.List;

public record FormattedDiagnostic(DiagnosticMessage message, Object... arguments) {
    public String format() {
        return message.format(arguments);
    }

    public static FormattedDiagnostic of(DiagnosticMessage message, Object... arguments) {
        return new FormattedDiagnostic(message, arguments);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Object> arguments = new ArrayList<>();
        private DiagnosticMessage message;

        public Builder message(DiagnosticMessage message) {
            this.message = message;
            return this;
        }

        public Builder argument(Object argument) {
            this.arguments.add(argument);
            return this;
        }

        public FormattedDiagnostic build() {
            return new FormattedDiagnostic(this.message, this.arguments.toArray());
        }
    }
}
