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

public class MathLibrary {

    public double log(final Double num) {
        return Math.log(num);
    }

    public double log10(final Double num) {
        return Math.log10(num);
    }

    public double max(final Double x, final Double y) {
        return Math.max(x, y);
    }

    public double min(final Double x, final Double y) {
        return Math.min(x, y);
    }

    public double pow(final Double x, final Double y) {
        return Math.pow(x, y);
    }

    public double sqrt(final Double num) {
        return Math.sqrt(num);
    }

    public double sin(final Double num) {
        return Math.sin(num);
    }

    public double cos(final Double num) {
        return Math.cos(num);
    }

    public double tan(final Double num) {
        return Math.tan(num);
    }

    public double asin(final Double num) {
        return Math.asin(num);
    }

    public double acos(final Double num) {
        return Math.acos(num);
    }

    public double atan(final Double num) {
        return Math.atan(num);
    }

    public double sinh(final Double num) {
        return Math.sinh(num);
    }

    public double cosh(final Double num) {
        return Math.cosh(num);
    }

    public double tanh(final Double num) {
        return Math.tanh(num);
    }
}
