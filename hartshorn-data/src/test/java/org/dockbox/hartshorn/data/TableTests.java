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

package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.data.exceptions.EmptyEntryException;
import org.dockbox.hartshorn.data.exceptions.IdentifierMismatchException;
import org.dockbox.hartshorn.data.exceptions.UnknownIdentifierException;
import org.dockbox.hartshorn.data.identifiers.TestColumnIdentifiers;
import org.dockbox.hartshorn.data.objects.IdentifiedUser;
import org.dockbox.hartshorn.data.objects.User;
import org.dockbox.hartshorn.data.objects.WronglyIdentifiedUser;
import org.dockbox.hartshorn.data.table.ColumnIdentifier;
import org.dockbox.hartshorn.data.table.Merge;
import org.dockbox.hartshorn.data.table.Order;
import org.dockbox.hartshorn.data.table.Table;
import org.dockbox.hartshorn.data.table.TableRow;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class TableTests extends ApplicationAwareTest {

    @Test
    public void testSelectColumnOfTable() {
        final Table table = this.createTestTable();
        table.addRow(new IdentifiedUser(1, "coulis"));

        final ColumnIdentifier<?>[] expectedColumns = { TestColumnIdentifiers.NAME };

        final Table selectedTable = table.select(TestColumnIdentifiers.NAME);

        // Check if table's identifiers have been removed properly
        Assertions.assertArrayEquals(expectedColumns, selectedTable.identifiers());
        // Check if table rows' columns have been removed properly
        Assertions.assertEquals(1, selectedTable.rows().get(0).columns().size());
    }

    private Table createTestTable() {
        return new Table(TestColumnIdentifiers.NUMERAL_ID, TestColumnIdentifiers.NAME);
    }

    @Test
    public void testWorkingIdentifiedFields() {
        final Table table = this.createTestTable();
        table.addRow(new IdentifiedUser(1, "coulis"));

        Assertions.assertEquals(1, table.rows().size());
        final TableRow row = table.rows().get(0);
        Assertions.assertEquals("coulis", row.value(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testWronglyIdentifiedFields() {
        Assertions.assertThrows(UnknownIdentifierException.class, () -> {
            final Table table = this.createTestTable();
            table.addRow(new WronglyIdentifiedUser(1, "coulis"));
        });
    }

    @Test
    public void testThrowsExceptionOnTypeMismatch() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            final Table table = this.createTestTable();
            table.addRow("3", 1);
        });
    }

    @Test
    public void testAcceptsTypeFields() {
        final Table table = this.createTestTable();
        table.addRow(new User(1, "pumbas600"));

        Assertions.assertEquals(1, table.rows().size());
        final TableRow row = table.rows().get(0);
        Assertions.assertEquals("pumbas600", row.value(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testThrowsExceptionOnMissingTypeField() {
        Assertions.assertThrows(UnknownIdentifierException.class, () -> {
            final Table table = new Table(
                    TestColumnIdentifiers.NUMERAL_ID,
                    TestColumnIdentifiers.NAME,
                    TestColumnIdentifiers.UUID);
            table.addRow(new User(1, "pumbas600"));

            Assertions.assertEquals(1, table.rows().size());
            final TableRow row = table.rows().get(0);
            Assertions.assertEquals("pumbas600", row.value(TestColumnIdentifiers.NAME).get());
        });
    }

    @Test
    public void testAcceptsValidVarargs() {
        final Table table = this.createTestTable();
        table.addRow(2, "Diggy");

        Assertions.assertEquals(1, table.rows().size());
        final TableRow row = table.rows().get(0);
        Assertions.assertEquals("Diggy", row.value(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testOrderByDesc() {
        final Table table = this.createTestTable();
        table.addRow(2, "Diggy");
        table.addRow(3, "pumbas600");
        table.addRow(1, "coulis");

        table.orderBy(TestColumnIdentifiers.NUMERAL_ID, Order.DESC); // Expected: 3, 2, 1
        Assertions.assertSame(3, table.first().get().value(TestColumnIdentifiers.NUMERAL_ID).get());
        Assertions.assertSame(1, table.last().get().value(TestColumnIdentifiers.NUMERAL_ID).get());
    }

    @Test
    public void testOrderByAsc() {
        final Table table = this.createTestTable();
        table.addRow(2, "Diggy");
        table.addRow(3, "pumbas600");
        table.addRow(1, "coulis");

        table.orderBy(TestColumnIdentifiers.NUMERAL_ID, Order.ASC); // Expected: 1, 2, 3
        Assertions.assertSame(1, table.first().get().value(TestColumnIdentifiers.NUMERAL_ID).get());
        Assertions.assertSame(3, table.last().get().value(TestColumnIdentifiers.NUMERAL_ID).get());
    }

    @Test
    public void testOrderByDoesNotAcceptInvalidColumn() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            final Table table = this.createTestTable();
            table.addRow(2, "Diggy");
            table.orderBy(TestColumnIdentifiers.UUID, Order.ASC);
        });
    }

    @Test
    public void testWhere() {
        final Table table = this.createTestTable();
        table.addRow(2, "Diggy");
        table.addRow(3, "pumbas600");
        table.addRow(1, "coulis");

        final Table where = table.where(TestColumnIdentifiers.NUMERAL_ID, 1);
        Assertions.assertEquals(1, where.rows().size());
    }

    @Test
    public void testJoinPreferOriginal() throws EmptyEntryException, IdentifierMismatchException {
        final Table original = this.createTestTable();
        original.addRow(1, "coulis");
        original.addRow(2, "NotDiggy");

        final Table other = this.createTestTable();
        other.addRow(2, "Diggy");
        other.addRow(3, "pumbas600");

        final Table joined = original.join(other, TestColumnIdentifiers.NUMERAL_ID, Merge.PREFER_ORIGINAL);
        final Table whereLookup = joined.where(TestColumnIdentifiers.NUMERAL_ID, 2);
        Assertions.assertEquals(3, joined.rows().size());
        Assertions.assertEquals("NotDiggy", whereLookup.first().get().value(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testJoinPreferForeign() throws EmptyEntryException, IdentifierMismatchException {
        final Table original = this.createTestTable();
        original.addRow(1, "coulis");
        original.addRow(2, "NotDiggy");

        final Table other = this.createTestTable();
        other.addRow(2, "Diggy");
        other.addRow(3, "pumbas600");

        final Table joined = original.join(other, TestColumnIdentifiers.NUMERAL_ID, Merge.PREFER_FOREIGN);
        final Table whereLookup = joined.where(TestColumnIdentifiers.NUMERAL_ID, 2);
        Assertions.assertEquals(3, joined.rows().size());
        Assertions.assertEquals("Diggy", whereLookup.first().get().value(TestColumnIdentifiers.NAME).get());
    }

    @Test
    public void testJoinThrowsExceptionOnEmptyEntry() {
        Assertions.assertThrows(EmptyEntryException.class, () -> {
            final Table original = this.createTestTable();
            original.addRow(1, "coulis");
            original.addRow(2, "NotDiggy");

            final Table other = new Table(
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
        final Table original = this.createTestTable();
        original.addRow(1, "coulis");
        original.addRow(2, "NotDiggy");

        final Table other = new Table(
                TestColumnIdentifiers.NUMERAL_ID,
                TestColumnIdentifiers.NAME,
                TestColumnIdentifiers.UUID);
        other.addRow(2, "Diggy", UUID.randomUUID());
        other.addRow(3, "pumbas600", UUID.randomUUID());

        final Table joined = original.join(other, TestColumnIdentifiers.NUMERAL_ID, Merge.PREFER_FOREIGN, true);
        final Table whereLookup = joined.where(TestColumnIdentifiers.NUMERAL_ID, 1);
        Assertions.assertFalse(
                whereLookup.first().get().value(TestColumnIdentifiers.UUID).present());
    }
}
