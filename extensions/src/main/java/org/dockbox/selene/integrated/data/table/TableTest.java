package org.dockbox.selene.integrated.data.table;

import org.junit.Test;

public class TableTest {

    @Test
    public void test() {
        Table table = new Table(Identity.UUID, Identity.NAME);
        table.addRow(new User(1, "pumbas600"))
            .addRow(2, "Diggy")
            .addRow("3", 1);

        table.getRows().forEach(r -> {
            r.values().forEach(v -> System.out.print(String.format("Class (%s): %s, ", v.getClass(), v)));
            System.out.println();
        });
    }
}
