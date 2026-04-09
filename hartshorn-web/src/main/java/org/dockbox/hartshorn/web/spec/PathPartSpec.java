package org.dockbox.hartshorn.web.spec;

import java.util.Map;

public interface PathPartSpec {

    String stringValue();

    boolean matches(String pathPart, Map<String, String> pathParameters);
}
