package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.core.context.ContextCarrier;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.properties.AttributeHolder;

import java.util.Set;

import javax.persistence.EntityManager;

public interface JpaRepository<T, ID> extends AttributeHolder, ContextCarrier {

    void save(final T object);

    void update(T object);

    void updateOrSave(T object);

    void delete(T object);

    Set<T> findAll();

    Exceptional<T> findById(ID id);

    EntityManager entityManager();
}
