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

package test.org.dockbox.hartshorn.launchpad;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class OutputCaptureAppender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> events = new ArrayList<>();

    public static OutputCaptureAppender registerForLogger(Logger logger) {
        OutputCaptureAppender appender = new OutputCaptureAppender();
        appender.start();
        ((ch.qos.logback.classic.Logger) logger).addAppender(appender);
        return appender;
    }

    public List<ILoggingEvent> events() {
        return this.events;
    }

    @Override
    protected void append(ILoggingEvent e) {
        this.events.add(e);
    }
}
