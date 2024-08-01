/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util;

import java.util.UUID;

/**
 * A single {@link Subject} which can be identified by its name and unique ID.
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public interface Identifiable extends Subject {

    /**
     * Gets the unique ID of the subject
     *
     * @return The unique ID
     */
    UUID uniqueId();

    /**
     * Gets the name of the subject
     *
     * @return The name
     */
    String name();
}
