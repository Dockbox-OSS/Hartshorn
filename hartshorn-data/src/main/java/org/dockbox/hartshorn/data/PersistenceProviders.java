package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.jpa.JpaParameterLoader;

@Service(activators = UsePersistence.class)
public class PersistenceProviders {

    @Provider("jpa_query")
    public ParameterLoader jpaParameterLoader() {
        return new JpaParameterLoader();
    }

}
