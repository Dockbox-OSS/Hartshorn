/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.reporting;

/**
 * A consumer that writes to a {@link DiagnosticsPropertyWriter}, typically used for delegation of
 * writing to a specific type.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public interface DiagnosticsPropertyWriterConsumer {

    /**
     * Writes properties to the given {@link DiagnosticsPropertyWriter}.
     *
     * @param writer The writer to which properties should be written.
     */
    void writeTo(DiagnosticsPropertyWriter writer);
}
