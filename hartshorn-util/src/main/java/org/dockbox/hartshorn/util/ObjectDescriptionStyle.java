package org.dockbox.hartshorn.util;

public interface ObjectDescriptionStyle {

    void describeStart(StringBuilder builder, Object object);

    void describeEnd(StringBuilder builder, Object object);

    void describeField(StringBuilder builder, Object object, String fieldName, Object fieldValue);

    void describeFieldSeparator(StringBuilder builder, Object object);
}
