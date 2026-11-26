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

import java.time.Instant;

/**
 * Creates a new session with the given ID and creation timestamp.
 */
@FunctionalInterface
public interface SessionFactory {

    /**
     * Creates a new session with the given ID and creation timestamp.
     *
     * @param id The ID of the session.
     * @param createdAt The creation timestamp of the session.
     * @return The newly created session.
     */
    Session create(SessionId id, Instant createdAt);
}