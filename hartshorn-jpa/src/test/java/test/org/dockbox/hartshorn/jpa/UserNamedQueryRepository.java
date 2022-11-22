package test.org.dockbox.hartshorn.jpa;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.jpa.annotations.DataSource;

@Service(lazy = true)
@DataSource("users")
public interface UserNamedQueryRepository extends JpaRepository<UserWithNamedQuery, Long> {
    UserWithNamedQuery findWaldo();
}
