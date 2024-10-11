package org.dockbox.hartshorn.reporting.support;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;
import org.dockbox.hartshorn.reporting.Reportable;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

public class ValueAdapterDiagnosticsPropertyWriterConsumer implements DiagnosticsPropertyWriterConsumer {

    private final Object object;

    public ValueAdapterDiagnosticsPropertyWriterConsumer(Object object) {
        this.object = object;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void writeTo(DiagnosticsPropertyWriter writer) {
        switch (object) {
            case String string -> writer.writeString(string);
            case Integer integer -> writer.writeInt(integer);
            case Long longNumber -> writer.writeLong(longNumber);
            case Float floatNumber -> writer.writeFloat(floatNumber);
            case Double doubleNumber -> writer.writeDouble(doubleNumber);
            case Boolean booleanValue -> writer.writeBoolean(booleanValue);
            case Enum enumValue -> writer.writeEnum(enumValue);
            case Reportable reportable -> writer.writeDelegate(reportable);
            case Class<?> clazz -> writer.writeString(clazz.getCanonicalName());
            case Annotation annotation -> writer.writeDelegate(new AnnotationReporter<>(annotation));
            case String[] strings -> writer.writeStrings(strings);
            case int[] integers -> writer.writeInts(integers);
            case long[] longs -> writer.writeLongs(longs);
            case float[] floats -> writer.writeFloats(floats);
            case double[] doubles -> writer.writeDoubles(doubles);
            case boolean[] booleans -> writer.writeBooleans(booleans);
            case Enum[] enums -> writer.writeEnums(enums);
            case Reportable[] reportables -> writer.writeDelegates(reportables);
            case Class[] clazz -> {
                String[] array = Stream.of(clazz)
                        .map(Class::getCanonicalName)
                        .toArray(String[]::new);
                writer.writeStrings(array);
            }
            case Annotation[] annotations -> {
                AnnotationReporter[] array = Stream.of(annotations)
                        .map(AnnotationReporter::new)
                        .toArray(AnnotationReporter[]::new);
                writer.writeDelegates(array);
            }
            default -> throw new IllegalStateException("Unexpected value: " + object);
        }
    }
}
