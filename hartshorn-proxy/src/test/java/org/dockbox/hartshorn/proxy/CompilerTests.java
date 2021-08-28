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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.proxy.compiler.Compiler;
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

public class CompilerTests extends ApplicationAwareTest {

    static final String SURFACE_JAVA = "package org.dockbox.hartshorn.proxy.compiler;"
            + "public class FlyingClass {"
            + "    boolean _dirty = false;"
            + "    public void dirty(boolean dirty) { this._dirty = dirty; }"
            + "    public boolean dirty() { return this._dirty; }"
            + "}";
    static final String SINGLE_JAVA = "package org.dockbox.hartshorn.proxy.compiler;"
            + "import org.dockbox.hartshorn.proxy.*;"
            + "public class UserProxy extends User implements BeanProxy {"
            + "    boolean _dirty = false;"
            + "    public UserProxy id(String id) {"
            + "        super.id(id);"
            + "        dirty(true);"
            + "        return this;"
            + "    }"
            + "    public UserProxy name(String name) {"
            + "        super.name(name);"
            + "        dirty(true);"
            + "        return this;"
            + "    }"
            + "    public UserProxy created(long created) {"
            + "        super.created(created);"
            + "        dirty(true);"
            + "        return this;"
            + "    }"
            + "    public void dirty(boolean dirty) { this._dirty = dirty; }"
            + "    public boolean dirty() { return this._dirty; }"
            + "}";
    static final String MULTIPLE_JAVA = "package org.dockbox.hartshorn.proxy.compiler;"
            + "public class Multiple {"
            + "    public static class A { }"
            + "    class B { }"
            + "}"
            + ""
            + "class C { }";
    static final String EXISTING_JAVA = """
            package org.dockbox.hartshorn.proxy.compiler;public class User {
                String _extra;
                public String extra() { return this._extra; }
                public void extra(String extra) { this._extra = extra; }
            }""";
    static Compiler compiler;

    @BeforeAll
    static void setup() {
        compiler = new Compiler();
    }

    @Test
    public void surfaceTest() throws NoSuchMethodException {
        Class<?> flying = this.context().get(Compiler.class).compile(SURFACE_JAVA).rethrow().get();
        Object flyingInstance = this.context().get(flying);

        Assertions.assertEquals("FlyingClass", flying.getSimpleName());
        Assertions.assertNotNull(flying.getMethod("dirty"));
        Assertions.assertNotNull(flyingInstance);
        Assertions.assertTrue(flying.isInstance(flyingInstance));
    }

    @Test
    public void testCompileSingleClass() throws Exception {
        Map<String, byte[]> results = compiler.compile("UserProxy.java", SINGLE_JAVA);
        Assertions.assertEquals(1, results.size());
        Assertions.assertTrue(results.containsKey("org.dockbox.hartshorn.proxy.compiler.UserProxy"));
        Class<?> clazz = compiler.loadClass("org.dockbox.hartshorn.proxy.compiler.UserProxy", results);
        // get method:
        Method setId = clazz.getMethod("id", String.class);
        Method setName = clazz.getMethod("name", String.class);
        Method setCreated = clazz.getMethod("created", long.class);
        // try instance:
        Object obj = clazz.getDeclaredConstructor().newInstance();
        // get as proxy:
        BeanProxy proxy = (BeanProxy) obj;
        Assertions.assertFalse(proxy.dirty());
        // set:
        setId.invoke(obj, "A-123");
        setName.invoke(obj, "Fly");
        setCreated.invoke(obj, 123000999);
        // get as user:
        User user = (User) obj;
        Assertions.assertEquals("A-123", user.id());
        Assertions.assertEquals("Fly", user.name());
        Assertions.assertEquals(123000999, user.created());
        Assertions.assertTrue(proxy.dirty());
    }

    @Test
    public void testCompileMultipleClasses() throws Exception {
        Map<String, byte[]> results = compiler.compile("Multiple.java", MULTIPLE_JAVA);
        Assertions.assertEquals(4, results.size());
        Assertions.assertTrue(results.containsKey("org.dockbox.hartshorn.proxy.compiler.Multiple"));
        Assertions.assertTrue(results.containsKey("org.dockbox.hartshorn.proxy.compiler.Multiple$A"));
        Assertions.assertTrue(results.containsKey("org.dockbox.hartshorn.proxy.compiler.Multiple$B"));
        Assertions.assertTrue(results.containsKey("org.dockbox.hartshorn.proxy.compiler.C"));
        Class<?> clzMul = compiler.loadClass("org.dockbox.hartshorn.proxy.compiler.Multiple", results);
        Object obj = clzMul.getDeclaredConstructor().newInstance();
        Assertions.assertNotNull(obj);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void existingClassIsNotChanged() throws Exception {
        Map<String, byte[]> results = compiler.compile("User.java", EXISTING_JAVA);
        Class<?> clzExisting = compiler.loadClass("org.dockbox.hartshorn.proxy.User", results);
        Assertions.assertThrows(NoSuchMethodException.class, () -> clzExisting.getMethod("extra"));
        Assertions.assertNotNull(clzExisting.getMethod("name"));
    }
}
