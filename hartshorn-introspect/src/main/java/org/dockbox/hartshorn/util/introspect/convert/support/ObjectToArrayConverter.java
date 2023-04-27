/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;

import java.lang.reflect.Array;
import java.util.Set;

public class ObjectToArrayConverter implements GenericConverter {

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(ConvertibleTypePair.of(Object.class, Object[].class));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <I, O> Object convert(@Nullable final Object source, @NonNull final Class<I> sourceType, @NonNull final Class<O> targetType) {
        if (sourceType.isPrimitive()) {
            return this.convertPrimitive(source, sourceType, targetType);
        }

        final I[] array = (I[]) Array.newInstance(sourceType, 1);
        array[0] = (I) source;
        return array;
    }

    private <I, O> Object convertPrimitive(final Object source, final Class<I> sourceType, final Class<O> targetType) {
        switch (sourceType.getName()) {
            case "boolean" -> {
                final boolean[] booleanArray = new boolean[1];
                booleanArray[0] = (boolean) source;
                return booleanArray;
            }
            case "byte" -> {
                final byte[] byteArray = new byte[1];
                byteArray[0] = (byte) source;
                return byteArray;
            }
            case "char" -> {
                final char[] charArray = new char[1];
                charArray[0] = (char) source;
                return charArray;
            }
            case "short" -> {
                final short[] shortArray = new short[1];
                shortArray[0] = (short) source;
                return shortArray;
            }
            case "int" -> {
                final int[] intArray = new int[1];
                intArray[0] = (int) source;
                return intArray;
            }
            case "long" -> {
                final long[] longArray = new long[1];
                longArray[0] = (long) source;
                return longArray;
            }
            case "float" -> {
                final float[] floatArray = new float[1];
                floatArray[0] = (float) source;
                return floatArray;
            }
            case "double" -> {
                final double[] doubleArray = new double[1];
                doubleArray[0] = (double) source;
                return doubleArray;
            }
            default -> throw new IllegalArgumentException("Unsupported primitive type: " + sourceType.getName());
        }
    }
}
