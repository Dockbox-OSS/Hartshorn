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

package org.dockbox.selene.core.impl.web;

import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.web.WebUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class DefaultWebUtil implements WebUtil
{

    @Override
    public <T> Exceptional<T> getContent(Class<T> type, String url)
    {
        try
        {
            return this.getContent(type, new URL(url));
        }
        catch (MalformedURLException e)
        {
            Selene.handle("Invalid URL", e);
            return Exceptional.of(e);
        }
    }

    @Override
    public String getContent(URL url)
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder builder = new StringBuilder();

            while (null != in.readLine())
                builder.append(builder).append("\n");
            in.close();
            return builder.toString();
        }
        catch (IOException e)
        {
            Selene.handle("Could not read content from '" + url.toExternalForm() + "'", e);
            return "";
        }
    }

    @Override
    public String getContent(String url)
    {
        try
        {
            return this.getContent(new URL(url));
        }
        catch (MalformedURLException e)
        {
            Selene.handle("Invalid URL", e);
            return "";
        }
    }

}
