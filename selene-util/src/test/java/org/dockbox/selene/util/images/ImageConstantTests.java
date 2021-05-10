/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.util.images;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ImageConstantTests {

    private static Stream<Arguments> getDitherModes() {
        return Stream.of(
                Arguments.of(DitherMode.FloydSteinberg, false, "ed", 3, 2),
                Arguments.of(DitherMode.JarvisJudiceNinke, false, "ed", 5, 3),
                Arguments.of(DitherMode.Stucki, false, "ed", 5, 3),
                Arguments.of(DitherMode.Atkinson, false, "ed", 5, 3),
                Arguments.of(DitherMode.Burkes, false, "ed", 5, 2),
                Arguments.of(DitherMode.Sierra3, false, "ed", 5, 3),
                Arguments.of(DitherMode.TwoRowSierra, false, "ed", 5, 2),
                Arguments.of(DitherMode.SierraLite, false, "ed", 3, 2),
                Arguments.of(DitherMode.OrderedBayer2x2, true, "bayer", 2, 2),
                Arguments.of(DitherMode.OrderedBayer4x4, true, "bayer", 4, 4),
                Arguments.of(DitherMode.OrderedBayer8x8, true, "bayer", 8, 8),
                Arguments.of(DitherMode.OrderedBayer16x16, true, "bayer", 16, 16)
        );
    }

    @ParameterizedTest
    @MethodSource("getDitherModes")
    public void testDitherMode(DitherMode mode, boolean isOrdered, String matrixType, int matrixWidth, int matrixHeight) {
        // Test ordered
        Assertions.assertEquals(isOrdered, mode.isOrdered());

        // Test matrix type
        switch (matrixType) {
            case "bayer":
                Assertions.assertDoesNotThrow(mode::getBayerMatrix);
                Assertions.assertThrows(UnsupportedOperationException.class, mode::getErrorDiffusionMatrix);
                break;
            case "ed":
                Assertions.assertDoesNotThrow(mode::getErrorDiffusionMatrix);
                Assertions.assertThrows(UnsupportedOperationException.class, mode::getBayerMatrix);
                break;
            default:
                Assertions.fail("Unknown matrix type: " + matrixType + " (This is a test definition failure, not a implementation failure)");
        }

        // Test matrix size
        double[][] matrix = matrixType.equals("bayer") ? mode.getBayerMatrix() : mode.getErrorDiffusionMatrix();
        Assertions.assertEquals(matrixHeight, matrix.length);
        for (double[] doubles : matrix) {
            Assertions.assertEquals(matrixWidth, doubles.length);
        }
    }

    @Test
    void testScaleModesHaveResampleFilters() {
        for (ScaleMode scaleMode : ScaleMode.values()) {
            if (scaleMode == ScaleMode.NoScale) {
                Assertions.assertThrows(UnsupportedOperationException.class, () -> scaleMode.getResampleFilter().apply(0));
            }
            else {
                Assertions.assertNotNull(scaleMode.getResampleFilter());
                Assertions.assertDoesNotThrow(() -> scaleMode.getResampleFilter().apply(-1));
            }
        }
    }
}
