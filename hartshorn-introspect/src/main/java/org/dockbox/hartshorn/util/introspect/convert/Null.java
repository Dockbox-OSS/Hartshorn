package org.dockbox.hartshorn.util.introspect.convert;

/**
 * A marker class used to represent a null value. This is used to avoid unnecessary conversions
 * when using {@link DefaultValueProvider}s. This class is not intended to be used outside the
 * {@link org.dockbox.hartshorn.util.introspect.convert} package, and is thus reserved for
 * internal use only.
 *
 * @author Guus Lieben
 * @since 23.1
 */
final class Null {

    public static final Class<Null> TYPE = Null.class;
    public static final Null INSTANCE = new Null();

    private Null() {
    }
}
