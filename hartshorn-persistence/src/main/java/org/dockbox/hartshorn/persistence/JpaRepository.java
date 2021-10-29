package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.properties.AttributeHolder;

import java.util.Set;

public interface JpaRepository extends AttributeHolder {

    void save(Object object);

    void update(Object object);

    void updateOrSave(Object object);

    void delete(Object object);

    <T> Set<T> findAll(Class<T> type);

    <T> Exceptional<T> findById(Class<T> type, Object id);
}
