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

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.option.Option;

import java.time.Instant;

/**
 * A standard implementation of the {@link Session} interface.
 *
 * @since 0.8.0
 *
 * @author Guus Lieben
 */
public class StandardSession extends DefaultContext implements Session {

    private SessionId id;
    private final Instant createdAt;
    private volatile Instant lastAccessedAt;
    private volatile Instant expiresAt;

    public StandardSession(SessionId id, Instant createdAt, Instant lastAccessedAt, Instant expiresAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.lastAccessedAt = lastAccessedAt;
        this.expiresAt = expiresAt;
    }

    public StandardSession(SessionId id, Instant createdAt, Instant lastAccessedAt) {
        this(id, createdAt, lastAccessedAt, null);
    }

    @Override
    public SessionId id() {
        return this.id;
    }

    @Override
    public Instant createdAt() {
        return this.createdAt;
    }

    @Override
    public Instant lastAccessedAt() {
        return this.lastAccessedAt;
    }

    @Override
    public Option<Instant> expiresAt() {
        return Option.of(this.expiresAt);
    }

    @Override
    public void rotate(SessionId newId) {
        this.id = newId;
    }

    @Override
    public void touch(Instant accessedAt) {
        if (accessedAt.isAfter(this.lastAccessedAt)) {
            this.lastAccessedAt = accessedAt;
        }
    }

    @Override
    public void expiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
