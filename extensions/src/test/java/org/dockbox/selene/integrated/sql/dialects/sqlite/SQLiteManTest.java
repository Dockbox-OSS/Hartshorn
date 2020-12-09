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

import com.google.common.io.Files;

import org.dockbox.selene.core.exceptions.global.UncheckedSeleneException;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.integrated.data.table.Table;
import org.dockbox.selene.integrated.data.table.identifiers.TestColumnIdentifiers;
import org.dockbox.selene.integrated.sql.ISQLMan;
import org.dockbox.selene.integrated.sql.exceptions.InvalidConnectionException;
import org.dockbox.selene.integrated.sql.properties.SQLColumnProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

class SQLiteManTest {

    private static Path file;

    private Path getTestFile() {
        return SQLiteManTest.file;
    }

    @BeforeAll
    public static void prepare() {
        String path = "src/test/resources/";
        File file = new File(path + "storage.db");
        try {
            File copy = File.createTempFile("tmp", null);
            Files.copy(file, copy);
            SQLiteManTest.file = copy.toPath();
        } catch (IOException e) {
            throw new UncheckedSeleneException(e);
        }
    }

    private ISQLMan<?> getSQLiteMan(boolean enableState) {
        ISQLMan<?> man = new SQLiteMan();
        if (enableState)
            man.stateEnabling(new SQLitePathProperty(this.getTestFile()));
        return man;
    }

    @Test
    public void testGetTable() throws InvalidConnectionException {
        ISQLMan<?> man = this.getSQLiteMan(true);
        Table plots = man.getTable("plot");
        Assertions.assertEquals(2301, plots.count());
    }

    @Test
    public void testIdentifierProperties() throws InvalidConnectionException {
        ISQLMan<?> man = this.getSQLiteMan(false);
        man.stateEnabling(
                new SQLitePathProperty(this.getTestFile()),
                new SQLColumnProperty("world", SQLiteColumnIdentifiers.PLOT_WORLD)
        );
        Table plots = man.getTable("plot");
        Table worlds = plots.where(SQLiteColumnIdentifiers.PLOT_WORLD, "*");
        Assertions.assertEquals(161, worlds.count());
    }

    @Test
    public void testTableConversion() throws InvalidConnectionException {
        ISQLMan<?> man = this.getSQLiteMan(true);
        Table plots = man.getTable("plot");

        long count = plots.getRowsAs(PlotEntry.class).stream()
                .filter(Exceptional::isPresent)
                .count();
        Assertions.assertEquals(2301, count);
    }

    @Test
    void testStore() throws InvalidConnectionException {
        Table table = new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.NAME);
        ISQLMan<?> man = this.getSQLiteMan(false);
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
