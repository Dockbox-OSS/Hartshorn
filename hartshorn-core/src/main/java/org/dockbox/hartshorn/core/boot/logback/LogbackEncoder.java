package org.dockbox.hartshorn.core.boot.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

public class LogbackEncoder extends PatternLayoutEncoder {

    @Override
    public void start() {
        PatternLayout.DEFAULT_CONVERTER_MAP.put("process_id", LogbackPIDConverter.class.getName());
        super.start();
    }
}
