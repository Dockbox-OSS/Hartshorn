package org.dockbox.hartshorn.util;

public final class HartshornObjectDescriptionStyle implements ObjectDescriptionStyle {

    public static final ObjectDescriptionStyle INSTANCE = new HartshornObjectDescriptionStyle();

    private HartshornObjectDescriptionStyle() {
        // Use constant instance
    }

    @Override
    public void describeStart(StringBuilder builder, Object object) {
        builder.append(object.getClass().getSimpleName()).append(" {");
    }

    @Override
    public void describeEnd(StringBuilder builder, Object object) {
        builder.append("}");
    }

    @Override
    public void describeField(StringBuilder builder, Object object, String fieldName, Object fieldValue) {
        builder.append(fieldName).append(": ").append(fieldValue);
    }

    @Override
    public void describeFieldSeparator(StringBuilder builder, Object object) {
        builder.append(", ");
    }
}
