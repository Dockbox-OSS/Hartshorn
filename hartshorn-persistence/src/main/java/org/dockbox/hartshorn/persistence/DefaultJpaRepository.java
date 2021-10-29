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

public interface DefaultJpaRepository<EM extends EntityManager & AutoCloseable, T, ID> extends JpaRepository<T, ID> {

    <R> R transform(final Function<EM, R> function);

    void accept(final Consumer<EM> consumer);

    @Override
    default Set<T> findAll() {
        return this.transform(em -> {
            final CriteriaBuilder builder = em.getCriteriaBuilder();
            final CriteriaQuery<T> criteria = builder.createQuery(this.reify());
            criteria.from(this.reify());
            final List<T> data = em.createQuery(criteria).getResultList();
            return HartshornUtils.asUnmodifiableSet(data);
        });
    }

    @Override
    default Exceptional<T> findById(final ID id) {
        return this.transform(em -> Exceptional.of(() -> em.find(this.reify(), id)));
    }

    Class<T> reify();

}
