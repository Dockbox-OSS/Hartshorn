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

/**
 * Manages sessions, allowing for the creation, retrieval, rotation, and invalidation of sessions.
 *
 * @since 0.8.0
 *
 * @author Guus Lieben
 */
public interface SessionManager {

    /**
     * Creates a new session.
     *
     * @return The newly created session.
     */
    Session create();

    /**
     * Retrieves a session by its identifier, without modifying its access time.
     *
     * @param id The identifier of the session to retrieve.
     * @return An {@link Option} containing the session if it exists, or an empty option if it does not.
     */
    Option<Session> find(SessionId id);

    /**
     * Accesses a session by its identifier, updating its access time.
     *
     * @param id The identifier of the session to access.
     * @return An {@link Option} containing the session if it exists, or an empty option if it does not.
     */
    Option<Session> access(SessionId id);

    /**
     * Rotates a session, generating a new identifier for it. {@link #find(SessionId)} should be used to retrieve the
     * session before rotation.
     *
     * @param session The session to rotate.
     * @return The new identifier for the session.
     *
     * @throws IllegalStateException if the session is not found or is expired.
     */
    SessionId rotate(Session session);

    /**
     * Invalidates a session, removing it from the session manager.
     *
     * @param session The session to invalidate.
     */
    void invalidate(Session session);

    /**
     * Invalidates a session by its identifier, removing it from the session manager.
     *
     * @param id The identifier of the session to invalidate.
     */
    void invalidate(SessionId id);

    /**
     * Adds a listener to the session manager, which will be notified of session lifecycle events.
     *
     * @param listener The listener to add.
     */
    void addListener(SessionLifecycleListener listener);

    /**
     * Removes a listener from the session manager.
     *
     * @param listener The listener to remove.
     */
    void removeListener(SessionLifecycleListener listener);
}
