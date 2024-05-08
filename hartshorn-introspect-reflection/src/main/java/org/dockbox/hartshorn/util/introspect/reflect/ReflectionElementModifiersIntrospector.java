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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.introspect.AccessModifier;
import org.dockbox.hartshorn.util.introspect.ElementModifiersIntrospector;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * TODO: #1059 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ReflectionElementModifiersIntrospector implements ElementModifiersIntrospector {

    private static final int SYNTHETIC;
    private static final int MANDATED;

    static {
        int syntheticModifier;
        int mandatedModifier;
        try {
            Class<Modifier> modifier = Modifier.class;
            Field synthetic = modifier.getDeclaredField("SYNTHETIC");
            synthetic.setAccessible(true);
            syntheticModifier = (int) synthetic.get(null);

            Field mandated = modifier.getDeclaredField("MANDATED");
            mandated.setAccessible(true);
            mandatedModifier = (int) mandated.get(null);
        }
        catch (NoSuchFieldException | IllegalAccessException | InaccessibleObjectException e) {
            syntheticModifier = 0x00001000;
            mandatedModifier  = 0x00008000;
        }
        SYNTHETIC = syntheticModifier;
        MANDATED = mandatedModifier;
    }

    private final int modifiers;
    private final Member member;

    public ReflectionElementModifiersIntrospector(Member member) {
        this.modifiers = member.getModifiers();
        this.member = member;
    }

    public ReflectionElementModifiersIntrospector(int modifiers) {
        this.modifiers = modifiers;
        this.member = null;
    }

    @Override
    public int asInt() {
        return this.modifiers;
    }

    @Override
    public boolean has(AccessModifier modifier) {
        return modifier.test(this.asInt());
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(this.asInt());
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(this.asInt());
    }

    @Override
    public boolean isProtected() {
        return Modifier.isProtected(this.asInt());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.asInt());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.asInt());
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.asInt());
    }

    @Override
    public boolean isTransient() {
        return Modifier.isTransient(this.asInt());
    }

    @Override
    public boolean isVolatile() {
        return Modifier.isVolatile(this.asInt());
    }

    @Override
    public boolean isSynchronized() {
        return Modifier.isSynchronized(this.asInt());
    }

    @Override
    public boolean isNative() {
        return Modifier.isNative(this.asInt());
    }

    @Override
    public boolean isStrict() {
        return Modifier.isStrict(this.asInt());
    }

    @Override
    public boolean isMandated() {
        return (this.asInt() & MANDATED) != 0;
    }

    @Override
    public boolean isSynthetic() {
        return (this.asInt() & SYNTHETIC) != 0;
    }

    @Override
    public boolean isDefault() {
        return this.member instanceof Method method && method.isDefault();
    }
}
