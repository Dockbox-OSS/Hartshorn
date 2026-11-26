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

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.util.option.Option;

import java.time.Instant;

/**
 * Sessions represent a user's interaction with an application. They are used to maintain state across multiple
 * interactions. While primarily used in HTTP sessions, they can be used in other contexts as well.
 *
 * @since 0.8.0
 *
 * @author Guus Lieben
 */
public interface Session extends Context {

    /**
     * Returns the unique identifier of this session.
     *
     * @return the unique identifier of this session
     */
    SessionId id();

    /**
     * Returns the instant at which this session was created.
     *
     * @return the instant at which this session was created
     */
    Instant createdAt();

    /**
     * Returns the instant at which this session was last accessed.
     *
     * @return the instant at which this session was last accessed
     */
    Instant lastAccessedAt();

    /**
     * Returns the instant at which this session will expire, if any.
     *
     * @return the instant at which this session will expire, if any
     */
    Option<Instant> expiresAt();

    /**
     * Rotates the session identifier to a new value. This is useful when a session needs to be invalidated or
     * transferred to a new user.
     *
     * @param newId the new session identifier
     */
    void rotate(SessionId newId);

    /**
     * Indicates that a session was accessed at the given instant, updating the last accessed timestamp. Note that if
     * the access timestamp is older than the current last accessed timestamp, it will be ignored. If the access
     * interaction affects the expiration timestamp, it should be updated accordingly through
     * {@link #expiresAt(Instant)}.
     *
     * @param accessedAt the instant at which the session was last accessed
     */
    void touch(Instant accessedAt);

    /**
     * Updates the expiration timestamp of this session to the given instant, no matter whether the new timestamp is
     * older or newer than the current expiration timestamp.
     *
     * @param expiresAt the new expiration timestamp
     */
    void expiresAt(Instant expiresAt);
}
