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

import org.dockbox.hartshorn.persistence.table.Table;
import org.dockbox.hartshorn.persistence.table.TableRow;
import org.dockbox.hartshorn.persistence.table.behavior.Merge;
import org.dockbox.hartshorn.persistence.table.behavior.Order;
import org.dockbox.hartshorn.persistence.table.column.ColumnIdentifier;
import org.dockbox.hartshorn.persistence.table.exceptions.EmptyEntryException;
import org.dockbox.hartshorn.persistence.table.exceptions.IdentifierMismatchException;
import org.dockbox.hartshorn.persistence.table.exceptions.UnknownIdentifierException;
import org.dockbox.hartshorn.persistence.table.identifiers.TestColumnIdentifiers;
import org.dockbox.hartshorn.persistence.table.objects.IdentifiedUser;
import org.dockbox.hartshorn.persistence.table.objects.User;
import org.dockbox.hartshorn.persistence.table.objects.WronglyIdentifiedUser;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

@ExtendWith(HartshornRunner.class)
public class TableTests {

    @Test
    public void testSelectColumnOfTable() {
        Table table = this.createTestTable();
        table.addRow(new IdentifiedUser(1, "coulis"));

        ColumnIdentifier<?>[] expectedColumns = { TestColumnIdentifiers.NAME };

        Table selectedTable = table.select(TestColumnIdentifiers.NAME);

        // Check if table's identifiers have been removed properly
        Assertions.assertArrayEquals(expectedColumns, selectedTable.getIdentifiers());
        // Check if table rows' columns have been removed properly
        Assertions.assertEquals(1, selectedTable.getRows().get(0).getColumns().size());
    }

