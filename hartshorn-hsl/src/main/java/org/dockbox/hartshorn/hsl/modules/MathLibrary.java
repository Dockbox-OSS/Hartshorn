/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.hsl.modules;

/**
 * A standard wrapper library around {@link Math}.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class MathLibrary {

    /**
     * @see Math#log(double)
     */
    public double log(final Number num) {
        return Math.log(num.floatValue());
    }

    /**
     * @see Math#log10(double)
     */
    public double log10(final Number num) {
        return Math.log10(num.floatValue());
    }

    /**
     * @see Math#max(double, double)
     */
    public double max(final Number x, final Number y) {
        return Math.max(x.floatValue(), y.floatValue());
    }

    /**
     * @see Math#min(double, double)
     */
    public double min(final Number x, final Number y) {
        return Math.min(x.floatValue(), y.floatValue());
    }

    /**
     * @see Math#pow(double, double)
     */
    public double pow(final Number x, final Number y) {
        return Math.pow(x.floatValue(), y.floatValue());
    }

    /**
     * @see Math#sqrt(double)
     */
    public double sqrt(final Number num) {
        return Math.sqrt(num.floatValue());
    }

    /**
     * @see Math#sin(double)
     */
    public double sin(final Number num) {
        return Math.sin(num.floatValue());
    }

    /**
     * @see Math#cos(double)
     */
    public double cos(final Number num) {
        return Math.cos(num.floatValue());
    }

    /**
     * @see Math#tan(double)
     */
    public double tan(final Number num) {
        return Math.tan(num.floatValue());
    }

    /**
     * @see Math#asin(double)
     */
    public double asin(final Number num) {
        return Math.asin(num.floatValue());
    }

    /**
     * @see Math#acos(double)
     */
    public double acos(final Number num) {
        return Math.acos(num.floatValue());
    }

    /**
     * @see Math#atan(double)
     */
    public double atan(final Number num) {
        return Math.atan(num.floatValue());
    }

    /**
     * @see Math#sinh(double)
     */
    public double sinh(final Number num) {
        return Math.sinh(num.floatValue());
    }

    /**
     * @see Math#cosh(double)
     */
    public double cosh(final Number num) {
        return Math.cosh(num.floatValue());
    }

    /**
     * @see Math#tanh(double)
     */
    public double tanh(final Number num) {
        return Math.tanh(num.floatValue());
    }
}
