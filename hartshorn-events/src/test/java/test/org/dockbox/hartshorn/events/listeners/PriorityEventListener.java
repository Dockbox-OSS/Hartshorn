/*
 * Copyright 2019-2022 the original author or authors.
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

package test.org.dockbox.hartshorn.events.listeners;

import test.org.dockbox.hartshorn.events.SampleEvent;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.events.annotations.Listener.Priority;
import org.junit.jupiter.api.Assertions;

public class PriorityEventListener {

    private static Priority last;

    public static Priority last() {
        return last;
    }

    @Listener(Priority.FIRST)
    public void onFirst(final SampleEvent event) {
        Assertions.assertNull(PriorityEventListener.last);
        PriorityEventListener.last = Priority.FIRST;
    }

    @Listener(Priority.EARLY)
    public void onEarly(final SampleEvent event) {
        Assertions.assertEquals(PriorityEventListener.last, Priority.FIRST);
        PriorityEventListener.last = Priority.EARLY;
    }

    @Listener(Priority.NORMAL)
    public void onNormal(final SampleEvent event) {
        Assertions.assertEquals(PriorityEventListener.last, Priority.EARLY);
        PriorityEventListener.last = Priority.NORMAL;
    }

    @Listener(Priority.LATE)
    public void onLate(final SampleEvent event) {
        Assertions.assertEquals(PriorityEventListener.last, Priority.NORMAL);
        PriorityEventListener.last = Priority.LATE;
    }

    @Listener(Priority.LAST)
    public void onLast(final SampleEvent event) {
        Assertions.assertEquals(PriorityEventListener.last, Priority.LATE);
        PriorityEventListener.last = Priority.LAST;
    }
}
