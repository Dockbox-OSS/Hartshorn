package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.processing.Provider;

@Service
public class PersonProviders {

    @Provider
    public Class<? extends Person> person = PersonImpl.class;

    @Provider
    public Class<? extends User> user = BoundUserImpl.class;
}
