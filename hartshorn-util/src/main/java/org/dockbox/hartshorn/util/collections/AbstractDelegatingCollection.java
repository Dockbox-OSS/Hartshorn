/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.Iterator;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A basic implementation of {@link Collection} that delegates all operations to a backing {@link Collection} instance.
 * This class is intended to be extended by classes that need to implement {@link Collection}, but do not need to
 * implement all methods.
 *
 * @param <E> the type of elements in this collection
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class AbstractDelegatingCollection<E> implements Collection<E> {

    private final Collection<E> delegate;

    public AbstractDelegatingCollection(Collection<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return this.delegate.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return this.delegate.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return this.delegate.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.delegate.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        return this.delegate.addAll(c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return this.delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return this.delegate.retainAll(c);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }
}
