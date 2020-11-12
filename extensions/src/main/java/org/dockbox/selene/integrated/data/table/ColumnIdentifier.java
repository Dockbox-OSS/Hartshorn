package org.dockbox.selene.integrated.data.table;

@FunctionalInterface
public interface ColumnIdentifier<T> {

    String getColumnName();
}
