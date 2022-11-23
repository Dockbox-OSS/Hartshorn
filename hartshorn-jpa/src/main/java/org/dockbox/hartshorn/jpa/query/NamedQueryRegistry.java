package org.dockbox.hartshorn.jpa.query;

import jakarta.persistence.Query;

public interface NamedQueryRegistry {

    boolean has(String name);

    void register(String name, Query query);

}
