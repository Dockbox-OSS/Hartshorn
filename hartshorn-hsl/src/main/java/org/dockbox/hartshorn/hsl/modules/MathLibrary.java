/*
 * Copyright 2019-2024 the original author or authors.
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
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class MathLibrary {

    /**
     * @param num The number to calculate the natural logarithm of.
     * @return The natural logarithm of the given number.
     *
     * @see Math#log(double)
     */
    public double log(Double num) {
        return Math.log(num);
    }

    /**
     * @param num The number to calculate the base 10 logarithm of.
     * @return The base 10 logarithm of the given number.
     *
     * @see Math#log10(double)
     */
    public double log10(Double num) {
        return Math.log10(num);
    }

    /**
     * @param x The first number.
     * @param y The second number.
     * @return The maximum of the two numbers.
     *
     * @see Math#max(double, double)
     */
    public double max(Double x, Double y) {
        return Math.max(x, y);
    }

    /**
     * @param x The first number.
     * @param y The second number.
     * @return The minimum of the two numbers.
     *
     * @see Math#min(double, double)
     */
    public double min(Double x, Double y) {
        return Math.min(x, y);
    }

    /**
     * @param x The base.
     * @param y The exponent.
     * @return The value of {@code x} raised to the power of {@code y}.
     *
     * @see Math#pow(double, double)
     */
    public double pow(Double x, Double y) {
        return Math.pow(x, y);
    }

    /**
     * @param num The number to calculate the square root of.
     * @return The square root of the given number.
     *
     * @see Math#sqrt(double)
     */
    public double sqrt(Double num) {
        return Math.sqrt(num);
    }

    /**
     * @param num The number to calculate the sine of.
     * @return The sine of the given number.
     *
     * @see Math#sin(double)
     */
    public double sin(Double num) {
        return Math.sin(num);
    }

    /**
     * @param num The number to calculate the cosine of.
     * @return The cosine of the given number.
     *
     * @see Math#cos(double)
     */
    public double cos(Double num) {
        return Math.cos(num);
    }

    /**
     * @param num The number to calculate the tangent of.
     * @return The tangent of the given number.
     *
     * @see Math#tan(double)
     */
    public double tan(Double num) {
        return Math.tan(num);
    }

    /**
     * @param num The number to calculate the arc sine of.
     * @return The arc sine of the given number.
     *
     * @see Math#asin(double)
     */
    public double asin(Double num) {
        return Math.asin(num);
    }

    /**
     * @param num The number to calculate the arc cosine of.
     * @return The arc cosine of the given number.
     *
     * @see Math#acos(double)
     */
    public double acos(Double num) {
        return Math.acos(num);
    }

    /**
     * @param num The number to calculate the arc tangent of.
     * @return The arc tangent of the given number.
     *
     * @see Math#atan(double)
     */
    public double atan(Double num) {
        return Math.atan(num);
    }

    /**
     * @param num The number to calculate the hyperbolic sine of.
     * @return The hyperbolic sine of the given number.
     *
     * @see Math#sinh(double)
     */
    public double sinh(Double num) {
        return Math.sinh(num);
    }

    /**
     * @param num The number to calculate the hyperbolic cosine of.
     * @return The hyperbolic cosine of the given number.
     *
     * @see Math#cosh(double)
     */
    public double cosh(Double num) {
        return Math.cosh(num);
    }

    /**
     * @param num The number to calculate the hyperbolic tangent of.
     * @return The hyperbolic tangent of the given number.
     *
     * @see Math#tanh(double)
     */
    public double tanh(Double num) {
        return Math.tanh(num);
    }
}
