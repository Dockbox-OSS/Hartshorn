/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.events.moderation;

import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.core.events.AbstractCancellableEvent;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.targets.Target;

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
    protected BanEvent(T target, CommandSource source, Exceptional<String> reason, Exceptional<LocalDateTime> expiration, LocalDateTime creation) {
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

    public static class PlayerBannedEvent extends BanEvent<Player> {

        /**
         * The event fired when a {@link Target} is banned, typically this is a {@link Player}.
         *
         * @param target
         *         The player being banned
         * @param reason
         *         The reason of the ban
         * @param source
         *         The {@link CommandSource} executing the ban
         * @param expiration
         *         The {@link LocalDateTime} of when the ban expires, if present
         * @param creation
         *         The {@link LocalDateTime} of when the ban was issued.
         */
        public PlayerBannedEvent(Player target, CommandSource source, Exceptional<String> reason, Exceptional<LocalDateTime> expiration, LocalDateTime creation) {
            super(target, source, reason, expiration, creation);
        }
    }

    public static class IpBannedEvent extends BanEvent<InetAddress> {

        /**
         * The event fired when a IP is banned, represented by a {@link InetAddress}. This prevents any user with the provided
         * IP from joining the server, typically used to avoid alt-account ban bypassing.
         *
         * @param host
         *         The IP being banned
         * @param reason
         *         The reason of the ban
         * @param source
         *         The {@link CommandSource} executing the ban
         * @param expiration
         *         The {@link LocalDateTime} of when the ban expires, if present
         * @param creation
         *         The {@link LocalDateTime} of when the ban was issued.
         */
        public IpBannedEvent(InetAddress host, CommandSource source, Exceptional<String> reason, Exceptional<LocalDateTime> expiration, LocalDateTime creation) {
            super(host, source, reason, expiration, creation);
        }
    }

    public static class NameBannedEvent extends BanEvent<String> {

        /**
         * The event fired when a name is banned. This prevents any user with the provided name from joining the server.
         *
         * @param name
         *         The player being banned
         * @param reason
         *         The reason of the ban
         * @param source
         *         The {@link CommandSource} executing the ban
         * @param expiration
         *         The {@link LocalDateTime} of when the ban expires, if present
         * @param creation
         *         The {@link LocalDateTime} of when the ban was issued.
         */
        public NameBannedEvent(String name, CommandSource source, Exceptional<String> reason, Exceptional<LocalDateTime> expiration, LocalDateTime creation) {
            super(name, source, reason, expiration, creation);
        }
    }

    public static class PlayerUnbannedEvent extends BanEvent<Target> {

        /**
         * The event fired when a {@link Target} is unbanned. This can be either through a manual unban, or by the expiration
         * of a ban.
         *
         * @param target
         *         The player being unbanned
         * @param reason
         *         The reason of the original ban
         * @param source
         *         The {@link CommandSource} executing the pardon
         * @param creation
         *         The {@link LocalDateTime} of when the pardon was issued.
         */
        public PlayerUnbannedEvent(Target target, CommandSource source, Exceptional<String> reason, LocalDateTime creation) {
            super(target, source, reason, Exceptional.empty(), creation);
        }
    }

    public static class IpUnbannedEvent extends BanEvent<InetAddress> {

        /**
         * The event fired when a IP is unbanned, represented by a {@link InetAddress}.
         *
         * @param host
         *         The IP being unbanned
         * @param reason
         *         The reason of the original ban
         * @param source
         *         The {@link CommandSource} executing the pardon
         * @param creation
         *         The {@link LocalDateTime} of when the pardon was issued.
         */
        public IpUnbannedEvent(InetAddress host, CommandSource source, Exceptional<String> reason, LocalDateTime creation) {
            super(host, source, reason, Exceptional.empty(), creation);
        }
    }

    public static class NameUnbannedEvent extends BanEvent<String> {

        /**
         * The event fired when a name is unbanned.
         *
         * @param name
         *         The name being unbanned
         * @param reason
         *         The reason of the original ban
         * @param source
         *         The {@link CommandSource} executing the pardon
         * @param creation
         *         The {@link LocalDateTime} of when the pardon was issued.
         */
        public NameUnbannedEvent(String name, CommandSource source, Exceptional<String> reason, LocalDateTime creation) {
            super(name, source, reason, Exceptional.empty(), creation);
        }
    }
}
