package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public interface DefaultJpaRepository<EM extends EntityManager & AutoCloseable> extends JpaRepository {

    <T> T transform(final Function<EM, T> function);

    void accept(final Consumer<EM> consumer);

    @Override
    default  <T> Set<T> findAll(final Class<T> type) {
        return this.transform(em -> {
            final CriteriaBuilder builder = em.getCriteriaBuilder();
            final CriteriaQuery<T> criteria = builder.createQuery(type);
            criteria.from(type);
            final List<T> data = em.createQuery(criteria).getResultList();
            return HartshornUtils.asUnmodifiableSet(data);
        });
    }

    @Override
    default <T> Exceptional<T> findById(final Class<T> type, final Object id) {
        return this.transform(em -> Exceptional.of(() -> em.find(type, id)));
    }

}
