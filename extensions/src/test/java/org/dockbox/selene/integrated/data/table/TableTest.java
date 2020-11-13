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

package org.dockbox.selene.integrated.data.table;

import org.dockbox.selene.integrated.data.table.column.ColumnIdentifier;
import org.dockbox.selene.integrated.data.table.identifiers.TestColumnIdentifiers;
import org.dockbox.selene.integrated.data.table.objects.IdentifiedUser;
import org.dockbox.selene.integrated.data.table.objects.User;
import org.junit.Assert;
import org.junit.Test;

public class TableTest {

    private Table getTable() {
        return new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.NAME);
    }

    @Test
    public void testSelectColumnOfTable() {
        Table table = this.getTable();
        table.addRow(new IdentifiedUser(1, "coulis"));

        ColumnIdentifier<?>[] supposedColumns = {TestColumnIdentifiers.NAME};

        Table selectedTable = table.select(TestColumnIdentifiers.NAME);

        // Check if table's identifiers have been removed properly
        Assert.assertArrayEquals(selectedTable.getIdentifiers(), supposedColumns);
        // Check if table rows' columns have been removed properly
        Assert.assertEquals(this.getTable().getRows().get(0).getColumns().size(), 1);
    }

    @Test
    public void testWorkingIdentifiedFields() {
        Table table = this.getTable();
        table.addRow(new IdentifiedUser(1, "coulis"));

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals(row.getValue(TestColumnIdentifiers.NAME).get(), "coulis");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWronglyIdentifiedFields() {
        Table table = this.getTable();
        table.addRow(new IdentifiedUser(1, "coulis"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionOnTypeMismatch() {
        Table table = getTable();
        table.addRow("3", 1);
    }

    @Test
    public void testAcceptsTypeFields() {
        Table table = this.getTable();
        table.addRow(new User(1, "pumbas600"));

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals(row.getValue(TestColumnIdentifiers.NAME).get(), "pumbas600");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionOnMissingTypeField() {
        Table table = new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.NAME, TestColumnIdentifiers.UUID);
        table.addRow(new User(1, "pumbas600"));

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals(row.getValue(TestColumnIdentifiers.NAME).get(), "pumbas600");
    }

    @Test
    public void testAcceptsValidVarargs() {
        Table table = this.getTable();
        table.addRow(2, "Diggy");

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals(row.getValue(TestColumnIdentifiers.NAME).get(), "Diggy");
    }

    // TODO, Where and merge tests (also see TODO's in Table for Merge/where methods)
}
