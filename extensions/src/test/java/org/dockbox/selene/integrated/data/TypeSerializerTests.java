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

package org.dockbox.selene.integrated.data;

import com.google.common.reflect.TypeToken;

import org.dockbox.selene.core.impl.files.serialize.SeleneTypeSerializers;
import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.integrated.data.registry.Registry;
import org.dockbox.selene.integrated.data.registry.RegistryColumn;
import org.dockbox.selene.integrated.data.registry.TestIdentifier;
import org.dockbox.selene.integrated.data.serializers.RegistrySerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

public class TypeSerializerTests {

    private TestConfigurationLoader getTestLoader() {
        TestConfigurationLoader.Builder tlb = TestConfigurationLoader.builder();
        TypeSerializerCollection tsc = tlb.getDefaultOptions().getSerializers().newChild();
        SeleneTypeSerializers.registerTypeSerializers(tsc);
        tsc.registerPredicate(typeToken ->
                SeleneUtils.isAssignableFrom(new TypeToken<Registry<?>>() {
                }.getRawType(), typeToken.getRawType()), new RegistrySerializer());
        tlb.setDefaultOptions(tlb.getDefaultOptions().setSerializers(tsc));
        return tlb.build();
    }

    private Registry<Registry<String>> buildTestRegistry() {
        return new Registry<Registry<String>>()
                .addColumn(TestIdentifier.BRICK, new Registry<String>()
                        .addColumn(TestIdentifier.FULLBLOCK, "Brick Fullblock1", "Brick Fullblock2")
                        .addColumn(TestIdentifier.STAIR, "Brick Stair1")
                        .addColumn(TestIdentifier.SLAB, "Brick Slab1"))
                .addColumn(TestIdentifier.SANDSTONE, new Registry<String>()
                        .addColumn(TestIdentifier.FULLBLOCK, "Sandstone Fullblock1")
                        .addColumn(TestIdentifier.STAIR, "Sandstone Stair1"))
                .addColumn(TestIdentifier.COBBLESTONE, new Registry<String>()
                        .addColumn(TestIdentifier.FULLBLOCK, "Cobblestone Fullblock1"));
    }

    @Test
    public void testThatRegistryCanBeSerialised() throws ObjectMappingException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<Registry<Registry<String>>>() {}, this.buildTestRegistry());

        Registry<Registry<String>> reg = cn.getValue(new TypeToken<Registry<Registry<String>>>() {});

        RegistryColumn<Registry<String>> result = reg.getAllData();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void testThatRegistryCanBeDeserialised() throws ObjectMappingException {
        TestConfigurationLoader tl = this.getTestLoader();
        ConfigurationNode cn = tl.createEmptyNode().setValue(new TypeToken<Registry<Registry<String>>>() {}, this.buildTestRegistry());

        Registry<Registry<String>> reg = cn.getValue(new TypeToken<Registry<Registry<String>>>() {});

        List<String> result = reg.getMatchingColumns(TestIdentifier.BRICK)
                .mapToSingleList(r -> r.getMatchingColumns(TestIdentifier.FULLBLOCK));

        Assertions.assertTrue(result.contains("Brick Fullblock1"));
        Assertions.assertTrue(result.contains("Brick Fullblock2"));
    }

}
