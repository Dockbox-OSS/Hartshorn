package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.types.User;

@Service
public interface SampleFactoryService {
    @Factory
    public User user(String name);
}
