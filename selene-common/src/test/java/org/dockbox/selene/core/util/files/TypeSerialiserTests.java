/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

/*
 * This file is part of Neutrino, licensed under the MIT License (MIT).
 */
package org.dockbox.selene.core.util.files;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import org.dockbox.selene.core.impl.util.files.serialize.ByteArrayTypeSerialiser;
import org.dockbox.selene.core.impl.util.files.serialize.IntArrayTypeSerialiser;
import org.dockbox.selene.core.impl.util.files.serialize.SetTypeSerialiser;
import org.dockbox.selene.core.impl.util.files.serialize.ShortArrayTypeSerialiser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

public class TypeSerialiserTests {

    private TestConfigurationLoader getSetTestLoader() {
        TestConfigurationLoader.Builder tclb = TestConfigurationLoader.builder();
        TypeSerializerCollection tsc = tclb.getDefaultOptions().getSerializers();
        tsc.registerPredicate(
                typeToken -> Set.class.isAssignableFrom(typeToken.getRawType()),
                new SetTypeSerialiser()
        );

        tclb.setDefaultOptions(tclb.getDefaultOptions().setSerializers(tsc));
        return tclb.build();
    }

    private TestConfigurationLoader getArrayTestLoader() {
        TestConfigurationLoader.Builder tclb = TestConfigurationLoader.builder();
        TypeSerializerCollection tsc = tclb.getDefaultOptions().getSerializers();
        tsc.registerType(new TypeToken<byte[]>() {}, new ByteArrayTypeSerialiser());
        tsc.registerType(new TypeToken<short[]>() {}, new ShortArrayTypeSerialiser());
        tsc.registerType(new TypeToken<int[]>() {}, new IntArrayTypeSerialiser());

        tclb.setDefaultOptions(tclb.getDefaultOptions().setSerializers(tsc)
            .setAcceptedTypes(Sets.newHashSet(Byte.class, Integer.class, Short.class)));
        return tclb.build();
    }

    @Test
    public void testThatByteArraysCanBeSerialised() throws ObjectMappingException {
        byte[] array = { 4, -2 };

        TestConfigurationLoader tcl = this.getArrayTestLoader();
        ConfigurationNode cn = tcl.createEmptyNode().setValue(new TypeToken<byte[]>() {}, array);

        List<Byte> ls = cn.getList(TypeToken.of(Byte.class));
        Assert.assertTrue(ls.contains((byte)4));
        Assert.assertTrue(ls.contains((byte)-2));
    }

    @Test
    public void testThatByteArraysCanBeDeserialised() throws ObjectMappingException {
        TestConfigurationLoader tcl = this.getArrayTestLoader();
        ConfigurationNode cn = tcl.createEmptyNode().setValue(new TypeToken<List<Integer>>() {}, Lists.newArrayList(4, -2));

        byte[] ls = cn.getValue(new TypeToken<byte[]>() {});
        Assert.assertEquals(2, ls.length);

        Assert.assertEquals(ls[0], ((byte) 4));
        Assert.assertEquals(ls[1], ((byte) -2));
    }

    @Test
    public void testThatShortArraysCanBeSerialised() throws ObjectMappingException {
        short[] array = { 4, -2 };

        TestConfigurationLoader tcl = this.getArrayTestLoader();
        ConfigurationNode cn = tcl.createEmptyNode().setValue(new TypeToken<short[]>() {}, array);

        List<Short> ls = cn.getList(TypeToken.of(Short.class));
        Assert.assertTrue(ls.contains((short)4));
        Assert.assertTrue(ls.contains((short)-2));
    }

    @Test
    public void testThatShortArraysCanBeDeserialised() throws ObjectMappingException {
        TestConfigurationLoader tcl = this.getArrayTestLoader();
        ConfigurationNode cn = tcl.createEmptyNode().setValue(new TypeToken<List<Integer>>() {}, Lists.newArrayList(4, -2));

        short[] ls = cn.getValue(new TypeToken<short[]>() {});
        Assert.assertEquals(2, ls.length);

        Assert.assertEquals(ls[0], ((short) 4));
        Assert.assertEquals(ls[1], ((short) -2));
    }

    @Test
    public void testThatIntArraysCanBeSerialised() throws ObjectMappingException {
        int[] array = { 4, -2 };

        TestConfigurationLoader tcl = this.getArrayTestLoader();
        ConfigurationNode cn = tcl.createEmptyNode().setValue(new TypeToken<int[]>() {}, array);

        List<Integer> ls = cn.getList(TypeToken.of(Integer.class));
        Assert.assertTrue(ls.contains(4));
        Assert.assertTrue(ls.contains(-2));
    }

    @Test
    public void testThatIntArraysCanBeDeserialised() throws ObjectMappingException {
        TestConfigurationLoader tcl = this.getArrayTestLoader();
        ConfigurationNode cn = tcl.createEmptyNode().setValue(new TypeToken<List<Integer>>() {}, Lists.newArrayList(4, -2));

        int[] ls = cn.getValue(new TypeToken<int[]>() {});
        Assert.assertEquals(2, ls.length);

        Assert.assertEquals(4, ls[0]);
        Assert.assertEquals(ls[1], -2);
    }

    @Test
    public void testThatSetsCanBeSerialised() throws ObjectMappingException {
        TestConfigurationLoader tcl = this.getSetTestLoader();
        ConfigurationNode cn = tcl.createEmptyNode().setValue(new TypeToken<Set<String>>() {}, Sets.newHashSet("test", "test2"));

        List<String> ls = cn.getList(TypeToken.of(String.class));
        Assert.assertTrue(ls.contains("test"));
        Assert.assertTrue(ls.contains("test2"));
    }

    @Test
    public void testThatSetsCanBeDeserialised() throws ObjectMappingException {
        TestConfigurationLoader tcl = this.getSetTestLoader();
        ConfigurationNode cn = tcl.createEmptyNode().setValue(new TypeToken<List<String>>() {}, Lists.newArrayList("test", "test", "test2"));

        Set<String> ls = cn.getValue(new TypeToken<Set<String>>() {});
        Assert.assertEquals(2, ls.size());
        Assert.assertTrue(ls.contains("test"));
        Assert.assertTrue(ls.contains("test2"));
    }
}
