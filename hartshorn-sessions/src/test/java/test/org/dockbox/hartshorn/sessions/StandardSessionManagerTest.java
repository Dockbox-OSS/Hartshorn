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

package test.org.dockbox.hartshorn.sessions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.dockbox.hartshorn.sessions.InMemorySessionRepository;
import org.dockbox.hartshorn.sessions.Session;
import org.dockbox.hartshorn.sessions.SessionId;
import org.dockbox.hartshorn.sessions.SessionLifecycleListener;
import org.dockbox.hartshorn.sessions.StandardSessionFactory;
import org.dockbox.hartshorn.sessions.StandardSessionManager;
import org.dockbox.hartshorn.sessions.expiration.IdleSessionExpirationPolicy;
import org.dockbox.hartshorn.sessions.id.LongSessionId;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSessionManagerTest {

    private static final Duration TIMEOUT = Duration.ofMinutes(5);
    private static final Instant START = Instant.parse("2026-01-01T00:00:00Z");

    private final InMemorySessionRepository repository = new InMemorySessionRepository();
    private final StandardSessionFactory factory = new StandardSessionFactory();
    private final AtomicLong nextId = new AtomicLong();
    private final RecordingListener listener = new RecordingListener();

    private Instant now = START;
    private final InstantSource timeSource = () -> this.now;
    private final Clock clock = this.timeSource.withZone(ZoneOffset.UTC);

    private final StandardSessionManager manager = new StandardSessionManager(
            this.repository,
            this.factory,
            () -> new LongSessionId(this.nextId.incrementAndGet()),
            new IdleSessionExpirationPolicy(TIMEOUT),
            this.clock);

    @BeforeEach
    void registerListener() {
        this.manager.addListener(this.listener);
    }

    @Test
    void createsAndStoresSessionWithInitialExpiration() {
        Session session = this.manager.create();

        assertEquals(new LongSessionId(1), session.id());
        assertEquals(START, session.createdAt());
        assertEquals(START.plus(TIMEOUT), session.expiresAt().orNull());
        assertSame(session, this.repository.find(session.id()).orNull());
        assertEquals(List.of("expirationChanged", "created"), this.listener.events);
        assertSame(session, this.listener.lastSession);
        assertNull(this.listener.previousExpiration);
    }

    @Test
    void findLeavesSessionUntouchedWhileAccessRefreshesIdleExpiration() {
        Session session = this.manager.create();
        this.listener.events.clear();
        this.now = START.plusSeconds(60);

        assertSame(session, this.manager.find(session.id()).orNull());
        assertEquals(START, session.lastAccessedAt());
        assertEquals(START.plus(TIMEOUT), session.expiresAt().orNull());
        assertEquals(List.of(), this.listener.events);

        assertSame(session, this.manager.access(session.id()).orNull());
        assertEquals(this.now, session.lastAccessedAt());
        assertEquals(this.now.plus(TIMEOUT), session.expiresAt().orNull());
        assertEquals(START.plus(TIMEOUT), this.listener.previousExpiration);
        assertEquals(List.of("expirationChanged", "accessed"), this.listener.events);
        assertSame(session, this.listener.lastSession);
    }

    @Test
    void findExpiresSessionAtDeadlineAndNotifiesOnce() {
        Session session = this.manager.create();
        this.listener.events.clear();
        this.now = START.plus(TIMEOUT);

        assertNull(this.manager.find(session.id()).orNull());
        assertNull(this.repository.find(session.id()).orNull());
        assertNull(this.manager.find(session.id()).orNull());
        assertEquals(List.of("invalidated", "expired"), this.listener.events);
        assertSame(session, this.listener.lastSession);
    }

    @Test
    void accessCannotReviveExpiredSession() {
        Session session = this.manager.create();
        this.listener.events.clear();
        this.now = START.plus(TIMEOUT).plusSeconds(1);

        assertNull(this.manager.access(session.id()).orNull());
        assertNull(this.repository.find(session.id()).orNull());
        assertEquals(START, session.lastAccessedAt());
        assertEquals(List.of("invalidated", "expired"), this.listener.events);
    }

    @Test
    void rotatesSessionAndNotifiesWithPreviousId() {
        Session session = this.manager.create();
        SessionId previousId = session.id();
        this.listener.events.clear();

        SessionId newId = this.manager.rotate(session);

        assertEquals(new LongSessionId(2), newId);
        assertEquals(newId, session.id());
        assertNull(this.manager.find(previousId).orNull());
        assertSame(session, this.manager.find(newId).orNull());
        assertEquals(List.of("rotated"), this.listener.events);
        assertEquals(previousId, this.listener.previousId);
        assertSame(session, this.listener.lastSession);
    }

    @Test
    void rotatingExpiredSessionRemovesItAndFails() {
        Session session = this.manager.create();
        SessionId previousId = session.id();
        this.now = START.plus(TIMEOUT);

        assertThrows(IllegalStateException.class, () -> this.manager.rotate(session));
        assertEquals(previousId, session.id());
        assertNull(this.repository.find(previousId).orNull());
    }

    @Test
    void invalidatingByIdOnlyNotifiesForRegisteredSession() {
        Session session = this.manager.create();
        this.listener.events.clear();

        this.manager.invalidate(session.id());
        this.manager.invalidate(session.id());

        assertNull(this.repository.find(session.id()).orNull());
        assertEquals(List.of("invalidated"), this.listener.events);
        assertSame(session, this.listener.lastSession);
    }

    @Test
    void removedListenerReceivesNoEvents() {
        this.manager.removeListener(this.listener);
        this.manager.create();

        assertEquals(List.of(), this.listener.events);
    }

    @Test
    void sessionWithoutExpirationRemainsAccessible() {
        StandardSessionManager manager = new StandardSessionManager(
                this.repository, this.factory, () -> new LongSessionId(1), session -> Option.empty(), this.clock);
        Session session = manager.create();
        this.now = START.plus(Duration.ofDays(1));

        assertSame(session, manager.access(session.id()).orNull());
        assertEquals(this.now, session.lastAccessedAt());
        assertNull(session.expiresAt().orNull());
    }

    private static class RecordingListener implements SessionLifecycleListener {

        private final List<String> events = new ArrayList<>();
        private Session lastSession;
        private SessionId previousId;
        private Instant previousExpiration;

        @Override
        public void onSessionCreated(Session session) {
            this.record("created", session);
        }

        @Override
        public void onSessionAccessed(Session session) {
            this.record("accessed", session);
        }

        @Override
        public void onSessionRotated(Session session, SessionId previousId) {
            this.previousId = previousId;
            this.record("rotated", session);
        }

        @Override
        public void onSessionInvalidated(Session session) {
            this.record("invalidated", session);
        }

        @Override
        public void onSessionExpired(Session session) {
            this.record("expired", session);
        }

        @Override
        public void onSessionExpirationChanged(Session session, Instant previousExpiration) {
            this.previousExpiration = previousExpiration;
            this.record("expirationChanged", session);
        }

        private void record(String event, Session session) {
            this.events.add(event);
            this.lastSession = session;
        }
    }
}
