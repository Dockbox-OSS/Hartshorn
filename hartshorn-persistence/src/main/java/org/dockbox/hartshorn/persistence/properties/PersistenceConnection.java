package org.dockbox.hartshorn.persistence.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PersistenceConnection {

    private final String url;
    private final String username;
    private final String password;
    private final Remote remote;

}
