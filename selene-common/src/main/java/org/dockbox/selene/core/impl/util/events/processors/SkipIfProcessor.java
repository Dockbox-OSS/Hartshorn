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

package org.dockbox.selene.core.impl.util.events.processors;

import org.dockbox.selene.core.annotations.SkipIf;
import org.dockbox.selene.core.exceptions.SkipEventException;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.core.util.events.AbstractEventParamProcessor;
import org.dockbox.selene.core.util.events.IWrapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Parameter;

public class SkipIfProcessor extends AbstractEventParamProcessor<SkipIf> {

    @NotNull
    @Override
    public Class<SkipIf> getAnnotationClass() {
        return SkipIf.class;
    }

    @Override
    public Object process(Object object, SkipIf annotation, Event event, Parameter parameter, IWrapper wrapper) throws SkipEventException {
        switch (annotation.value()) {
            case NULL:
                if (null == object) throw new SkipEventException();
                break;
            case EMPTY:
                if (SeleneUtils.isEmpty(object)) throw new SkipEventException();
                break;
            case ZERO:
                if (object instanceof Number && 0 == ((Number) object).floatValue())
                    throw new SkipEventException();
                break;
        }
        return object;
    }

}
