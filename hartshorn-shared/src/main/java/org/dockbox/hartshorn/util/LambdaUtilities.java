package org.dockbox.hartshorn.util;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class LambdaUtilities {

    public static BooleanSupplier alwaysTrue() {
        return () -> true;
    }

    public static BooleanSupplier alwaysFalse() {
        return () -> false;
    }

    public static Supplier<Boolean> alwaysTrueSupplier() {
        return alwaysTrue()::getAsBoolean;
    }

    public static Supplier<Boolean> alwaysFalseSupplier() {
        return alwaysFalse()::getAsBoolean;
    }
}
