/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.persistence.jpa;

import org.dockbox.hartshorn.core.context.ContextCarrier;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.persistence.properties.PersistenceConnection;

import java.util.Set;

import javax.persistence.EntityManager;

public interface JpaRepository<T, ID> extends ContextCarrier {

    void save(final T object);

    void update(T object);

    void updateOrSave(T object);

    void delete(T object);

    Set<T> findAll();

    Exceptional<T> findById(ID id);

    EntityManager entityManager();

    Class<T> reify();

    void flush();

    JpaRepository<T, ID> connection(PersistenceConnection connection);
}
