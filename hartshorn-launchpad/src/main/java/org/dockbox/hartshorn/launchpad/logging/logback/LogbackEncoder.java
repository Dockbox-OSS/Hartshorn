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

package org.dockbox.hartshorn.launchpad.logging.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

/**
 * A custom {@link PatternLayoutEncoder} that allows for the use of the active process ID
 * in the logback configuration, using the syntax {@code %process_id}.
 *
 * @see LogbackPIDConverter
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public class LogbackEncoder extends PatternLayoutEncoder {

    @Override
    public void start() {
        PatternLayout.DEFAULT_CONVERTER_MAP.put("process_id", LogbackPIDConverter.class.getName());
        super.start();
    }
}
