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

package org.dockbox.selene.integrated.sql.sqlite;

import org.dockbox.selene.integrated.sql.exceptions.TableMoficationException;
import org.javalite.activejdbc.MetaModel;
import org.javalite.activejdbc.Model;

import java.lang.reflect.Field;
import java.util.Map;

public class DynamicModel extends Model {

    @Override
    public Map<String, Object> getAttributes() {
        return super.getAttributes();
    }

    public void setTableName(String name) throws TableMoficationException {
        try {
            final Field metaModel = Model.class.getDeclaredField("metaModelLocal");
            metaModel.setAccessible(true);
            final Field tableName = MetaModel.class.getDeclaredField("tableName");
            tableName.setAccessible(true);

            MetaModel metaModelInstance = (MetaModel) metaModel.get(this);

            tableName.set(metaModelInstance, name);
            metaModel.set(this, metaModelInstance);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            throw new TableMoficationException("Could not restore table name for '" + name + "' on " + this, e);
        }
    }

}
