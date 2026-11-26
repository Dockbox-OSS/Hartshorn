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
 * Listener for session lifecycle events.
 *
 * @since 0.8.0
 *
 * @author Guus Lieben
 */
public interface SessionLifecycleListener {

    /**
     * Called when a session is created.
     *
     * @param session The session that was created.
     */
    default void onSessionCreated(Session session) {
    }

    /**
     * Called when a session is accessed.
     *
     * @param session The session that was accessed.
     */
    default void onSessionAccessed(Session session) {
    }

    /**
     * Called when a session is rotated.
     *
     * @param session The session that was rotated.
     * @param previousId The previous session id.
     */
    default void onSessionRotated(
            Session session,
            SessionId previousId
    ) {
    }

    /**
     * Called when a session is invalidated.
     *
     * @param session The session that was invalidated.
     */
    default void onSessionInvalidated(Session session) {
    }

    /**
     * Called when a session expires.
     *
     * @param session The session that expired.
     */
    default void onSessionExpired(Session session) {
    }

    /**
     * Called when a session expiration changes.
     *
     * @param session The session that had its expiration changed.
     * @param previousExpiration The previous expiration time.
     */
    default void onSessionExpirationChanged(Session session, Instant previousExpiration) {
    }
}