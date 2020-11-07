package org.dockbox.selene.core.impl.objects.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class TResult<T> extends ArrayList<T>
{
    public TResult() {
        super();
    }

    public TResult(Collection<? extends T> values) {
        super(values);
    }

    public TResult<T> filter(Predicate<? super T> filter) {
        removeIf(filter);
        return this;
    }

    public <K> TResult<K> mapTo(Function<? super T, K> function) {
        TResult<K> result = new TResult<>();

        for (T value : this) {
            result.add(function.apply(value));
        }
        return result;
    }


    public <K> TResult<K> mapToSingleResult(Function<? super T, ? extends Collection<K>> function) {
        TResult<K> result = new TResult<>();

        for (T value : this) {
            result.addAll(function.apply(value));
        }
        return result;
    }

    public T firstMatch(Predicate<? super T> predicate) {
        for (T value : this) {
            if (predicate.test(value)) return value;
        }
        return null;
    }
}

