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

package org.dockbox.hartshorn.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.JDABuilder;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.di.annotations.inject.Provider;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.discord.annotations.UseDiscordEvents;

import javax.inject.Singleton;
import javax.security.auth.login.LoginException;

@Service(activators = UseDiscordEvents.class)
public class DiscordJDAStarter {

    private static final String TOKEN = "hartshorn.discord.token";

    @Value(TOKEN)
    private String token;

    @Provider
    @Singleton
    public JDA jda(DiscordEventAdapter adapter) throws ApplicationException {
        if (this.token == null) throw new ApplicationException("No value provided for '%s', cannot provide JDA instance.".formatted(TOKEN));
        try {
            JDA jda = JDABuilder.createDefault(this.token).build().awaitStatus(Status.CONNECTED);
            jda.addEventListener(adapter);
            return jda;
        } catch (LoginException | InterruptedException e) {
            throw new ApplicationException(e);
        }
    }

}
