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

package org.dockbox.selene.server.minecraft.events.chat;

import org.dockbox.selene.api.events.AbstractTargetCancellableEvent;
import org.dockbox.selene.api.i18n.MessageReceiver;
import org.dockbox.selene.api.i18n.text.Text;

import lombok.Getter;
import lombok.Setter;

/** The event fired when a player sends a message in chat. */
public class SendChatEvent extends AbstractTargetCancellableEvent {

    @Getter @Setter
    private Text message;

    public SendChatEvent(MessageReceiver target, Text message) {
        super(target);
        this.message = message;
    }
}
