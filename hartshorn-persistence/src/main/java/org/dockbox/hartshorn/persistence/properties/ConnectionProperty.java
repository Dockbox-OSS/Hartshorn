package org.dockbox.hartshorn.persistence.properties;

import org.dockbox.hartshorn.di.properties.InjectorProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ConnectionProperty implements InjectorProperty<PersistenceConnection> {
    @Getter private final PersistenceConnection value;

    public static ConnectionProperty of(PersistenceConnection connection) {
        return new ConnectionProperty(connection);
    }

    public static ConnectionProperty of(Remote remote, String url, String user, String password) {
        return of(remote.connection(url, user, password));
    }

    public static ConnectionProperty of(String url, String user, String password) {
        return of(Remote.DERBY, url, user, password);
    }

    public static ConnectionProperty of(String url) {
        return of(url, "", "");
    }
}
