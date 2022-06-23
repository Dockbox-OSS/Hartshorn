package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.util.Result;

public interface ResultCollector {
    void addResult(Object value);

    void addResult(String id, Object value);

    <T> Result<T> result();

    <T> Result<T> result(String id);
}
