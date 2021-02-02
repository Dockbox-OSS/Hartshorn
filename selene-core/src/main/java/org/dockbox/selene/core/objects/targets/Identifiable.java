package org.dockbox.selene.core.objects.targets;

import java.util.UUID;

public interface Identifiable {

    UUID getUniqueId();

    String getName();

    void setName(String name);

}
