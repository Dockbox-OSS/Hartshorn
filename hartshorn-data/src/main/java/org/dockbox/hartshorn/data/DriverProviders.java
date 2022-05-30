package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.data.hibernate.HibernateRemoteImpl;
import org.dockbox.hartshorn.data.remote.DerbyFileRemote;
import org.dockbox.hartshorn.data.remote.MariaDbRemote;
import org.dockbox.hartshorn.data.remote.MySQLRemote;
import org.dockbox.hartshorn.data.remote.PersistenceConnection;
import org.dockbox.hartshorn.data.remote.PostgreSQLRemote;
import org.dockbox.hartshorn.data.remote.Remote;
import org.dockbox.hartshorn.data.remote.SqlServerRemote;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.hibernate.dialect.Dialect;

import java.sql.Driver;

@Service(activators = UsePersistence.class)
public class DriverProviders {

    @Provider
    @RequiresClass(name = "com.microsoft.sqlserver.jdbc.SQLServerDriver")
    public Remote<?> sqlServer() {
        return new SqlServerRemote();
    }

    @Provider
    @RequiresClass(name = "com.mysql.cj.jdbc.Driver")
    public Remote<?> mySql() {
        return new MySQLRemote();
    }

    @Provider
    @RequiresClass(name = "org.postgresql.Driver")
    public Remote<?> postgres() {
        return new PostgreSQLRemote();
    }

    @Provider
    @RequiresClass(name = "org.mariadb.jdbc.Driver")
    public Remote<?> mariaDb() {
        return new MariaDbRemote();
    }

    @Provider
    @RequiresClass(name = "org.apache.derby.jdbc.EmbeddedDriver")
    public Remote<?> derby() {
        return new DerbyFileRemote();
    }

    @Provider
    public PersistenceConnection defaultConnection(final PropertyHolder propertyHolder) {
        final Result<String> remoteType = propertyHolder.get("hartshorn.data.remote");

        final String driver = (String) propertyHolder.get("hartshorn.data.hibernate.driver_class").orNull();
        if (driver == null) throw new IllegalStateException("Driver class was not configured, expected hartshorn.data.hibernate.driver_class or hartshorn.data.remote to be set, but got null");

        final String dialect = (String) propertyHolder.get("hartshorn.data.hibernate.dialect").orNull();
        if (dialect == null) throw new IllegalStateException("Dialect was not configured, expected hartshorn.data.hibernate.dialect or hartshorn.data.remote to be set, but got null");

        final TypeContext<?> dialectContext = TypeContext.lookup(dialect);
        if (!dialectContext.childOf(Dialect.class)) throw new IllegalStateException("Expected dialect to be a subtype of " + Dialect.class.getCanonicalName());

        final Class<? extends Dialect> dialectType = (Class<? extends Dialect>) dialectContext.type();

        final String username = (String) propertyHolder.get("hartshorn.data.username").orNull();
        final String password = (String) propertyHolder.get("hartshorn.data.password").orNull();

        final String url = (String) propertyHolder.get("hartshorn.data.url").orNull();
        if (url == null) throw new IllegalStateException("Connection string was not configured, expected hartshorn.data.url to be set, but got null");

        final HibernateRemoteImpl remote = new HibernateRemoteImpl(dialectType, driver);
        return new PersistenceConnection(url, username, password, remote);
    }

    @Provider
    public Class<? extends Driver> driver(final Remote<?> remote) throws ClassNotFoundException {
        return (Class<? extends Driver>) Class.forName(remote.driver());
    }

}
