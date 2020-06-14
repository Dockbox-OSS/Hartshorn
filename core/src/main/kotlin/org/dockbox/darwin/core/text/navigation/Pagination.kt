package org.dockbox.darwin.core.text.navigation

import org.dockbox.darwin.core.objects.targets.MessageReceiver
import org.dockbox.darwin.core.text.Text

interface Pagination {

    fun send(receiver: MessageReceiver)
    var padding: Text
    var linesPerPage: Number
    var header: Text
    var footer: Text
    var title: Text
    var content: Iterable<Text>

}
