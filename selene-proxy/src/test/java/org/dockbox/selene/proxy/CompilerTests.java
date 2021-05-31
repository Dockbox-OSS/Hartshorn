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

package org.dockbox.selene.proxy;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.proxy.compiler.Compiler;
import org.dockbox.selene.proxy.compiler.CompilerService;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Method;
import java.util.Map;

@ExtendWith(SeleneJUnit5Runner.class)
public class CompilerTests {

    static final String SURFACE_JAVA = "package org.dockbox.selene.proxy.compiler;"
            + "public class FlyingClass {"
            + "    boolean _dirty = false;"
            + "    public void setDirty(boolean dirty) { this._dirty = dirty; }"
            + "    public boolean isDirty() { return this._dirty; }"
            + "}";

    @Test
    public void surfaceTest() throws NoSuchMethodException {
        Class<?> flying = Selene.context().get(CompilerService.class).compile(SURFACE_JAVA).rethrow().get();
        Object flyingInstance = Selene.context().get(flying);

        Assertions.assertEquals("FlyingClass", flying.getSimpleName());
        Assertions.assertNotNull(flying.getMethod("isDirty"));
        Assertions.assertNotNull(flyingInstance);
        Assertions.assertTrue(flying.isInstance(flyingInstance));
    }

    static Compiler compiler;

    @BeforeAll
    static void setUp() {
        compiler = new Compiler();
    }

    static final String SINGLE_JAVA = "package org.dockbox.selene.proxy.compiler;"
            + "import org.dockbox.selene.proxy.compiler.*;"
            + "public class UserProxy extends User implements BeanProxy {"
            + "    boolean _dirty = false;"
            + "    public void setId(String id) {"
            + "        super.setId(id);"
            + "        setDirty(true);"
            + "    }"
            + "    public void setName(String name) {"
            + "        super.setName(name);"
            + "        setDirty(true);"
            + "    }"
            + "    public void setCreated(long created) {"
            + "        super.setCreated(created);"
            + "        setDirty(true);"
            + "    }"
            + "    public void setDirty(boolean dirty) { this._dirty = dirty; }"
            + "    public boolean isDirty() { return this._dirty; }"
            + "}";

    @Test
    public void testCompileSingleClass() throws Exception {
        Map<String, byte[]> results = compiler.compile("UserProxy.java", SINGLE_JAVA);
        Assertions.assertEquals(1, results.size());
        Assertions.assertTrue(results.containsKey("org.dockbox.selene.proxy.compiler.UserProxy"));
        Class<?> clazz = compiler.loadClass("org.dockbox.selene.proxy.compiler.UserProxy", results);
        // get method:
        Method setId = clazz.getMethod("setId", String.class);
        Method setName = clazz.getMethod("setName", String.class);
        Method setCreated = clazz.getMethod("setCreated", long.class);
        // try instance:
        Object obj = clazz.newInstance();
        // get as proxy:
        BeanProxy proxy = (BeanProxy) obj;
        Assertions.assertFalse(proxy.isDirty());
        // set:
        setId.invoke(obj, "A-123");
        setName.invoke(obj, "Fly");
        setCreated.invoke(obj, 123000999);
        // get as user:
        User user = (User) obj;
        Assertions.assertEquals("A-123", user.getId());
        Assertions.assertEquals("Fly", user.getName());
        Assertions.assertEquals(123000999, user.getCreated());
        Assertions.assertTrue(proxy.isDirty());
    }

    static final String MULTIPLE_JAVA = "package org.dockbox.selene.proxy.compiler;"
            + "public class Multiple {"
            + "    public static class A { }"
            + "    class B { }"
            + "}"
            + ""
            + "class C { }";

    @Test
    public void testCompileMultipleClasses() throws Exception {
        Map<String, byte[]> results = compiler.compile("Multiple.java", MULTIPLE_JAVA);
        Assertions.assertEquals(4, results.size());
        Assertions.assertTrue(results.containsKey("org.dockbox.selene.proxy.compiler.Multiple"));
        Assertions.assertTrue(results.containsKey("org.dockbox.selene.proxy.compiler.Multiple$A"));
        Assertions.assertTrue(results.containsKey("org.dockbox.selene.proxy.compiler.Multiple$B"));
        Assertions.assertTrue(results.containsKey("org.dockbox.selene.proxy.compiler.C"));
        Class<?> clzMul = compiler.loadClass("org.dockbox.selene.proxy.compiler.Multiple", results);
        Object obj = clzMul.newInstance();
        Assertions.assertNotNull(obj);
    }

    static final String EXISTING_JAVA = "package org.dockbox.selene.proxy.compiler;" +
            "public class User {\n" +
            "    String _extra;\n" +
            "    public String getExtra() { return this._extra; }\n" +
            "    public void setExtra(String extra) { this._extra = extra; }\n" +
            "}";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void existingClassIsNotChanged() throws Exception {
        Map<String, byte[]> results = compiler.compile("User.java", EXISTING_JAVA);
        Class<?> clzExisting = compiler.loadClass("org.dockbox.selene.proxy.User", results);
        Assertions.assertThrows(NoSuchMethodException.class, () -> clzExisting.getMethod("getExtra"));
        Assertions.assertNotNull(clzExisting.getMethod("getName"));
    }
}
