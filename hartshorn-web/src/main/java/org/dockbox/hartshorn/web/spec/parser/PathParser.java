package org.dockbox.hartshorn.web.spec.parser;

import org.dockbox.hartshorn.web.spec.PathSpec;

public interface PathParser {

    PathSpec parse(String pattern);
}
