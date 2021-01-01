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

package org.dockbox.selene.core.objects.bossbar;

import org.dockbox.selene.core.text.Text;

/**
 * The interface to be used when constructing a {@link Bossbar} instance. Typically this does not have a explicit
 * implementation, and is instead used to virtually create instances using {@link com.google.inject.assistedinject.AssistedInject}.
 *
 * <p>
 * See <a href="https://github.com/google/guice/wiki/AssistedInject">Google/guice 'AssistedInject'</a> for further
 * details
 */
public interface BossbarFactory {
    Bossbar create(String id, float percent, Text text, BossbarColor color, BossbarStyle style);
}
