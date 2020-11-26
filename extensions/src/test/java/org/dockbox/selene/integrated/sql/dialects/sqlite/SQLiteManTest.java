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

package org.dockbox.selene.integrated.sql.dialects.sqlite;

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.integrated.data.table.Table;
import org.dockbox.selene.integrated.data.table.identifiers.TestColumnIdentifiers;
import org.dockbox.selene.integrated.sql.SQLMan;
import org.dockbox.selene.integrated.sql.exceptions.InvalidConnectionException;
import org.dockbox.selene.integrated.sql.properties.SQLColumnProperty;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

class SQLiteManTest {

    private Path getTestFile() {
        String path = "src/test/resources/storage.db";
        return new File(path).toPath();
    }

    private SQLiteMan getSQLiteMan(boolean enableState) {
        SQLiteMan man = new SQLiteMan();
        if (enableState)
            man.stateEnabling(new SQLitePathProperty(this.getTestFile()));
        return man;
    }

    @Test
    public void testGetTable() throws InvalidConnectionException {
        SQLMan<?> man = this.getSQLiteMan(true);
        Table plots = man.getTable("plot");
        Assert.assertEquals(2301, plots.count());
    }

    @Test
    public void testIdentifierProperties() throws InvalidConnectionException {
        SQLiteMan man = this.getSQLiteMan(false);
        man.stateEnabling(
                new SQLitePathProperty(this.getTestFile()),
                new SQLColumnProperty("world", SQLiteColumnIdentifiers.PLOT_WORLD)
        );
        Table plots = man.getTable("plot");
        Table worlds = plots.where(SQLiteColumnIdentifiers.PLOT_WORLD, "*");
        Assert.assertEquals(161, worlds.count());
    }

    @Test
    public void testTableConversion() throws InvalidConnectionException {
        SQLiteMan man = this.getSQLiteMan(true);
        Table plots = man.getTable("plot");

        long count = plots.getRowsAs(PlotEntry.class).stream()
                .filter(Exceptional::isPresent)
                .count();
        Assert.assertEquals(2301, count);
    }

    @Test
    void testStore() throws InvalidConnectionException {
        Table table = new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.NAME);
        SQLiteMan man = this.getSQLiteMan(false);
        man.stateEnabling(
                new SQLitePathProperty(this.getTestFile()),
                new SQLColumnProperty("numeralId", TestColumnIdentifiers.NUMERAL_ID));

        table.addRow(2, "Diggy");
        table.addRow(3, "pumbas600");
        table.addRow(1, "coulis");

        man.store("developers", table);
        man.drop("developers");
    }
}
