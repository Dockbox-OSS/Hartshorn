package org.dockbox.selene.integrated.data.table;

public class Identity<T> implements ColumnIdentifier<T> {
    public static final Identity<Integer> UUID = new Identity<>("UUID");
    public static final Identity<String> NAME = new Identity<>("NAME");
    public static final Identity<String> NICK = new Identity<>("NICK");

    private String name;

    public Identity(String name) {
        this.name = name;
    }

    @Override
    public String getColumnName() {
        return this.name;
    }


}
