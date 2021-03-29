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

package org.dockbox.selene.api.util.images;

import org.dockbox.selene.api.objects.tuple.Tristate;

public enum DitherMode {
    NoDither,
    FloydSteinberg(
            Tristate.FALSE,
            MatrixType.ERROR_DIFFUSION,
            new double[][]{
                    { 0, 0, 7 },
                    { 3, 5, 1 }
            }),
    JarvisJudiceNinke(
            Tristate.FALSE,
            MatrixType.ERROR_DIFFUSION,
            new double[][]{
                    { 0, 0, 0, 7, 5 },
                    { 3, 5, 7, 5, 3 },
                    { 1, 3, 5, 3, 1 }
            }),
    Stucki(
            Tristate.FALSE,
            MatrixType.ERROR_DIFFUSION,
            new double[][]{
                    { 0, 0, 0, 8, 4 },
                    { 2, 4, 8, 4, 2 },
                    { 1, 2, 4, 2, 1 }
            }),
    Atkinson(
            Tristate.FALSE,
            MatrixType.ERROR_DIFFUSION,
            new double[][]{
                    { 0, 0, 0, 1, 1 },
                    { 0, 1, 1, 1, 0 },
                    { 0, 0, 1, 0, 0 }
            }),
    Burkes(
            Tristate.FALSE,
            MatrixType.ERROR_DIFFUSION,
            new double[][]{
                    { 0, 0, 0, 8, 4 },
                    { 2, 4, 8, 4, 2 }
            }),
    Sierra3(
            Tristate.FALSE,
            MatrixType.ERROR_DIFFUSION,
            new double[][]{
                    { 0, 0, 0, 5, 3 },
                    { 2, 4, 5, 4, 2 },
                    { 0, 2, 3, 2, 0 }
            }),
    TwoRowSierra(
            Tristate.FALSE,
            MatrixType.ERROR_DIFFUSION,
            new double[][]{
                    { 0, 0, 0, 4, 3 },
                    { 1, 2, 3, 2, 1 }
            }),
    SierraLite(
            Tristate.FALSE,
            MatrixType.ERROR_DIFFUSION,
            new double[][]{
                    { 0, 0, 2 },
                    { 1, 1, 0 }
            }),
    OrderedBayer2x2(
            Tristate.TRUE,
            MatrixType.BAYER,
            new double[][]{
                    { 0, 2 },
                    { 3, 1 }
            }),
    OrderedBayer4x4(
            Tristate.TRUE,
            MatrixType.BAYER,
            new double[][]{
                    { 0, 8, 2, 10 },
                    { 12, 4, 14, 6 },
                    { 3, 11, 1, 9 },
                    { 15, 7, 13, 5 }
            }),
    OrderedBayer8x8(
            Tristate.TRUE,
            MatrixType.BAYER,
            new double[][]{
                    { 0, 48, 12, 60, 3, 51, 15, 63 },
                    { 32, 16, 44, 28, 35, 19, 47, 31 },
                    { 8, 56, 4, 52, 11, 59, 7, 55 },
                    { 40, 24, 36, 20, 43, 27, 39, 23 },
                    { 2, 50, 14, 62, 1, 49, 13, 61 },
                    { 34, 18, 46, 30, 33, 17, 45, 29 },
                    { 10, 58, 6, 54, 9, 57, 5, 53 },
                    { 42, 26, 38, 22, 41, 25, 37, 21 }
            }),
    OrderedBayer16x16(
            Tristate.TRUE,
            MatrixType.BAYER,
            new double[][]{
                    { 0, 192, 48, 240, 12, 204, 60, 252, 3, 195, 51, 243, 15, 207, 63, 255 },
                    { 128, 64, 176, 112, 140, 76, 188, 124, 131, 67, 179, 115, 143, 79, 191, 127 },
                    { 32, 224, 16, 208, 44, 236, 28, 220, 35, 227, 19, 211, 47, 239, 31, 223 },
                    { 160, 96, 144, 80, 172, 108, 156, 92, 163, 99, 147, 83, 175, 111, 159, 95 },
                    { 8, 200, 56, 248, 4, 196, 52, 244, 11, 203, 59, 251, 7, 199, 55, 247 },
                    { 136, 72, 184, 120, 132, 68, 180, 116, 139, 75, 187, 123, 135, 71, 183, 119 },
                    { 40, 232, 24, 216, 36, 228, 20, 212, 43, 235, 27, 219, 39, 231, 23, 215 },
                    { 168, 104, 152, 88, 164, 100, 148, 84, 171, 107, 155, 91, 167, 103, 151, 87 },
                    { 2, 194, 50, 242, 14, 206, 62, 254, 1, 193, 49, 241, 13, 205, 61, 253 },
                    { 130, 66, 178, 114, 142, 78, 190, 126, 129, 65, 177, 113, 141, 77, 189, 125 },
                    { 34, 226, 18, 210, 46, 238, 30, 222, 33, 225, 17, 209, 45, 237, 29, 221 },
                    { 162, 98, 146, 82, 174, 110, 158, 94, 161, 97, 145, 81, 173, 109, 157, 93 },
                    { 10, 202, 58, 250, 6, 198, 54, 246, 9, 201, 57, 249, 5, 197, 53, 245 },
                    { 138, 74, 186, 122, 134, 70, 182, 118, 137, 73, 185, 121, 133, 69, 181, 117 },
                    { 42, 234, 26, 218, 38, 230, 22, 214, 41, 233, 25, 217, 37, 229, 21, 213 },
                    { 170, 106, 154, 90, 166, 102, 150, 86, 169, 105, 153, 89, 165, 101, 149, 85 }
            });

    private Tristate ordered = Tristate.UNDEFINED;
    private MatrixType matrixType;
    private double[][] matrix;

    DitherMode() {}

    DitherMode(Tristate ordered, MatrixType matrixType, double[][] matrix) {
        this.ordered = ordered;
        this.matrixType = matrixType;
        this.matrix = matrix;
    }

    boolean isEnabled() {
        return NoDither != this;
    }

    boolean isOrdered() {
        if (Tristate.UNDEFINED == this.ordered) {
            throw new UnsupportedOperationException("Unknown/corrupted dither mode!");
        }
        return this.ordered.booleanValue();
    }

    double[][] getErrorDiffusionMatrix() {
        if (MatrixType.ERROR_DIFFUSION != this.matrixType) {
            throw new UnsupportedOperationException("Only caught diffusion matrices supported, invalid dither mode");
        }

        double[][] result = this.matrix;

        int sum = 0;
        for (double[] resulti : result) for (double resultij : resulti) sum += resultij;
        for (int i = 0; i < result.length; i++)
            for (int j = 0; j < result[i].length; j++) result[i][j] /= sum;
        return result;
    }

    double[][] getBayerMatrix() {
        if (MatrixType.BAYER != this.matrixType) {
            throw new UnsupportedOperationException("Only ordered dither matrices supported, invalid dither mode");
        }

        double[][] result = this.matrix;

        double divider = result.length * result[0].length;
        for (int i = 0; i < result.length; i++)
            for (int j = 0; j < result[i].length; j++) result[i][j] /= divider;
        return result;
    }

    private enum MatrixType {
        BAYER,
        ERROR_DIFFUSION
    }
}
