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
 * This file is part of Neutrino, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package org.dockbox.selene.core.util.files;

import org.dockbox.selene.core.impl.util.files.annotations.Default;
import org.dockbox.selene.core.impl.util.files.annotations.RequiresProperty;
import org.dockbox.selene.core.impl.util.files.mapping.NeutrinoObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

public class RequiresPropertyTests {

    @Test
    public void testTestIsNotAltered() throws Exception {
        System.setProperty("neutrino.test2", "true");
        CommentedConfigurationNode ccn = SimpleCommentedConfigurationNode.root();
        ccn.getNode("test").setValue("ok");
        ccn.getNode("def").setValue("ok");
        ccn.getNode("updated").setValue("ok");

        TestConf sut = NeutrinoObjectMapperFactory.getInstance().getMapper(TestConf.class).bindToNew().populate(ccn);
        Assert.assertEquals("not", sut.test);
        Assert.assertEquals("def", sut.def);
        Assert.assertEquals("ok", sut.updated);
    }

    @Test
    public void testNodeIsNotAltered() throws Exception {
        System.setProperty("neutrino.test2", "true");
        CommentedConfigurationNode ccn = SimpleCommentedConfigurationNode.root();
        ccn.getNode("test").setValue("ok");
        ccn.getNode("def").setValue("ok");
        ccn.getNode("updated").setValue("ok");

        TestConf sut = new TestConf();
        NeutrinoObjectMapperFactory.getInstance().getMapper(TestConf.class).bind(sut).serialize(ccn);
        Assert.assertEquals("ok", ccn.getNode("test").getString());
        Assert.assertEquals("ok", ccn.getNode("def").getString());
        Assert.assertEquals("not", ccn.getNode("updated").getString());
    }

    @Test
    public void testRegexFunctionsCorrectlyPopulate() throws Exception {
        System.setProperty("neutrino.test2", "true");
        CommentedConfigurationNode ccn = SimpleCommentedConfigurationNode.root();
        ccn.getNode("regex").setValue("ok");
        ccn.getNode("regex2").setValue("ok");

        TestConf sut = new TestConf();
        NeutrinoObjectMapperFactory.getInstance().getMapper(TestConf.class).bind(sut).populate(ccn);
        Assert.assertEquals("ok", sut.regex2);
        Assert.assertEquals("no", sut.regex);
    }

    @Test
    public void testRegexFunctionsCorrectlySerialise() throws Exception {
        System.setProperty("neutrino.test2", "true");
        CommentedConfigurationNode ccn = SimpleCommentedConfigurationNode.root();
        ccn.getNode("regex").setValue("ok");
        ccn.getNode("regex2").setValue("ok");

        TestConf sut = new TestConf();
        NeutrinoObjectMapperFactory.getInstance().getMapper(TestConf.class).bind(sut).serialize(ccn);
        Assert.assertEquals("no", ccn.getNode("regex2").getString());
        Assert.assertEquals("ok", ccn.getNode("regex").getString());
    }

    @ConfigSerializable
    public static class TestConf {

        @RequiresProperty("neutrino.test")
        @Setting("test")
        private String test = "not";

        @RequiresProperty("neutrino.test")
        @Default("def")
        @Setting("def")
        private String def = "not";

        @RequiresProperty("neutrino.test2")
        @Setting("updated")
        private String updated = "not";

        @RequiresProperty(value = "neutrino.test2", matchedName = "fals[Ee]")
        @Setting("regex")
        private String regex = "no";

        @RequiresProperty(value = "neutrino.test2", matchedName = "t.+")
        @Setting("regex2")
        private String regex2 = "no";
    }
}
