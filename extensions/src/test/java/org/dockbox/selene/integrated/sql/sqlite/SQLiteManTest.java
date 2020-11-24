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

import org.dockbox.selene.core.impl.server.properties.SimpleProperty;
import org.dockbox.selene.integrated.data.table.Table;
import org.dockbox.selene.integrated.sql.SQLMan;
import org.dockbox.selene.integrated.sql.exceptions.InvalidConnectionException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.File;

class SQLiteManTest {

    private SQLMan getSQLiteMan() {
        String path = "src/test/resources/storage.db";
        File file = new File(path);
        SQLiteMan man = new SQLiteMan();
        man.stateEnabling(new SimpleProperty<>(file.toPath(), SQLiteMan.PATH_KEY));
        return man;
    }

    @Test
    public void testGetTable() throws InvalidConnectionException {
        SQLMan man = this.getSQLiteMan();
        Table plots = man.getTable("plot");
        Assert.assertEquals(2301, plots.count());
    }

}
