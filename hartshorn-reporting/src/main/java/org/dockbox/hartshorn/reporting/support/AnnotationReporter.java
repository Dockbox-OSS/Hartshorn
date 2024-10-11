package org.dockbox.hartshorn.reporting.support;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.TypeUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

public class AnnotationReporter<A extends Annotation> implements Reportable {

    private final A annotation;

    public AnnotationReporter(A annotation) {
        this.annotation = annotation;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Map<String, Object> attributes = TypeUtils.getAttributes(annotation);
        collector.property("annotationType").writeString(annotationType.getCanonicalName());
        for (String attributeKey : attributes.keySet()) {
            Object attributeValue = attributes.get(attributeKey);
            var consumer = new ValueAdapterDiagnosticsPropertyWriterConsumer(attributeValue);
            consumer.writeTo(collector.property(attributeKey));
        }
    }
}
