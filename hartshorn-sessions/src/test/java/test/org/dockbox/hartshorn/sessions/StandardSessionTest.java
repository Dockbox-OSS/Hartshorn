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

import java.time.Duration;
import java.time.Instant;
import org.dockbox.hartshorn.sessions.Session;
import org.dockbox.hartshorn.sessions.StandardSessionFactory;
import org.dockbox.hartshorn.sessions.expiration.AbsoluteSessionExpirationPolicy;
import org.dockbox.hartshorn.sessions.expiration.IdleSessionExpirationPolicy;
import org.dockbox.hartshorn.sessions.id.LongSessionId;
import org.junit.jupiter.api.Test;

class StandardSessionTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");

    private final Session session = new StandardSessionFactory().create(new LongSessionId(1), CREATED_AT);

    @Test
    void factoryInitializesSessionWithoutExpiration() {
        assertEquals(new LongSessionId(1), this.session.id());
        assertEquals(CREATED_AT, this.session.createdAt());
        assertEquals(CREATED_AT, this.session.lastAccessedAt());
        assertNull(this.session.expiresAt().orNull());
    }

    @Test
    void touchDoesNotMoveLastAccessBackwards() {
        Instant later = CREATED_AT.plusSeconds(10);
        this.session.touch(later);
        this.session.touch(CREATED_AT.plusSeconds(5));

        assertEquals(later, this.session.lastAccessedAt());
        assertEquals(CREATED_AT, this.session.createdAt());
    }

    @Test
    void expirationCanBeSetAndCleared() {
        Instant expiration = CREATED_AT.plusSeconds(30);
        this.session.expiresAt(expiration);
        assertEquals(expiration, this.session.expiresAt().orNull());

        this.session.expiresAt(null);
        assertNull(this.session.expiresAt().orNull());
    }

    @Test
    void absoluteAndIdlePoliciesUseDifferentTimestamps() {
        Instant accessedAt = CREATED_AT.plusSeconds(10);
        this.session.touch(accessedAt);
        Duration timeout = Duration.ofSeconds(30);

        assertEquals(
                CREATED_AT.plus(timeout),
                new AbsoluteSessionExpirationPolicy(timeout)
                        .expiresAt(this.session)
                        .orNull());
        assertEquals(
                accessedAt.plus(timeout),
                new IdleSessionExpirationPolicy(timeout).expiresAt(this.session).orNull());
    }
}
