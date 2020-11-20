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
import org.dockbox.selene.integrated.data.table.objects.WronglyIdentifiedUser;
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

        ColumnIdentifier<?>[] expectedColumns = {TestColumnIdentifiers.NAME};

        Table selectedTable = table.select(TestColumnIdentifiers.NAME);

        // Check if table's identifiers have been removed properly
        Assert.assertArrayEquals(expectedColumns, selectedTable.getIdentifiers());
        // Check if table rows' columns have been removed properly
        Assert.assertEquals(1, this.getTable().getRows().get(0).getColumns().size());
    }

    @Test
    public void testWorkingIdentifiedFields() {
        Table table = this.getTable();
        table.addRow(new IdentifiedUser(1, "coulis"));

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals("coulis", row.getValue(TestColumnIdentifiers.NAME).get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWronglyIdentifiedFields() {
        Table table = this.getTable();
        table.addRow(new WronglyIdentifiedUser(1, "coulis"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionOnTypeMismatch() {
        Table table = this.getTable();
        table.addRow("3", 1);
    }

    @Test
    public void testAcceptsTypeFields() {
        Table table = this.getTable();
        table.addRow(new User(1, "pumbas600"));

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals("pumbas600", row.getValue(TestColumnIdentifiers.NAME).get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionOnMissingTypeField() {
        Table table = new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.NAME, TestColumnIdentifiers.UUID);
        table.addRow(new User(1, "pumbas600"));

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals("pumbas600", row.getValue(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testAcceptsValidVarargs() {
        Table table = this.getTable();
        table.addRow(2, "Diggy");

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals("Diggy", row.getValue(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testOrderByDesc() {
        Table table = this.getTable();
        table.addRow(2, "Diggy");
        table.addRow(3, "pumbas600");
        table.addRow(1, "coulis");

        table.orderBy(TestColumnIdentifiers.NUMERAL_ID, Orders.DESC); // Expected: 3, 2, 1
        Assert.assertSame(3, table.first().get().getValue(TestColumnIdentifiers.NUMERAL_ID).get());
        Assert.assertSame(1, table.last().get().getValue(TestColumnIdentifiers.NUMERAL_ID).get());
    }

    @Test
    public void testOrderByAsc() {
        Table table = this.getTable();
        table.addRow(2, "Diggy");
        table.addRow(3, "pumbas600");
        table.addRow(1, "coulis");

        table.orderBy(TestColumnIdentifiers.NUMERAL_ID, Orders.ASC); // Expected: 1, 2, 3
        Assert.assertSame(1, table.first().get().getValue(TestColumnIdentifiers.NUMERAL_ID).get());
        Assert.assertSame(3, table.last().get().getValue(TestColumnIdentifiers.NUMERAL_ID).get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrderByDoesNotAcceptInvalidColumn() {
        Table table = this.getTable();
        table.addRow(2, "Diggy");
        table.orderBy(TestColumnIdentifiers.UUID, Orders.ASC);
    }

    // TODO, Where and merge tests (also see TODO's in Table for Merge/where methods)
}
