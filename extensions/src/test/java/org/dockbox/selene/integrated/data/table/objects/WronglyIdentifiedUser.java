package org.dockbox.selene.integrated.data.table.objects;

import org.dockbox.selene.integrated.data.table.annotations.Identifier;

public class WronglyIdentifiedUser {

    public int Id;

    @Identifier(columnIdentifier = "name")
    public String displayedName;

    public WronglyIdentifiedUser(int id, String name) {
        this.Id = id;
        this.displayedName = name;
    }

}