    private Table createTestTable() {
        return new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.NAME);
    }

    @Test
    public void testWorkingIdentifiedFields() {
        Table table = this.createTestTable();
        table.addRow(new IdentifiedUser(1, "coulis"));

        Assertions.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assertions.assertEquals("coulis", row.getValue(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testWronglyIdentifiedFields() {
        Assertions.assertThrows(UnknownIdentifierException.class, () -> {
            Table table = this.createTestTable();
            table.addRow(new WronglyIdentifiedUser(1, "coulis"));
        });
    }

    @Test
    public void testThrowsExceptionOnTypeMismatch() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Table table = this.createTestTable();
            table.addRow("3", 1);
        });
    }

    @Test
    public void testAcceptsTypeFields() {
        Table table = this.createTestTable();
        table.addRow(new User(1, "pumbas600"));

        Assertions.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assertions.assertEquals("pumbas600", row.getValue(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testThrowsExceptionOnMissingTypeField() {
        Assertions.assertThrows(UnknownIdentifierException.class, () -> {
            Table table = new Table(
                    TestColumnIdentifiers.NUMERAL_ID,
                    TestColumnIdentifiers.NAME,
                    TestColumnIdentifiers.UUID);
            table.addRow(new User(1, "pumbas600"));

            Assertions.assertEquals(1, table.getRows().size());
            TableRow row = table.getRows().get(0);
            Assertions.assertEquals("pumbas600", row.getValue(TestColumnIdentifiers.NAME).get());
        });
    }

    @Test
    public void testAcceptsValidVarargs() {
        Table table = this.createTestTable();
        table.addRow(2, "Diggy");

        Assertions.assertEquals(1, table.getRows().size());
        TableRow row = table.getRows().get(0);
        Assertions.assertEquals("Diggy", row.getValue(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testOrderByDesc() {
        Table table = this.createTestTable();
        table.addRow(2, "Diggy");
        table.addRow(3, "pumbas600");
        table.addRow(1, "coulis");

        table.orderBy(TestColumnIdentifiers.NUMERAL_ID, Order.DESC); // Expected: 3, 2, 1
        Assertions.assertSame(3, table.first().get().getValue(TestColumnIdentifiers.NUMERAL_ID).get());
        Assertions.assertSame(1, table.last().get().getValue(TestColumnIdentifiers.NUMERAL_ID).get());
    }

    @Test
    public void testOrderByAsc() {
        Table table = this.createTestTable();
        table.addRow(2, "Diggy");
        table.addRow(3, "pumbas600");
        table.addRow(1, "coulis");

        table.orderBy(TestColumnIdentifiers.NUMERAL_ID, Order.ASC); // Expected: 1, 2, 3
        Assertions.assertSame(1, table.first().get().getValue(TestColumnIdentifiers.NUMERAL_ID).get());
        Assertions.assertSame(3, table.last().get().getValue(TestColumnIdentifiers.NUMERAL_ID).get());
    }

    @Test
    public void testOrderByDoesNotAcceptInvalidColumn() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Table table = this.createTestTable();
            table.addRow(2, "Diggy");
            table.orderBy(TestColumnIdentifiers.UUID, Order.ASC);
        });
    }

    @Test
    public void testWhere() {
        Table table = this.createTestTable();
        table.addRow(2, "Diggy");
        table.addRow(3, "pumbas600");
        table.addRow(1, "coulis");

        Table where = table.where(TestColumnIdentifiers.NUMERAL_ID, 1);
        Assertions.assertEquals(1, where.getRows().size());
    }

    @Test
    public void testJoinPreferOriginal() throws EmptyEntryException, IdentifierMismatchException {
        Table original = this.createTestTable();
        original.addRow(1, "coulis");
        original.addRow(2, "NotDiggy");

        Table other = this.createTestTable();
        other.addRow(2, "Diggy");
        other.addRow(3, "pumbas600");

        Table joined = original.join(other, TestColumnIdentifiers.NUMERAL_ID, Merge.PREFER_ORIGINAL);
        Table whereLookup = joined.where(TestColumnIdentifiers.NUMERAL_ID, 2);
        Assertions.assertEquals(3, joined.getRows().size());
        Assertions.assertEquals("NotDiggy", whereLookup.first().get().getValue(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testJoinPreferForeign() throws EmptyEntryException, IdentifierMismatchException {
        Table original = this.createTestTable();
        original.addRow(1, "coulis");
        original.addRow(2, "NotDiggy");

        Table other = this.createTestTable();
        other.addRow(2, "Diggy");
        other.addRow(3, "pumbas600");

        Table joined = original.join(other, TestColumnIdentifiers.NUMERAL_ID, Merge.PREFER_FOREIGN);
        Table whereLookup = joined.where(TestColumnIdentifiers.NUMERAL_ID, 2);
        Assertions.assertEquals(3, joined.getRows().size());
        Assertions.assertEquals("Diggy", whereLookup.first().get().getValue(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testJoinThrowsExceptionOnEmptyEntry() {
        Assertions.assertThrows(EmptyEntryException.class, () -> {
            Table original = this.createTestTable();
            original.addRow(1, "coulis");
            original.addRow(2, "NotDiggy");

            Table other = new Table(
                    TestColumnIdentifiers.NUMERAL_ID,
                    TestColumnIdentifiers.NAME,
                    TestColumnIdentifiers.UUID);
            other.addRow(2, "Diggy", UUID.randomUUID());
            other.addRow(3, "pumbas600", UUID.randomUUID());

            original.join(other, TestColumnIdentifiers.NUMERAL_ID, Merge.PREFER_FOREIGN, false);
        });
    }

    @Test
    public void testJoinPopulatesNullOnEmptyEntry() throws EmptyEntryException, IdentifierMismatchException {
        Table original = this.createTestTable();
        original.addRow(1, "coulis");
        original.addRow(2, "NotDiggy");

        Table other = new Table(
                TestColumnIdentifiers.NUMERAL_ID,
                TestColumnIdentifiers.NAME,
                TestColumnIdentifiers.UUID);
        other.addRow(2, "Diggy", UUID.randomUUID());
        other.addRow(3, "pumbas600", UUID.randomUUID());

        Table joined = original.join(other, TestColumnIdentifiers.NUMERAL_ID, Merge.PREFER_FOREIGN, true);
        Table whereLookup = joined.where(TestColumnIdentifiers.NUMERAL_ID, 1);
        Assertions.assertFalse(
                whereLookup.first().get().getValue(TestColumnIdentifiers.UUID).present());
    }
}