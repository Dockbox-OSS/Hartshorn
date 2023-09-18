package org.dockbox.hartshorn.util.introspect.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

record HierarchyKey(AnnotatedElement element, Class<? extends Annotation> annotationType) {

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        HierarchyKey key = (HierarchyKey) other;
        return Objects.equals(this.element, key.element) && Objects.equals(this.annotationType, key.annotationType);
    }

}
