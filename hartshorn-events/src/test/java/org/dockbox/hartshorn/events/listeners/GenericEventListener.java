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

package org.dockbox.hartshorn.events.listeners;

import org.dockbox.hartshorn.events.GenericEvent;
import org.dockbox.hartshorn.events.annotations.Listener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

public class GenericEventListener {

    @Getter private static final List<Object> objects = new CopyOnWriteArrayList<>();

    @Listener
    public void onString(final GenericEvent<String> event) {
        if (event.value() instanceof String) return;
        throw new IllegalArgumentException("Expected type to be String, but got " + event.value().getClass());
    }

    @Listener
    public void onInteger(final GenericEvent<Integer> event) {
        if (event.value() instanceof Integer) return;
        throw new IllegalArgumentException("Expected type to be Integer, but got " + event.value().getClass());
    }

    @Listener
    public void on(final GenericEvent<?> event) {
        objects.add(event.value());
    }

}
