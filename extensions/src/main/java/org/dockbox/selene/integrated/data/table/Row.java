package org.dockbox.selene.integrated.data.table;


import java.util.HashMap;

public class Row extends HashMap<ColumnIdentifier<?>, Object>{

    public Row() {
        // Eventually?
    }

    public <T> Row addValue(ColumnIdentifier<T> column, T value) {
        super.put(column, value);
        return this;
    }

    public <T> T getValue(ColumnIdentifier<T> column) {
        return (T) super.get(column);
    }

}
