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

    public static ConnectionProperty of(Remote remote, Object target, String user, String password) {
        return of(remote.connection(target, user, password));
    }

    public static ConnectionProperty of(Object target, String user, String password) {
        return of(Remote.DERBY, target, user, password);
    }

    public static ConnectionProperty of(Object target) {
        return of(target, "", "");
    }
}
