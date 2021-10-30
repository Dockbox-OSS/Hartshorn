package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.core.annotations.service.Service;

@Service
public interface UserJpaRepository extends JpaRepository<User, Long> {
}
