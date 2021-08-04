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

package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.properties.InjectableType;

import java.util.Set;

/**
 * Low-level interface for communicating with SQL databases.
 *
 * @param <T>
 *         The type of the target database source
 */
public interface SqlService extends InjectableType {

    void save(Object object);
    void update(Object object);
    void updateOrSave(Object object);
    void delete(Object object);
    <T> Set<T> findAll(Class<T> type);
    <T>Exceptional<T> findById(Class<T> type, Object id);

}