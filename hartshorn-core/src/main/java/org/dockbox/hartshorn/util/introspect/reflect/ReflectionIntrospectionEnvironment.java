package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.introspect.IntrospectionEnvironment;

public class ReflectionIntrospectionEnvironment implements IntrospectionEnvironment {

    private Tristate parameterNamesAvailable = Tristate.UNDEFINED;

    @Override
    public boolean parameterNamesAvailable() {
        if (this.parameterNamesAvailable == Tristate.UNDEFINED) {
            try {
                IntrospectionEnvironment.class.getDeclaredMethod("parameterNamesAvailable");
                this.parameterNamesAvailable = Tristate.TRUE;
            }
            catch (final NoSuchMethodException e) {
                this.parameterNamesAvailable = Tristate.FALSE;
            }
        }
        return this.parameterNamesAvailable.booleanValue();
    }
}
