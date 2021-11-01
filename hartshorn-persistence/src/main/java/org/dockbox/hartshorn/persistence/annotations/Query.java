package org.dockbox.hartshorn.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {
    String value();
    QueryType type() default QueryType.JPQL;
    boolean automaticClear() default false;
    boolean automaticFlush() default false;

    public enum QueryType {
        JPQL,
        NATIVE,
    }
}
