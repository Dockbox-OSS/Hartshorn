/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.api.events.moderation;

import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.events.AbstractCancellableEvent;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.objects.targets.Target;

import java.net.InetAddress;
import java.time.LocalDateTime;

public abstract class BanEvent<T> extends AbstractCancellableEvent {

    private final T target;
    private final CommandSource source;
    private final LocalDateTime creation;
    private Exceptional<String> reason;
    private Exceptional<LocalDateTime> expiration;

    /**
     * The abstract type which can be used to listen to all ban related events.
     *
     * @param target
     *         The target being banned
     * @param reason
     *         The reason of the ban
     * @param source
     *         The {@link CommandSource} executing the ban
     * @param expiration
     *         The {@link LocalDateTime} of when the ban expires, if present
     * @param creation
     *         The {@link LocalDateTime} of when the ban was issued.
     */
    protected BanEvent(
            T target,
            CommandSource source,
            Exceptional<String> reason,
            Exceptional<LocalDateTime> expiration,
            LocalDateTime creation
    ) {
        this.target = target;
        this.source = source;
        this.reason = reason;
        this.expiration = expiration;
        this.creation = creation;
    }

    public Exceptional<String> getReason() {
        return this.reason;
    }

    public void setReason(Exceptional<String> reason) {
        this.reason = reason;
    }

    public Exceptional<LocalDateTime> getExpiration() {
        return this.expiration;
    }

    public void setExpiration(Exceptional<LocalDateTime> expiration) {
        this.expiration = expiration;
    }

    public LocalDateTime getCreation() {
        return this.creation;
    }

    public T getTarget() {
        return this.target;
    }

    public CommandSource getSource() {
        return this.source;
    }

}
