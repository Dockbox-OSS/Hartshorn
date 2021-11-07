package org.dockbox.hartshorn.persistence.service;

import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.persistence.JpaRepository;

@Service
public interface JpaRepositoryFactory {
    @Factory
    <T> JpaRepository<T, ?> repository(Class<T> type);
}
