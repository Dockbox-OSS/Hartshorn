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

import org.junit.Assert;
import org.junit.Test;

public class TableTest {

    private Table getTable() {
        return new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionOnTypeMismatch() {
        Table table = getTable();
        getTable().addRow("3", 1);
    }

    @Test
    public void testAcceptsTypeFields() {
        Table table = getTable();
        table.addRow(new User(1, "pumbas600"));

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals(row.getValue(TestColumnIdentifiers.NAME), "pumbas600");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionOnMissingTypeField() {
        Table table = new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.NAME, TestColumnIdentifiers.UUID);
        table.addRow(new User(1, "pumbas600"));

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals(row.getValue(TestColumnIdentifiers.NAME), "pumbas600");
    }

    @Test
    public void testAcceptsValidVarargs() {
        Table table = getTable();
        table.addRow(2, "Diggy");

        Assert.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assert.assertEquals(row.getValue(TestColumnIdentifiers.NAME), "Diggy");
    }

    // TODO, Lookup and merge tests (also see TODO's in Table for Merge/Lookup methods)
}
