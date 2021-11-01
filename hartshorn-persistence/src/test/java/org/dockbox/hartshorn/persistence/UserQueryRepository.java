package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.persistence.annotations.EntityModifier;
import org.dockbox.hartshorn.persistence.annotations.Query;
import org.dockbox.hartshorn.persistence.annotations.Transactional;
import org.dockbox.hartshorn.persistence.objects.JpaUser;

import java.util.List;

@Service
public interface UserQueryRepository extends JpaRepository<JpaUser, Long> {

    @Query("select u from JpaUser u where u.age >= 18")
    List<JpaUser> findAdults();

    @Transactional
    @EntityModifier
    @Query("delete from JpaUser u")
    void deleteAll();

    @EntityModifier
    @Query("update JpaUser u set u.age = :age where u.id = :id")
    int nonTransactionalEntityUpdate(long id, int age);

    @Transactional
    @Query("update JpaUser u set u.age = :age where u.id = :id")
    int nonModifierEntityUpdate(long id, int age);

    @EntityModifier
    @Transactional
    @Query("update JpaUser u set u.age = :age where u.id = :id")
    int entityUpdate(long id, int age);
}
