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

import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A standard implementation of the {@link SessionManager} interface, supporting concurrent access.
 *
 * @since 0.8.0
 *
 * @author Guus Lieben
 */
public class StandardSessionManager implements SessionManager {

    private final Set<SessionLifecycleListener> listeners = new HashSet<>();
    private final SessionRepository repository;
    private final SessionFactory factory;
    private final SessionIdGenerationStrategy generationStrategy;
    private final SessionExpirationPolicy expirationPolicy;
    private final Clock clock;

    private final Object lock = new Object();

    public StandardSessionManager(
            SessionRepository repository,
            SessionFactory factory,
            SessionIdGenerationStrategy generationStrategy,
            SessionExpirationPolicy expirationPolicy,
            Clock clock
    ) {
        this.repository = repository;
        this.factory = factory;
        this.generationStrategy = generationStrategy;
        this.expirationPolicy = expirationPolicy;
        this.clock = clock;
    }

    @Override
    public Session create() {
        synchronized (this.lock) {
            Instant now = this.clock.instant();
            SessionId id = this.generationStrategy.generate();

            Session session = this.factory.create(id, now);
            this.updateExpiration(session);

            Session savedSession = this.repository.save(session);
            this.listeners.forEach(listener -> listener.onSessionCreated(savedSession));
            return savedSession;
        }
    }

    @Override
    public Option<Session> find(SessionId id) {
        synchronized (this.lock) {
            return this.findActiveSession(id, this.clock.instant());
        }
    }

    @Override
    public Option<Session> access(SessionId id) {
        synchronized (this.lock) {
            Instant now = this.clock.instant();
            return this.findActiveSession(id, now).map(session -> {
                session.touch(now);
                this.updateExpiration(session);

                Session savedSession = this.repository.save(session);
                this.listeners.forEach(listener -> listener.onSessionAccessed(savedSession));
                return savedSession;
            });
        }
    }

    @Override
    public SessionId rotate(Session session) {
        synchronized (this.lock) {
            if (this.isExpired(session, this.clock.instant())) {
                this.invalidate(session);
                throw new IllegalStateException("Cannot rotate an expired session");
            }
            SessionId previousId = session.id();
            SessionId newId = this.repository.rotate(session, this.generationStrategy.generate());
            this.listeners.forEach(listener -> listener.onSessionRotated(session, previousId));
            return newId;
        }
    }

    @Override
    public void invalidate(Session session) {
        synchronized (this.lock) {
            this.repository.delete(session);
            this.listeners.forEach(listener -> listener.onSessionInvalidated(session));
        }
    }

    @Override
    public void invalidate(SessionId id) {
        synchronized (this.lock) {
            this.repository.find(id).peek(this::invalidate);
        }
    }

    @Override
    public void addListener(SessionLifecycleListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(SessionLifecycleListener listener) {
        this.listeners.remove(listener);
    }

    protected Option<Session> findActiveSession(SessionId id, Instant now) {
        return this.repository.find(id).filter(session -> {
            if (this.isExpired(session, now)) {
                this.invalidate(session);
                this.listeners.forEach(listener -> listener.onSessionExpired(session));
                return false;
            }
            return true;
        });
    }

    protected boolean isExpired(Session session, Instant now) {
        return session.expiresAt().test(expiration -> !expiration.isAfter(now));
    }

    protected void updateExpiration(Session session) {
        Instant previousExpiration = session.expiresAt().orNull();
        Instant expiration = this.expirationPolicy.expiresAt(session).orElse(null);
        session.expiresAt(expiration);
        this.listeners.forEach(listener -> listener.onSessionExpirationChanged(session, previousExpiration));
    }
}