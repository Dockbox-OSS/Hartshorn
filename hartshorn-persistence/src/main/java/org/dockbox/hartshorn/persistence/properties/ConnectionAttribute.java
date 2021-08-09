package org.dockbox.hartshorn.persistence.properties;

import org.dockbox.hartshorn.di.properties.Attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ConnectionAttribute implements Attribute<PersistenceConnection> {
    @Getter private final PersistenceConnection value;

    public static ConnectionAttribute of(PersistenceConnection connection) {
        return new ConnectionAttribute(connection);
    }

    public static ConnectionAttribute of(Remote remote, Object target, String user, String password) {
        return of(remote.connection(target, user, password));
    }

    public static ConnectionAttribute of(Object target, String user, String password) {
        return of(Remote.DERBY, target, user, password);
    }

    public static ConnectionAttribute of(Object target) {
        return of(target, "", "");
    }
}
