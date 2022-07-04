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

package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * The top-level class of the virtual runtime. This class is responsible for managing the
 * error reporting throughout the entire runtime. Errors can be reported during any valid
 * {@link Phase} of the runtime, and should always include an origin position. The origin
 * position is identified either by a {@link Token} or by a line number.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public interface ErrorReporter {

    /**
     * Reports an error. The error is reported at the given position. This may yield a
     * {@link RuntimeException}, or consume the error without breaking the current phase.
     *
     * @param phase The phase at which the error occurred.
     * @param line The line number at which the error occurred.
     * @param message The error message.
     */
    void error(Phase phase, int line, String message);

    /**
     * Reports an error. The error is reported at the given position. This may yield a
     * {@link RuntimeException}, or consume the error without breaking the current phase.
     *
     * @param phase The phase at which the error occurred.
     * @param token The token at which the error occurred.
     * @param message The error message.
     */
    void error(Phase phase, Token token, String message);

    /**
     * Clears all reported errors, and resets the error reporter to its initial state.
     */
    void clear();
}
