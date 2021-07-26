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

package org.dockbox.hartshorn.commands.events;

import org.dockbox.hartshorn.api.events.AbstractTargetCancellableEvent;
import org.dockbox.hartshorn.api.events.annotations.filter.Filter;
import org.dockbox.hartshorn.api.events.parents.Filterable;
import org.dockbox.hartshorn.api.events.processing.FilterType;
import org.dockbox.hartshorn.api.events.processing.FilterTypes;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * The event fired when a command is executed natively through the implemented platform. This
 * typically includes both external commands and commands defined within Hartshorn.
 */
@Getter
@Setter
public class NativeCommandEvent extends AbstractTargetCancellableEvent implements Filterable {

    private String alias;
    private String[] arguments;

    public NativeCommandEvent(CommandSource source, String alias, String[] arguments) {
        super(source);
        this.alias = alias;
        this.arguments = arguments;
    }

    @Override
    public boolean permits(Filter filter) {
        if (Arrays.asList("alias", "command").contains(filter.param())) {
            return filter.type().test(filter.value(), this.alias());
        }
        else if (Arrays.asList("args", "arguments").contains(filter.param())) {
            String[] expectedARguments = filter.value().split(" ");
            if (FilterTypes.EQUALS == filter.type()) {
                for (String expectedArg : expectedARguments) {
                    if (!Arrays.asList(this.arguments()).contains(expectedArg)) return false;
                }
                return true;
            }
            else if (FilterTypes.CONTAINS == filter.type()) {
                for (String expectedArg : expectedARguments) {
                    if (Arrays.asList(this.arguments()).contains(expectedArg)) return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public List<FilterType> acceptedFilters() {
        return FilterTypes.commonStringTypes();
    }

    @Override
    public List<String> acceptedParams() {
        return HartshornUtils.asUnmodifiableList("alias", "args", "arguments", "command");
    }
}
