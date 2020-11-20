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

package org.dockbox.selene.core.events.moderation

import java.net.InetAddress
import java.time.LocalDateTime
import org.dockbox.selene.core.events.AbstractCancellableEvent
import org.dockbox.selene.core.objects.optional.Exceptional
import org.dockbox.selene.core.objects.targets.CommandSource
import org.dockbox.selene.core.objects.targets.Target

/**
 * The abstract type which can be used to listen to all ban related events.
 *
 * @param T The type of the target
 * @property target The target being banned
 * @property reason The reason of the ban
 * @property source The [CommandSource] executing the ban
 * @property expiration The [LocalDateTime] of when the ban expires, if present
 * @property creation The [LocalDateTime] of when the ban was issued.
 */
abstract class BanEvent<T>(
        val target: T,
        val reason: Exceptional<String>,
        val source: CommandSource,
        val expiration: Exceptional<LocalDateTime>,
        val creation: LocalDateTime
) : AbstractCancellableEvent() {

    /**
     * The event fired when a [Target] is banned, typically this is a [org.dockbox.selene.core.objects.user.Player].
     *
     * @param target The player being banned
     * @param reason The reason of the ban
     * @param source The [CommandSource] executing the ban
     * @param expiration The [LocalDateTime] of when the ban expires, if present
     * @param creation The [LocalDateTime] of when the ban was issued.
     */
    class PlayerBannedEvent(
            target: Target,
            reason: Exceptional<String>,
            source: CommandSource,
            expiration: Exceptional<LocalDateTime>,
            creation: LocalDateTime
    ) : BanEvent<Target>(target, reason, source, expiration, creation)

    /**
     * The event fired when a IP is banned, represented by a [InetAddress]. This prevents any user with the provided
     * IP from joining the server, typically used to avoid alt-account ban bypassing.
     *
     * @param host The IP being banned
     * @param reason The reason of the ban
     * @param source The [CommandSource] executing the ban
     * @param expiration The [LocalDateTime] of when the ban expires, if present
     * @param creation The [LocalDateTime] of when the ban was issued.
     */
    class IpBannedEvent(
            host: InetAddress,
            reason: Exceptional<String>,
            source: CommandSource,
            expiration: Exceptional<LocalDateTime>,
            creation: LocalDateTime
    ) : BanEvent<InetAddress>(host, reason, source, expiration, creation)

    /**
     * The event fired when a name is banned. This prevents any user with the provided name from joining the server.
     *
     * @param name The player being banned
     * @param reason The reason of the ban
     * @param source The [CommandSource] executing the ban
     * @param expiration The [LocalDateTime] of when the ban expires, if present
     * @param creation The [LocalDateTime] of when the ban was issued.
     */
    class NameBannedEvent(
            name: String,
            reason: Exceptional<String>,
            source: CommandSource,
            expiration: Exceptional<LocalDateTime>,
            creation: LocalDateTime
    ) : BanEvent<String>(name, reason, source, expiration, creation)

    /**
     * The event fired when a [Target] is unbanned. This can be either through a manual unban, or by the expiration
     * of a ban.
     *
     * @param target The player being unbanned
     * @param reason The reason of the original ban
     * @param source The [CommandSource] executing the pardon
     * @param creation The [LocalDateTime] of when the pardon was issued.
     */
    class PlayerUnbannedEvent(
            target: Target,
            reason: Exceptional<String>,
            source: CommandSource,
            creation: LocalDateTime
    ) : BanEvent<Target>(target, reason, source, Exceptional.empty(), creation)

    /**
     * The event fired when a IP is unbanned, represented by a [InetAddress].
     *
     * @param host The IP being unbanned
     * @param reason The reason of the original ban
     * @param source The [CommandSource] executing the pardon
     * @param creation The [LocalDateTime] of when the pardon was issued.
     */
    class IpUnbannedEvent(
            host: InetAddress,
            reason: Exceptional<String>,
            source: CommandSource,
            creation: LocalDateTime
    ) : BanEvent<InetAddress>(host, reason, source, Exceptional.empty(), creation)

    /**
     * The event fired when a name is unbanned.
     *
     * @param name The name being unbanned
     * @param reason The reason of the original ban
     * @param source The [CommandSource] executing the pardon
     * @param creation The [LocalDateTime] of when the pardon was issued.
     */
    class NameUnbannedEvent(
            name: String,
            reason: Exceptional<String>,
            source: CommandSource,
            creation: LocalDateTime
    ) : BanEvent<String>(name, reason, source, Exceptional.empty(), creation)
}
