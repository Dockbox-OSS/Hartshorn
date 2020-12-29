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

package org.dockbox.selene.core.impl.files;

import org.dockbox.selene.core.files.FileType;
import org.dockbox.selene.core.impl.files.util.XStreamUtils;
import org.dockbox.selene.core.impl.files.util.XStreamUtils.XStreamBuilder;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.util.SeleneUtils;

import java.nio.file.Path;

public abstract class DefaultXStreamManager extends DefaultAbstractFileManager {

    protected DefaultXStreamManager() {
        super(FileType.XML);
    }

    @Override
    public <T> Exceptional<T> getFileContent(Path file, Class<T> type) {
        SeleneUtils.REFLECTION.rejects(type, DefaultXStreamManager.class, true);

        return Exceptional.empty();
    }

    @Override
    public <T> Exceptional<Boolean> writeFileContent(Path file, T content) {
        SeleneUtils.REFLECTION.rejects(content.getClass(), DefaultXStreamManager.class, true);

        XStreamBuilder builder = XStreamUtils.create();
        if (content != null) {
            // TODO:
            //  - Omit fields annotated with @Ignore
            //  - Alias fields annotated with @Property
            //  (Recursive over supers)
        }
        return Exceptional.empty();
    }

}
