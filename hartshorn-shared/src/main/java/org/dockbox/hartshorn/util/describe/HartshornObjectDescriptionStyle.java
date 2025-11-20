/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.util.describe;

/**
 * Standard implementation of {@link ObjectDescriptionStyle} that describes objects in a style
 * similar to standard Java object descriptions.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public final class HartshornObjectDescriptionStyle implements ObjectDescriptionStyle {

    /**
     * The constant instance of this style. The instance is stateless and can be used by multiple
     * threads.
     */
    public static final ObjectDescriptionStyle INSTANCE = new HartshornObjectDescriptionStyle();

    private HartshornObjectDescriptionStyle() {
        // Use constant instance
    }

    @Override
    public void describeObjectStart(StringBuilder builder, Object object, boolean includeTypeName) {
        if (includeTypeName) {
            builder.append(object.getClass().getSimpleName());
        }
        builder.append(" {");
    }

    @Override
    public void describeObjectEnd(StringBuilder builder, Object object) {
        builder.append("}");
    }

    @Override
    public void describeField(
        StringBuilder builder,
        Object object,
        String fieldName,
        Object fieldValue
    ) {
        builder.append(fieldName).append(": ").append(fieldValue);
    }

    @Override
    public void describeFieldSeparator(StringBuilder builder, Object object) {
        builder.append(", ");
    }

    @Override
    public void describeArrayStart(
        StringBuilder builder,
        Object collectionObject,
        int length,
        boolean includeTypeName
    ) {
        if (includeTypeName) {
            builder.append(collectionObject.getClass().getSimpleName());
        }
        builder.append(" [");
    }

    @Override
    public void describeArrayEnd(StringBuilder builder, Object collectionObject) {
        builder.append("]");
    }

    @Override
    public void describeArrayElement(
        StringBuilder builder,
        Object collectionObject,
        int index,
        Object element
    ) {
        builder.append(element);
    }

    @Override
    public void describeArrayElementSeparator(
        StringBuilder builder,
        Object collectionObject,
        int index
    ) {
        builder.append(", ");
    }
}
