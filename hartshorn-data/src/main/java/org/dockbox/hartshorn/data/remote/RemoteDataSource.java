package org.dockbox.hartshorn.data.remote;

public class RemoteDataSource implements Remote {

    private final PersistenceConnection connection;
    private final String driver;

    public RemoteDataSource(final PersistenceConnection connection, final String driver) {
        this.connection = connection;
        this.driver = driver;
    }

    @Override
    public PersistenceConnection connection(final Object target, final String user, final String password) {
        return this.connection;
    }

    @Override
    public String driver() {
        return this.driver;
    }
}
