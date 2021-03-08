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

package org.dockbox.selene.common.web;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.dockbox.selene.api.objects.Exceptional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GsonWebUtil extends DefaultWebUtil {

    @Override
    public <T> Exceptional<T> getContent(Class<T> type, URL url) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            Gson gson = new Gson();
            T result = gson.fromJson(in, type);
            return Exceptional.ofNullable(result);
        }
        catch (JsonIOException | JsonSyntaxException | IOException e) {
            return Exceptional.of(e);
        }
    }
}
