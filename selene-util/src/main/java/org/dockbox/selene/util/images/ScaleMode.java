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

import com.mortennobel.imagescaling.ResampleFilter;
import com.mortennobel.imagescaling.ResampleFilters;

import java.util.function.Supplier;

public enum ScaleMode {
    NoScale(() -> { throw new UnsupportedOperationException("Unsupported resample filter!"); }),
    BSpline(ResampleFilters::getBSplineFilter),
    Bell(ResampleFilters::getBellFilter),
    BiCubic(ResampleFilters::getBiCubicFilter),
    BiCubicHighFreqResponse(ResampleFilters::getBiCubicHighFreqResponse),
    BoxFilter(ResampleFilters::getBoxFilter),
    Hermite(ResampleFilters::getHermiteFilter),
    Lanczos3(ResampleFilters::getLanczos3Filter),
    Mitchell(ResampleFilters::getMitchellFilter),
    Triangle(ResampleFilters::getTriangleFilter);

    private final Supplier<ResampleFilter> resampleFilter;

    ScaleMode(Supplier<ResampleFilter> resampleFilter) {
        this.resampleFilter = resampleFilter;
    }

    ResampleFilter getResampleFilter() {
        return this.resampleFilter.get();
    }
}
