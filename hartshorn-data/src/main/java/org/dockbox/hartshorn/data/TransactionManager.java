package org.dockbox.hartshorn.data;

public interface TransactionManager {
    public void beginTransaction();

    public void commitTransaction();

    public void rollbackTransaction();
}
