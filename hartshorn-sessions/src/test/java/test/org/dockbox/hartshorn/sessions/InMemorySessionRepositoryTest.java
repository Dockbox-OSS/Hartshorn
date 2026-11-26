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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.dockbox.hartshorn.sessions.InMemorySessionRepository;
import org.dockbox.hartshorn.sessions.Session;
import org.dockbox.hartshorn.sessions.SessionIdConflictException;
import org.dockbox.hartshorn.sessions.StandardSessionFactory;
import org.dockbox.hartshorn.sessions.id.LongSessionId;
import org.junit.jupiter.api.Test;

class InMemorySessionRepositoryTest {

    private final InMemorySessionRepository repository = new InMemorySessionRepository();
    private final LongSessionId originalId = new LongSessionId(1);
    private final LongSessionId newId = new LongSessionId(2);
    private final StandardSessionFactory factory = new StandardSessionFactory();
    private final Session session = this.factory.create(this.originalId, Instant.EPOCH);

    @Test
    void savesAndFindsSessionByValueEqualId() {
        assertNull(this.repository.find(this.originalId).orNull());
        assertSame(this.session, this.repository.save(this.session));
        assertSame(this.session, this.repository.save(this.session));
        assertSame(this.session, this.repository.find(new LongSessionId(1)).orNull());
    }

    @Test
    void duplicateIdLeavesOriginalSessionRegistered() {
        this.repository.save(this.session);
        Session duplicate = this.factory.create(new LongSessionId(1), Instant.EPOCH);

        assertThrows(SessionIdConflictException.class, () -> this.repository.save(duplicate));
        assertSame(this.session, this.repository.find(this.originalId).orNull());
        assertFalse(this.repository.delete(duplicate));
    }

    @Test
    void rotatingMovesSessionToNewId() {
        this.repository.save(this.session);

        assertEquals(this.newId, this.repository.rotate(this.session, this.newId));
        assertEquals(this.newId, this.session.id());
        assertNull(this.repository.find(this.originalId).orNull());
        assertSame(this.session, this.repository.find(this.newId).orNull());
        assertSame(this.session, this.repository.save(this.session));
    }

    @Test
    void conflictingRotationPreservesBothSessions() {
        Session other = this.factory.create(this.newId, Instant.EPOCH);
        this.repository.save(this.session);
        this.repository.save(other);

        assertThrows(SessionIdConflictException.class, () -> this.repository.rotate(this.session, this.newId));
        assertEquals(this.originalId, this.session.id());
        assertSame(this.session, this.repository.find(this.originalId).orNull());
        assertSame(other, this.repository.find(this.newId).orNull());
    }

    @Test
    void rejectsRotationForUnregisteredSession() {
        assertThrows(IllegalStateException.class, () -> this.repository.rotate(this.session, this.newId));
        assertEquals(this.originalId, this.session.id());
        assertNull(this.repository.find(this.newId).orNull());
    }

    @Test
    void rejectsSavingSessionWhoseIdChangedOutsideRepository() {
        this.repository.save(this.session);
        this.session.rotate(this.newId);

        assertThrows(IllegalStateException.class, () -> this.repository.save(this.session));
        assertSame(this.session, this.repository.find(this.originalId).orNull());
        assertNull(this.repository.find(this.newId).orNull());
    }

    @Test
    void deletingByIdOrSessionRemovesBothMappings() {
        this.repository.save(this.session);
        assertTrue(this.repository.delete(this.originalId));
        assertFalse(this.repository.delete(this.session));

        this.repository.save(this.session);
        assertTrue(this.repository.delete(this.session));
        assertFalse(this.repository.delete(this.originalId));
        assertNull(this.repository.find(this.originalId).orNull());
    }
}
