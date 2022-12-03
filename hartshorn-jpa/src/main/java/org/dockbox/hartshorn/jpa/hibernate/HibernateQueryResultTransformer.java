package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.jpa.query.QueryResultTransformer;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;

import jakarta.persistence.Query;

public class HibernateQueryResultTransformer implements QueryResultTransformer {

    @Override
    public Query transform(final Query query, final TypeView<?> resultType) {
        // SqmQuery implementations are correctly typed, so those don't need to be transformed. Native queries
        // are not pre-configured with a result transformer, so we need to add one.
        if (query instanceof NativeQuery<?> hibernateQuery) {
            final TypeView<?> targetType = QueryResultTransformer.createSafeTargetType(resultType);
            return hibernateQuery.setTupleTransformer(Transformers.aliasToBean(targetType.type()));
        }
        return query;
    }
}
