package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;

public class QueryExecutionContext extends DefaultContext {

    private Map<MethodView<?, ?>, LockModeType> lockModes = new ConcurrentHashMap<>();
    private Map<MethodView<?, ?>, FlushModeType> flushModes = new ConcurrentHashMap<>();

    public LockModeType lockMode(final MethodView<?, ?> method) {
        return this.lockModes.get(method);
    }

    public FlushModeType flushMode(final MethodView<?, ?> method) {
        return this.flushModes.get(method);
    }

    public void lockMode(final MethodView<?, ?> method, final LockModeType lockMode) {
        this.lockModes.put(method, lockMode);
    }

    public void flushMode(final MethodView<?, ?> method, final FlushModeType flushMode) {
        this.flushModes.put(method, flushMode);
    }

    public boolean hasLockMode(final MethodView<?, ?> method) {
        return this.lockModes.containsKey(method);
    }

    public boolean hasFlushMode(final MethodView<?, ?> method) {
        return this.flushModes.containsKey(method);
    }
}
