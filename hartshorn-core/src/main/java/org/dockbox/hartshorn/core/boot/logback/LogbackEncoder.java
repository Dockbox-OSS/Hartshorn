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

package org.dockbox.hartshorn.core.boot.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

/**
 * A custom {@link PatternLayoutEncoder} that allows for the use of the active process ID
 * in the logback configuration, using the syntax {@code %process_id}.
 *
 * @author Guus Lieben
 * @since 21.9
 * @see LogbackPIDConverter
 */
public class LogbackEncoder extends PatternLayoutEncoder {

    @Override
    public void start() {
        PatternLayout.DEFAULT_CONVERTER_MAP.put("process_id", LogbackPIDConverter.class.getName());
        super.start();
    }
}
