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

package org.dockbox.hartshorn.i18n.common;

import org.dockbox.hartshorn.i18n.MessageReceiver;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.persistence.PersistentCapable;

public interface ResourceEntry extends Formattable, PersistentCapable<ResourceEntryModel> {

    Text asText();

    String plain();

    ResourceEntry translate(MessageReceiver receiver);

    ResourceEntry translate(Language lang);

    ResourceEntry translate();

    ResourceEntry format(Object... args);

    @Override
    default Class<? extends ResourceEntryModel> type() {
        return ResourceEntryModel.class;
    }

    @Override
    default ResourceEntryModel model() {
        return new ResourceEntryModel(this.key(), this.asString(), this.language());
    }

    String key();

    String asString();

    Language language();
}
