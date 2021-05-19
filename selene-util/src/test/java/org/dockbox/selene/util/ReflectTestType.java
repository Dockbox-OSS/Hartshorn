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

package org.dockbox.selene.util;

import org.dockbox.selene.api.entity.annotations.Entity;
import org.dockbox.selene.api.entity.annotations.Property;
import org.dockbox.selene.util.annotations.Demo;
import org.junit.jupiter.api.Assertions;

import java.util.Locale;

@Demo
@Entity(value = "test", rejects = RejectingType.class)
public class ReflectTestType extends ParentTestType {

    @Demo
    private String privateField = "privateField";

    @Property("propertyField")
    public String publicField = "publicField";

    private final String finalPrivateField = "finalPrivateField";
    public final String finalPublicField = "finalPublicField";

    @Demo
    public static String publicStaticField = "publicStaticField";
    private static String privateStaticField = "privateStaticField";

    @Property(getter = "getField", setter = "setField")
    private String accessorField;

    public String getField() {
        return "accessorField";
    }

    public void setField(String value) {
        this.activatedSetter = true;
    }

    public String publicMethod(String argument) {
        Assertions.assertEquals("value", argument);
        return argument.toUpperCase(Locale.ROOT);
    }

    public String privateMethod(String argument) {
        Assertions.assertEquals("value", argument);
        return argument.toUpperCase(Locale.ROOT);
    }

    @Demo
    public void publicAnnotatedMethod() {}

    @Demo
    private void privateAnnotatedMethod() {}

    @Demo
    public ReflectTestType() {
        this.activatedConstructor = true;
    }

    /* TEST UTILITIES, DO NOT TEST AGAINST */
    private boolean activatedSetter = false;
    private boolean activatedMethod = false;
    private boolean activatedConstructor = false;

    public boolean isActivatedSetter() {
        return this.activatedSetter;
    }

    public boolean isActivatedMethod() {
        return this.activatedMethod;
    }

    public boolean isActivatedConstructor() {
        return this.activatedConstructor;
    }
}
