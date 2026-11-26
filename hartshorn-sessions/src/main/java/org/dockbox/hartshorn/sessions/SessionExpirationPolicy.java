/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.sessions;

import org.dockbox.hartshorn.util.option.Option;

import java.time.Instant;

/**
 * Determines the expiration timestamp of a session based on its current state.
 *
 * @since 0.8.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface SessionExpirationPolicy {

    /**
     * Determines the expiration timestamp of a session based on its current state, returning an {@link Option} that may
     * contain the expiration timestamp or be empty if the session does not expire.
     *
     * @param session The session for which to determine the expiration timestamp.
     * @return An {@link Option} containing the expiration timestamp or empty if the session does not expire.
     */
    Option<Instant> expiresAt(Session session);
}