package org.dockbox.selene.integrated.data.table.objects;

import org.dockbox.selene.integrated.data.table.annotations.Identifier;

public class IdentifiedUser {

    public int numeralId;

    @Identifier(columnIdentifier = "name")
    public String displayedName;

    public IdentifiedUser(int id, String name) {
        this.numeralId = id;
        this.displayedName = name;
    }

}
