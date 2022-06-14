package org.dockbox.hartshorn.core.types;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

public class LongCycles {

    @Singleton
    public static class LongCycleA {
        @Inject
        public LongCycleA(final LongCycleB b) {}
    }

    @Singleton
    public static class LongCycleB {
        @Inject
        public LongCycleB(final LongCycleC c) {}
    }

    @Singleton
    public static class LongCycleC {
        @Inject
        public LongCycleC(final LongCycleD d) {}
    }

    @Singleton
    public static class LongCycleD {
        @Inject
        public LongCycleD(final LongCycleA a) {}
    }
}
