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

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.impl.files.serialize.SeleneTypeSerializers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;

import io.leangen.geantyref.TypeToken;

public class SeleneTypeSerializersTests {

    private TestConfigurationLoader getTestLoader() {
        TestConfigurationLoader.Builder tlb = TestConfigurationLoader.builder();
        tlb.defaultOptions(build -> build.serializers(SeleneTypeSerializers.collection()));
        return tlb.build();
    }

    @Test
    public void testThatByteArraysCanBeSerialised() throws SerializationException {
        byte[] array = { 4, -2 };

        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createNode().set(new TypeToken<byte[]>() {}, array);

        List<Byte> ls = cn.getList(TypeToken.get(Byte.class));
        Assertions.assertTrue(ls.contains((byte)4));
        Assertions.assertTrue(ls.contains((byte)-2));
    }

    @Test
    public void testThatByteArraysCanBeDeserialised() throws SerializationException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createNode().set(new TypeToken<List<Integer>>() {}, Lists.newArrayList(4, -2));

        byte[] ls = cn.get(new TypeToken<byte[]>() {});
        Assertions.assertEquals(2, ls.length);

        Assertions.assertEquals(ls[0], ((byte) 4));
        Assertions.assertEquals(ls[1], ((byte) -2));
    }

    @Test
    public void testThatShortArraysCanBeSerialised() throws SerializationException {
        short[] array = { 4, -2 };

        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createNode().set(new TypeToken<short[]>() {}, array);

        List<Short> ls = cn.getList(TypeToken.get(Short.class));
        Assertions.assertTrue(ls.contains((short)4));
        Assertions.assertTrue(ls.contains((short)-2));
    }

    @Test
    public void testThatShortArraysCanBeDeserialised() throws SerializationException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createNode().set(new TypeToken<List<Integer>>() {}, Lists.newArrayList(4, -2));

        short[] ls = cn.get(new TypeToken<short[]>() {});
        Assertions.assertEquals(2, ls.length);

        Assertions.assertEquals(ls[0], ((short) 4));
        Assertions.assertEquals(ls[1], ((short) -2));
    }

    @Test
    public void testThatIntArraysCanBeSerialised() throws SerializationException {
        int[] array = { 4, -2 };

        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createNode().set(new TypeToken<int[]>() {}, array);

        List<Integer> ls = cn.getList(TypeToken.get(Integer.class));
        Assertions.assertTrue(ls.contains(4));
        Assertions.assertTrue(ls.contains(-2));
    }

    @Test
    public void testThatIntArraysCanBeDeserialised() throws SerializationException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createNode().set(new TypeToken<List<Integer>>() {}, Lists.newArrayList(4, -2));

        int[] ls = cn.get(new TypeToken<int[]>() {});
        Assertions.assertEquals(2, ls.length);

        Assertions.assertEquals(4, ls[0]);
        Assertions.assertEquals(ls[1], -2);
    }

    @Test
    public void testThatLanguagesCanBeSerialised() throws SerializationException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createNode().set(new TypeToken<Language>() {}, Language.NL_NL);

        Language ls = cn.get(TypeToken.get(Language.class));
        Assertions.assertEquals(ls.getCode(), Language.NL_NL.getCode());
        Assertions.assertEquals(ls.getNameLocalized(), Language.NL_NL.getNameLocalized());
    }

    @Test
    public void testThatLanguagesCanBeDeserialised() throws SerializationException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createNode().set(new TypeToken<Language>() {}, Language.NL_NL);

        Language ls = cn.get(TypeToken.get(Language.class));
        Assertions.assertEquals(ls.getCode(), Language.NL_NL.getCode());
        Assertions.assertEquals(ls.getNameLocalized(), Language.NL_NL.getNameLocalized());
    }
}
