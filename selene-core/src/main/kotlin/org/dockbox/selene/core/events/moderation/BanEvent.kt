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

abstract class BanEvent<T>(
        val target: T,
        val reason: Exceptional<String>,
        val source: CommandSource,
        val expiration: Exceptional<LocalDateTime>,
        val creation: LocalDateTime
) : AbstractCancellableEvent() {

    class PlayerBannedEvent(
            target: Target,
            reason: Exceptional<String>,
            source: CommandSource,
            expiration: Exceptional<LocalDateTime>,
            creation: LocalDateTime
    ) : BanEvent<Target>(target, reason, source, expiration, creation)

    class IpBannedEvent(
            host: InetAddress,
            reason: Exceptional<String>,
            source: CommandSource,
            expiration: Exceptional<LocalDateTime>,
            creation: LocalDateTime
    ) : BanEvent<InetAddress>(host, reason, source, expiration, creation)

    class NameBannedEvent(
            name: String,
            reason: Exceptional<String>,
            source: CommandSource,
            expiration: Exceptional<LocalDateTime>,
            creation: LocalDateTime
    ) : BanEvent<String>(name, reason, source, expiration, creation)

    class PlayerUnbannedEvent(
            target: Target,
            reason: Exceptional<String>,
            source: CommandSource,
            creation: LocalDateTime
    ) : BanEvent<Target>(target, reason, source, Exceptional.empty(), creation)

    class IpUnbannedEvent(
            host: InetAddress,
            reason: Exceptional<String>,
            source: CommandSource,
            creation: LocalDateTime
    ) : BanEvent<InetAddress>(host, reason, source, Exceptional.empty(), creation)

    class NameUnbannedEvent(
            name: String,
            reason: Exceptional<String>,
            source: CommandSource,
            creation: LocalDateTime
    ) : BanEvent<String>(name, reason, source, Exceptional.empty(), creation)
}
