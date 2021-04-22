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

package org.dockbox.selene.database;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.database.dialects.sqlite.SQLiteMan;
import org.dockbox.selene.database.dialects.sqlite.SQLitePathProperty;
import org.dockbox.selene.database.exceptions.InvalidConnectionException;
import org.dockbox.selene.api.domain.table.Table;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.jooq.SQLDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@ExtendWith(SeleneJUnit5Runner.class)
class SQLiteManTest {

    private static final String MAIN_TABLE = "developers";
    private static final String ID_TABLE = "identifiers";
    private static final UUID uuidOne = UUID.fromString("af50f8a9-20a3-438b-8562-1999fdb7a995");
    private static final UUID uuidTwo = UUID.fromString("0f3ff86e-b6c3-4dd0-8744-274db3d7018f");
    private static final UUID uuidThree = UUID.fromString("ef9f3ad4-a397-4895-97fe-10a549f5bc6e");

    @Test
    void testCanWriteToFile() {
        Assertions.assertDoesNotThrow(
                (ThrowingSupplier<? extends ISQLMan<?>>) SQLiteManTest::writeToFile);
    }

    private static ISQLMan<?> writeToFile() throws InvalidConnectionException {
        ISQLMan<?> man = SQLiteManTest.getSQLiteMan();
        return writeToFile(man);
    }

    private static SQLiteMan getSQLiteMan() {
        SQLiteMan man = new SQLiteMan();
        man.stateEnabling(new SQLitePathProperty(SQLiteManTest.getTestFile()));
        return man;
    }

    private static ISQLMan<?> writeToFile(ISQLMan<?> man) throws InvalidConnectionException {
        Table main = SQLiteManTest.getPopulatedMainTable();
        man.store(MAIN_TABLE, main);
        Table ids = SQLiteManTest.getPopulatedIdTable();
        man.store(ID_TABLE, ids);
        return man;
    }

    private static Path getTestFile() {
        try {
            File tempFile = File.createTempFile("sqlite-", ".db");
            tempFile.deleteOnExit();
            return tempFile.toPath();
        }
        catch (Exception e) {
            Assumptions.assumeTrue(false);
            //noinspection ReturnOfNull
            return null;
        }
    }

    private static Table getPopulatedMainTable() {
        Table table = new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.NAME);
        table.addRow(2, "Diggy");
        table.addRow(3, "pumbas600");
        table.addRow(1, "coulis");
        return table;
    }

    private static Table getPopulatedIdTable() {
        Table table = new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.UUID);
        table.addRow(1, uuidOne);
        table.addRow(2, uuidTwo);
        table.addRow(3, uuidThree);
        return table;
    }

    @Test
    public void testGetTable() throws InvalidConnectionException {
        ISQLMan<?> man = SQLiteManTest.writeToFile();
        Table table = man.getTable(MAIN_TABLE);
        Assertions.assertEquals(SQLiteManTest.getPopulatedMainTable().count(), table.count());
    }

    @Test
    public void testIdentifierProperties() throws InvalidConnectionException {
        ISQLMan<?> man = SQLiteManTest.writeToFile();
        Table table = man.getTable(MAIN_TABLE);
        Table developers = table.where(TestColumnIdentifiers.NUMERAL_ID, 1);
        Assertions.assertEquals(1, developers.count());
    }

    @Test
    public void testTableConversion() throws InvalidConnectionException, IOException {
        ISQLMan<?> man = SQLiteManTest.writeToFile();
        Table table = man.getTable(MAIN_TABLE);

        long count = table.getRowsAs(Developer.class).stream().filter(Exceptional::present).count();
        Assertions.assertEquals(3, count);
    }

    @Test
    void getDialectReturnsSQLite() {
        SQLMan<?> man = SQLiteManTest.getSQLiteMan();
        Assertions.assertEquals(SQLDialect.SQLITE, man.getDialect());
    }

    @Test
    void tablesCanBeDropped() throws InvalidConnectionException {
        ISQLMan<?> man = SQLiteManTest.writeToFile();
        man.drop(ID_TABLE);
        Assertions.assertTrue(man.getTableSafe(ID_TABLE).absent());
    }

    @Test
    void rowsCanBeRemovedByFilter() throws InvalidConnectionException {
        ISQLMan<?> man = SQLiteManTest.writeToFile();
        man.deleteIf(ID_TABLE, TestColumnIdentifiers.NUMERAL_ID, 1);
        Table filteredTable = man.getTable(ID_TABLE).where(TestColumnIdentifiers.NUMERAL_ID, 1);
        Assertions.assertEquals(0, filteredTable.count());
    }
}
