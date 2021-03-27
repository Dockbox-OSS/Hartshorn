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

package org.dockbox.selene.sponge.util.command.values;

import org.dockbox.selene.common.command.values.AbstractArgumentElement;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;

public class SpongeArgumentElement extends AbstractArgumentElement<CommandElement> {

    public SpongeArgumentElement(CommandElement reference) {
        super(reference);
    }

    public SpongeArgumentElement(SpongeArgumentElement... elements) {
        super(elements);
    }

    @Override
    protected void ofElements(AbstractArgumentElement<CommandElement>[] elements) {
        CommandElement element;
        if (0 == elements.length) element = GenericArguments.none();
        else if (1 == elements.length) element = elements[0].getReference();
        else {
            CommandElement[] commandElements = new CommandElement[elements.length];
            for (int i = 0; i < elements.length; i++) {
                AbstractArgumentElement<CommandElement> commandElementAbstractArgumentElement = elements[i];
                commandElements[i] = commandElementAbstractArgumentElement.getReference();
            }
            element = GenericArguments.seq(commandElements);
        }
        this.setReference(element);
    }

    @Override
    public AbstractArgumentElement<CommandElement> asOptional() {
        return new SpongeArgumentElement(GenericArguments.optional(this.getReference()));
    }
}
