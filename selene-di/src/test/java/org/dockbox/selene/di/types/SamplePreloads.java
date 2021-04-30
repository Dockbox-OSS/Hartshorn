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

package org.dockbox.selene.di.types;

import org.dockbox.selene.api.BootstrapPhase;
import org.dockbox.selene.api.Phase;
import org.dockbox.selene.di.preload.Preloadable;

public class SamplePreloads {

    @Phase(BootstrapPhase.PRE_CONSTRUCT)
    public static class PreConstructPreload extends SamplePreloads implements Preloadable {

        private static boolean applied = false;

        @Override
        public void preload() {
            PreConstructPreload.applied = true;
        }

        public static boolean isApplied() {
            return applied;
        }
    }

    @Phase(BootstrapPhase.CONSTRUCT)
    public static class ConstructPreload extends SamplePreloads implements Preloadable {

        private static boolean applied = false;

        @Override
        public void preload() {
            ConstructPreload.applied = true;
        }

        public static boolean isApplied() {
            return applied;
        }
    }

    @Phase(BootstrapPhase.PRE_INIT)
    public static class PreInitPreload extends SamplePreloads implements Preloadable {

        private static boolean applied = false;

        @Override
        public void preload() {
            PreInitPreload.applied = true;
        }

        public static boolean isApplied() {
            return applied;
        }
    }

    @Phase(BootstrapPhase.INIT)
    public static class InitPreload extends SamplePreloads implements Preloadable {

        private static boolean applied = false;

        @Override
        public void preload() {
            InitPreload.applied = true;
        }

        public static boolean isApplied() {
            return applied;
        }
    }

}
