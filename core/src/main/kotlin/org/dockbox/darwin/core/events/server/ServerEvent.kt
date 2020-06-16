package org.dockbox.darwin.core.events.server

import org.dockbox.darwin.core.objects.events.Event

abstract class ServerEvent : Event {

    class Init : ServerEvent()
    class Reload : ServerEvent()
    class Starting : ServerEvent()
    class Started : ServerEvent()

}
