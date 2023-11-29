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

package org.dockbox.hartshorn.hsl.modules;

/**
 * A standard wrapper library around {@link Math}.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class MathLibrary {

    /**
     * @see Math#log(double)
     */
    public double log(Double num) {
        return Math.log(num);
    }

    /**
     * @see Math#log10(double)
     */
    public double log10(Double num) {
        return Math.log10(num);
    }

    /**
     * @see Math#max(double, double)
     */
    public double max(Double x, Double y) {
        return Math.max(x, y);
    }

    /**
     * @see Math#min(double, double)
     */
    public double min(Double x, Double y) {
        return Math.min(x, y);
    }

    /**
     * @see Math#pow(double, double)
     */
    public double pow(Double x, Double y) {
        return Math.pow(x, y);
    }

    /**
     * @see Math#sqrt(double)
     */
    public double sqrt(Double num) {
        return Math.sqrt(num);
    }

    /**
     * @see Math#sin(double)
     */
    public double sin(Double num) {
        return Math.sin(num);
    }

    /**
     * @see Math#cos(double)
     */
    public double cos(Double num) {
        return Math.cos(num);
    }

    /**
     * @see Math#tan(double)
     */
    public double tan(Double num) {
        return Math.tan(num);
    }

    /**
     * @see Math#asin(double)
     */
    public double asin(Double num) {
        return Math.asin(num);
    }

    /**
     * @see Math#acos(double)
     */
    public double acos(Double num) {
        return Math.acos(num);
    }

    /**
     * @see Math#atan(double)
     */
    public double atan(Double num) {
        return Math.atan(num);
    }

    /**
     * @see Math#sinh(double)
     */
    public double sinh(Double num) {
        return Math.sinh(num);
    }

    /**
     * @see Math#cosh(double)
     */
    public double cosh(Double num) {
        return Math.cosh(num);
    }

    /**
     * @see Math#tanh(double)
     */
    public double tanh(Double num) {
        return Math.tanh(num);
    }
}
