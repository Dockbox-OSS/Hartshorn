package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Map;

import lombok.Getter;

public class ViewModelImpl implements ViewModel {

    @Getter
    private final Map<String, Object> attributes = HartshornUtils.emptyConcurrentMap();

    @Override
    public void attribute(final String name, final Object value) {
        this.attributes.put(name, value);
    }

    @Override
    public Exceptional<Object> attribute(final String name) {
        return Exceptional.of(this.attributes.get(name));
    }
}
