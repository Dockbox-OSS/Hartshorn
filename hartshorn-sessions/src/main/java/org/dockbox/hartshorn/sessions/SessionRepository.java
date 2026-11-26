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
 * A repository for storing and resolving sessions.
 *
 * @since 0.8.0
 *
 * @author Guus Lieben
 */
public interface SessionRepository {

    /**
     * Finds a session by its identifier.
     *
     * @param id The identifier of the session to find.
     * @return An {@link Option} containing the session if it exists, or {@link Option#empty()} if it does not.
     */
    Option<Session> find(SessionId id);

    /**
     * Saves a session to the repository.
     *
     * @param session The session to save.
     * @return The saved session.
     */
    Session save(Session session);

    /**
     * Deletes a session from the repository.
     *
     * @param session The session to delete.
     * @return {@code true} if the session was deleted, {@code false} otherwise.
     */
    boolean delete(Session session);

    /**
     * Deletes a session from the repository.
     *
     * @param id The identifier of the session to delete.
     * @return {@code true} if the session was deleted, {@code false} otherwise.
     */
    boolean delete(SessionId id);

    /**
     * Rotates a session, assigning a new identifier to it. {@link #find(SessionId)} should be used to retrieve the
     * session before rotation.
     *
     * @param session The session to rotate.
     * @param newId The new identifier for the session.
     * @return The new identifier for the session.
     */
    SessionId rotate(Session session, SessionId newId);
}
