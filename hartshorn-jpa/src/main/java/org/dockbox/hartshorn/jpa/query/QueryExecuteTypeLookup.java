package org.dockbox.hartshorn.jpa.query;

import jakarta.persistence.Query;

public interface QueryExecuteTypeLookup {
    QueryExecuteType lookup(String jpqlQuery);

    QueryExecuteType lookup(Query query);
}
