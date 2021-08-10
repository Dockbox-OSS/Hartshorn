package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.binding.BindingHierarchy;
import org.dockbox.hartshorn.di.binding.Provider;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ContextWrappedHierarchy<C> implements BindingHierarchy<C> {

    @Getter
    private BindingHierarchy<C> real;
    private final Consumer<BindingHierarchy<C>> onUpdate;

    @Override
    public Set<Provider<C>> providers() {
        return this.real().providers();
    }

    @Override
    public BindingHierarchy<C> add(final Provider<C> provider) {
        this.real = this.real().add(provider);
        return this.update();
    }

    @Override
    public BindingHierarchy<C> add(final int priority, final Provider<C> provider) {
        this.real = this.real().add(priority, provider);
        return this.update();
    }

    @Override
    public BindingHierarchy<C> merge(final BindingHierarchy<C> hierarchy) {
        this.real = this.real().merge(hierarchy);
        return this.update();
    }

    @Override
    public int size() {
        return this.real().size();
    }

    @Override
    public Exceptional<Provider<C>> get(final int priority) {
        return this.real().get(priority);
    }

    @Override
    public Key<C> key() {
        return this.real().key();
    }

    @NotNull
    @Override
    public Iterator<Entry<Integer, Provider<C>>> iterator() {
        return this.real().iterator();
    }

    private BindingHierarchy<C> update() {
        this.onUpdate.accept(this.real());
        return this;
    }
}
