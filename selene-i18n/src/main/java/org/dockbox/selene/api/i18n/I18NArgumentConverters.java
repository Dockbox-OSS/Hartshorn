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

package org.dockbox.selene.api.i18n;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.DefaultResource;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.commands.annotations.ArgumentProvider;
import org.dockbox.selene.commands.context.ArgumentConverter;
import org.dockbox.selene.commands.convert.CommandValueConverter;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;

@SuppressWarnings({ "unused", "ClassWithTooManyFields" })
@ArgumentProvider(module = Selene.class)
public final class I18NArgumentConverters implements InjectableType {

    public static final ArgumentConverter<ResourceEntry> RESOURCE = new CommandValueConverter<>(ResourceEntry.class, in -> {
        ResourceService rs = Selene.provide(ResourceService.class);
        in = rs.createValidKey(in);

        Exceptional<? extends ResourceEntry> or = rs.getExternalResource(in);
        if (or.present()) return or.map(ResourceEntry.class::cast);

        String finalValue = in;
        return Exceptional.of(() -> DefaultResource.valueOf(finalValue));
    }, "resource", "i18n", "translation");

    public static final ArgumentConverter<Text> TEXT = new CommandValueConverter<>(Text.class, in -> Exceptional.of(Text.of(in)), "text");

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Selene.log().info("Registered i18n specific command argument converters.");
    }
}
