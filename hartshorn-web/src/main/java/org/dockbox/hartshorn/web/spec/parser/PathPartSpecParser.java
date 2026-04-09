package org.dockbox.hartshorn.web.spec.parser;

import org.dockbox.hartshorn.web.spec.PathPartSpec;

public interface PathPartSpecParser {

    PathPartSpec parse(String part);
}
