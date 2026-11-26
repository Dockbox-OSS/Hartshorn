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

import org.dockbox.hartshorn.util.collections.BiMap;
import org.dockbox.hartshorn.util.collections.HashBiMap;
import org.dockbox.hartshorn.util.option.Option;

/**
 * In-memory session repository, which supports concurrent access. This implementation is only recommended for single
 * use cases where the application is not expected to scale beyond a single node. For scalable use cases, consider using
 * a distributed session repository.
 *
 * @since 0.8.0
 *
 * @author Guus Lieben
 */
public class InMemorySessionRepository implements SessionRepository {

    private final BiMap<SessionId, Session> sessions = new HashBiMap<>();
    private final Object lock = new Object();

    @Override
    public Option<Session> find(SessionId id) {
        synchronized (this.lock) {
            return Option.of(this.sessions.get(id));
        }
    }

    @Override
    public Session save(Session session) {
        synchronized (this.lock) {
            SessionId knownId = this.sessions.inverse().get(session);
            if (knownId != null) {
                if (!knownId.equals(session.id())) {
                    throw new IllegalStateException(
                            "Session identifier was modified outside the repository"
                    );
                }
                return session;
            }
            requireUniqueId(session.id());
            this.sessions.put(session.id(), session);
            return session;
        }
    }

    @Override
    public SessionId rotate(Session session, SessionId newId) {
        synchronized (this.lock) {
            SessionId previousId = this.sessions.inverse().get(session);
            if (previousId == null) {
                throw new IllegalStateException(
                        "Cannot rotate an unregistered session"
                );
            }
            requireUniqueId(newId);
            this.sessions.remove(previousId);
            session.rotate(newId);
            this.sessions.put(newId, session);
            return newId;
        }
    }

    @Override
    public boolean delete(Session session) {
        synchronized (this.lock) {
            SessionId id = this.sessions.inverse().get(session);
            return id != null && this.sessions.remove(id) != null;
        }
    }

    @Override
    public boolean delete(SessionId id) {
        synchronized (this.lock) {
            return this.sessions.remove(id) != null;
        }
    }

    private void requireUniqueId(SessionId id) {
        if (this.sessions.containsKey(id)) {
            throw new SessionIdConflictException(id);
        }
    }
}