package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Map;

public interface ViewModel {
    Map<String, Object> attributes();
    void attribute(String name, Object value);
    Exceptional<Object> attribute(String name);
}
