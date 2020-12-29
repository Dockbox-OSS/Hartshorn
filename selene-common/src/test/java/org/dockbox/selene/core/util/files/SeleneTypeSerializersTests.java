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

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.impl.files.serialize.SeleneTypeSerializers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

public class SeleneTypeSerializersTests {

    private TestConfigurationLoader getTestLoader() {
        TestConfigurationLoader.Builder tlb = TestConfigurationLoader.builder();
        TypeSerializerCollection tsc = tlb.getDefaultOptions().getSerializers().newChild();
        SeleneTypeSerializers.registerTypeSerializers(tsc);
        tlb.setDefaultOptions(tlb.getDefaultOptions().setSerializers(tsc));
        return tlb.build();
    }

    @Test
    public void testThatByteArraysCanBeSerialised() throws ObjectMappingException {
        byte[] array = { 4, -2 };

        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<byte[]>() {}, array);

        List<Byte> ls = cn.getList(TypeToken.of(Byte.class));
        Assertions.assertTrue(ls.contains((byte)4));
        Assertions.assertTrue(ls.contains((byte)-2));
    }

    @Test
    public void testThatByteArraysCanBeDeserialised() throws ObjectMappingException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<List<Integer>>() {}, Lists.newArrayList(4, -2));

        byte[] ls = cn.getValue(new TypeToken<byte[]>() {});
        Assertions.assertEquals(2, ls.length);

        Assertions.assertEquals(ls[0], ((byte) 4));
        Assertions.assertEquals(ls[1], ((byte) -2));
    }

    @Test
    public void testThatShortArraysCanBeSerialised() throws ObjectMappingException {
        short[] array = { 4, -2 };

        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<short[]>() {}, array);

        List<Short> ls = cn.getList(TypeToken.of(Short.class));
        Assertions.assertTrue(ls.contains((short)4));
        Assertions.assertTrue(ls.contains((short)-2));
    }

    @Test
    public void testThatShortArraysCanBeDeserialised() throws ObjectMappingException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<List<Integer>>() {}, Lists.newArrayList(4, -2));

        short[] ls = cn.getValue(new TypeToken<short[]>() {});
        Assertions.assertEquals(2, ls.length);

        Assertions.assertEquals(ls[0], ((short) 4));
        Assertions.assertEquals(ls[1], ((short) -2));
    }

    @Test
    public void testThatIntArraysCanBeSerialised() throws ObjectMappingException {
        int[] array = { 4, -2 };

        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<int[]>() {}, array);

        List<Integer> ls = cn.getList(TypeToken.of(Integer.class));
        Assertions.assertTrue(ls.contains(4));
        Assertions.assertTrue(ls.contains(-2));
    }

    @Test
    public void testThatIntArraysCanBeDeserialised() throws ObjectMappingException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<List<Integer>>() {}, Lists.newArrayList(4, -2));

        int[] ls = cn.getValue(new TypeToken<int[]>() {});
        Assertions.assertEquals(2, ls.length);

        Assertions.assertEquals(4, ls[0]);
        Assertions.assertEquals(ls[1], -2);
    }

    @Test
    public void testThatSetsCanBeSerialised() throws ObjectMappingException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<Set<String>>() {}, Sets.newHashSet("test", "test2"));

        List<String> ls = cn.getList(TypeToken.of(String.class));
        Assertions.assertTrue(ls.contains("test"));
        Assertions.assertTrue(ls.contains("test2"));
    }

    @Test
    public void testThatSetsCanBeDeserialised() throws ObjectMappingException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<List<String>>() {}, Lists.newArrayList("test", "test", "test2"));

        Set<String> ls = cn.getValue(new TypeToken<Set<String>>() {});
        Assertions.assertEquals(2, ls.size());
        Assertions.assertTrue(ls.contains("test"));
        Assertions.assertTrue(ls.contains("test2"));
    }

    @Test
    public void testThatLanguagesCanBeSerialised() throws ObjectMappingException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<Language>() {}, Language.NL_NL);

        Language ls = cn.getValue(TypeToken.of(Language.class));
        Assertions.assertEquals(ls.getCode(), Language.NL_NL.getCode());
        Assertions.assertEquals(ls.getNameLocalized(), Language.NL_NL.getNameLocalized());
    }

    @Test
    public void testThatLanguagesCanBeDeserialised() throws ObjectMappingException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<Language>() {}, Language.NL_NL);

        Language ls = cn.getValue(TypeToken.of(Language.class));
        Assertions.assertEquals(ls.getCode(), Language.NL_NL.getCode());
        Assertions.assertEquals(ls.getNameLocalized(), Language.NL_NL.getNameLocalized());
    }
}
