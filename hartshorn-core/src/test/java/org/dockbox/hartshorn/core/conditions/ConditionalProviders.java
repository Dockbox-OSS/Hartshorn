package org.dockbox.hartshorn.core.conditions;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.condition.RequiresProperty;
import org.dockbox.hartshorn.component.processing.Provider;

@Service
public class ConditionalProviders {

    /**
     * Passes as long as {@code java.lang.String} is on the classpath. As this is
     * part of the standard library, it should always be available.
     */
    @Provider("a")
    @RequiresClass(name = "java.lang.String")
    public String a() {
        return "a";
    }

    /**
     * Fails when {@code java.gnal.String} is not on the classpath. As this is
     * an intentional typo, it should never be available.
     */
    @Provider("b")
    @RequiresClass(name = "java.gnal.String")
    public String b() {
        return "b";
    }

    /**
     * Passes as long as {@code property.c} is present as a property, no matter
     * what its value is.
     */
    @Provider("c")
    @RequiresProperty(name = "property.c")
    public String c() {
        return "c";
    }

    /**
     * Passes as long as {@code property.d} is present as a property, and its
     * value is equal to {@code d}. This is handled by {@link ConditionTests},
     * so the property is <b>present</b>.
     * @return
     */
    @Provider("d")
    @RequiresProperty(name = "property.d", withValue = "d")
    public String d() {
        return "d";
    }

    /**
     * Passes as long as {@code property.e} is present as a property, and its
     * value is equal to {@code e}. This is handled by {@link ConditionTests},
     * so the property is <b>absent</b>.
     * @return
     */
    @Provider("e")
    @RequiresProperty(name = "property.e", withValue = "e")
    public String e() {
        return "e";
    }

    /**
     * Passes if there is no property named {@code property.l}. This is handled
     * by {@link ConditionTests}, so the property is <b>absent</b>.
     */
    @Provider("f")
    @RequiresProperty(name = "property.f", matchIfMissing = true)
    public String f() {
        return "f";
    }
}
