package org.dockbox.hartshorn.beans;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ContextBeanProvider implements BeanProvider {

    private final BeanContext beanContext;

    public ContextBeanProvider(final BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    private Predicate<BeanReference<?>> typeFilter(final Class<?> type) {
        return ref -> ref.type().childOf(type);
    }

    private Predicate<BeanReference<?>> idFilter(final String id) {
        return ref -> ref.id().equals(id);
    }

    private Predicate<BeanReference<?>> typeAndIdFilter(final Class<?> type, final String id) {
        return this.typeFilter(type).and(this.idFilter(id));
    }

    @Override
    public <T> T first(final Class<T> type) {
        return this.first(type, this.typeFilter(type));
    }

    @Override
    public <T> T first(final Class<T> type, final String id) {
        return this.first(type, this.typeAndIdFilter(type, id));
    }

    private <T> T first(final Class<T> type, final Predicate<BeanReference<?>> predicate) {
        return this.stream(type, predicate)
                .findFirst()
                .orElse(null);
    }

    @Override
    public <T> List<T> all(final Class<T> type) {
        return this.stream(type, this.typeFilter(type))
                .toList();
    }

    @Override
    public <T> List<T> all(final Class<T> type, final String id) {
        return this.stream(type, this.typeAndIdFilter(type, id))
                .toList();
    }

    private <T>Stream<T> stream(final Class<T> type, final Predicate<BeanReference<?>> predicate) {
        return this.beanContext.beans().stream()
                .filter(predicate)
                .map(BeanReference::bean)
                .map(type::cast);
    }
}
