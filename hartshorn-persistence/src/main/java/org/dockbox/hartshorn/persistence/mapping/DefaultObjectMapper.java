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

package org.dockbox.hartshorn.persistence.mapping;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.properties.PersistenceModifier;
import org.dockbox.hartshorn.persistence.properties.PersistenceProperty;

import java.util.List;

public abstract class DefaultObjectMapper implements ObjectMapper {

    protected FileType fileType;

    protected DefaultObjectMapper(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public FileType getFileType() {
        return this.fileType;
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) throws ApplicationException {
        final List<PersistenceProperty> persistenceProperties = Bindings.properties(PersistenceProperty.KEY, properties);
        for (PersistenceProperty persistenceProperty : persistenceProperties) {
            for (PersistenceModifier modifier : persistenceProperty.getObject()) this.modify(modifier);
        }
    }

    protected abstract void modify(PersistenceModifier modifier);
}
