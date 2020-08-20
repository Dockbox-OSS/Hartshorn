package org.dockbox.darwin.core.events.chat

import org.dockbox.darwin.core.events.AbstractTargetCancellableEvent
import org.dockbox.darwin.core.objects.targets.MessageReceiver

class SendChatEvent(
        target: MessageReceiver,
        val isGlobalChat: Boolean,
        val message: String,
        val channel: String?
) : AbstractTargetCancellableEvent(target)
