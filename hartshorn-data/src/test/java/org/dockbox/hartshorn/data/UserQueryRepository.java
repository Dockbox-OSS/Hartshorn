/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.data.annotations.EntityModifier;
import org.dockbox.hartshorn.data.annotations.Query;
import org.dockbox.hartshorn.data.annotations.Transactional;
import org.dockbox.hartshorn.data.jpa.JpaRepository;
import org.dockbox.hartshorn.data.objects.JpaUser;

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
