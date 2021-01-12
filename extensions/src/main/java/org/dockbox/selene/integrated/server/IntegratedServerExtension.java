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

package org.dockbox.selene.integrated.server;

import org.dockbox.selene.core.Weather;
import org.dockbox.selene.core.annotations.command.Arg;
import org.dockbox.selene.core.annotations.command.Command;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.command.context.CommandContext;
import org.dockbox.selene.core.command.context.CommandValue.Argument;
import org.dockbox.selene.core.events.EventBus;
import org.dockbox.selene.core.events.server.ServerEvent.ServerReloadEvent;
import org.dockbox.selene.core.extension.ExtensionContext;
import org.dockbox.selene.core.extension.ExtensionManager;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.targets.Identifiable;
import org.dockbox.selene.core.objects.targets.MessageReceiver;
import org.dockbox.selene.core.packets.ChangeGameStatePacket;
import org.dockbox.selene.core.server.IntegratedExtension;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.ServerType;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.actions.ClickAction;
import org.dockbox.selene.core.text.actions.HoverAction;
import org.dockbox.selene.core.text.pagination.PaginationBuilder;
import org.dockbox.selene.core.util.Reflect;
import org.dockbox.selene.core.util.SeleneUtils;

import java.util.List;

@Extension(
        id = "selene",
        name = "Selene",
        description = "Integrated features of Selene",
        authors = "GuusLieben"
)
@Command(aliases = {"selene", "darwin"}, usage = "selene")
public class IntegratedServerExtension implements IntegratedExtension {

    // Parent command
    @Command(aliases = "", usage = "")
    public void debugExtensions(MessageReceiver source) {
        Reflect.runWithInstance(ExtensionManager.class, em -> {
            PaginationBuilder pb = Selene.provide(PaginationBuilder.class);

            List<Text> content = SeleneUtils.emptyList();
            content.add(IntegratedServerResources.SERVER_HEADER
                    .format(Selene.getServer().getVersion())
                    .translate(source).asText()
            );
            content.add(IntegratedServerResources.SERVER_UPDATE
                    .format(Selene.getServer().getLastUpdate())
                    .translate(source).asText()
            );
            content.add(IntegratedServerResources.SERVER_AUTHORS
                    .format(String.join(",", Selene.getServer().getAuthors()))
                    .translate(source).asText());
            content.add(IntegratedServerResources.SERVER_EXTENSIONS.translate(source).asText());

            em.getRegisteredExtensionIds()
                    .forEach(id -> em.getHeader(id)
                            .map(e -> generateText(e, source))
                            .ifPresent(content::add)
                    );

            pb.title(IntegratedServerResources.PAGINATION_TITLE.translate(source).asText());
            pb.content(content);

            source.sendPagination(pb.build());
        });
    }

    private Text generateText(Extension e, MessageReceiver source) {
        Text line = IntegratedServerResources.EXTENSION_ROW
                .format(e.name(), e.id())
                .translate(source)
                .asText();
        line.onClick(ClickAction.runCommand("/dserver extension " + e.id()));
        line.onHover(HoverAction.showText(IntegratedServerResources.EXTENSION_ROW_HOVER
                .format(e.name())
                .translate(source)
                .asText()
        ));
        return line;
    }

    @Command(aliases = "extension", usage = "extension <id{Extension}>")
    public void debugExtension(MessageReceiver src, CommandContext ctx) {
        Reflect.runWithInstance(ExtensionManager.class, em -> {
            Exceptional<Argument<Extension>> oarg = ctx.getArgument("id", Extension.class);
            if (!oarg.isPresent()) {
                src.send(IntegratedServerResources.MISSING_ARGUMENT.format("id"));
                return;
            }

            Extension e = oarg.get().getValue();
            Exceptional<ExtensionContext> oec = em.getContext(e.id());

            if (!oec.isPresent()) {
                src.sendWithPrefix(IntegratedServerResources.EXTENSION_UNKNOWN.format(oarg.get().getValue()));
            } else {
                ExtensionContext ec = oec.get();

                src.send(IntegratedServerResources.EXTENSION_INFO_BLOCK.format(
                        e.name(), e.id(), e.description(),
                        0 == e.dependencies().length ? "None" : String.join("$3, $1", e.dependencies()),
                        String.join("$3, $1", e.authors()),
                        ec.getSource()
                ));
            }
        });
    }

    @Command(aliases = "reload", usage = "reload [id{Extension}]", confirm = true)
    public void reload(MessageReceiver src, CommandContext ctx) {
        EventBus eb = Selene.provide(EventBus.class);
        if (ctx.hasArgument("id")) {
            Exceptional<Argument<Extension>> oarg = ctx.getArgument("id", Extension.class);
            if (!oarg.isPresent()) {
                src.send(IntegratedServerResources.MISSING_ARGUMENT.format("id"));
                return;
            }

            Extension e = oarg.get().getValue();
            Exceptional<?> oi = Selene.provide(ExtensionManager.class).getInstance(e.id());

            oi.ifPresent(o -> {
                eb.post(new ServerReloadEvent(), o.getClass());
                src.send(IntegratedServerResources.EXTENSION_RELOAD_SUCCESSFUL.format(e.name()));
            }).ifAbsent(() ->
                    src.send(IntegratedServerResources.EXTENSION_RELOAD_FAILED.format(e.name())));
        } else {
            eb.post(new ServerReloadEvent());
            src.send(IntegratedServerResources.FULL_RELOAD_SUCCESSFUL);
        }
    }

    @Override
    @Command(aliases = "confirm", usage = "confirm <cooldownId{String}>")
    public void confirm(MessageReceiver src, CommandContext ctx) {
        if (!(src instanceof Identifiable)) {
            src.send(IntegratedServerResources.CONFIRM_WRONG_SOURCE);
            return;
        }
        Exceptional<Argument<String>> optionalCooldownId = ctx.getArgument("cooldownId");

        // UUID is stored by the command executor to ensure runnables are not called by other sources. The uuid
        // argument here is just a confirmation that the source is correct.
        optionalCooldownId
                .ifPresent(cooldownId -> {
                    String cid = cooldownId.getValue();
                    Selene.provide(CommandBus.class).confirmCommand(cid).ifAbsent(() ->
                            src.send(IntegratedServerResources.CONFIRM_FAILED));
                })
                .ifAbsent(() -> src.send(IntegratedServerResources.CONFIRM_INVALID_ID));
    }

    @Command(aliases = "platform", usage = "platform")
    public void platform(MessageReceiver src) {
        ServerType st = Selene.getServer().getServerType();
        String platformVersion = Selene.getServer().getPlatformVersion();

        String mcVersion = Selene.getServer().getMinecraftVersion().getReadableVersionString();

        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");

        String jvmVersion = System.getProperty("java.vm.version");
        String jvmName = System.getProperty("java.vm.name");
        String jvmVendor = System.getProperty("java.vm.vendor");

        String javaRuntimeVersion = System.getProperty("java.runtime.version");
        String classVersion = System.getProperty("java.class.version");

        src.send(IntegratedServerResources.PLATFORM_INFORMATION.format(
                st.getDisplayName(), platformVersion,
                mcVersion,
                javaVersion, javaVendor,
                jvmName, jvmVersion, jvmVendor,
                javaRuntimeVersion, classVersion
        ));
    }

    @Command(aliases = {"lang", "language"}, usage = "language <language{Language}> [player{Player}]", inherit = false)
    public void switchLang(MessageReceiver src, CommandContext ctx,
                           @Arg("language") Language lang,
                           @Arg("player") Player player) {
        if (null == player) {
            if (src instanceof Player) {
                player = (Player) src;
            } else {
                src.send(IntegratedResource.CONFIRM_WRONG_SOURCE);
                return;
            }
        }

        player.setLanguage(lang);

        String languageLocalized = lang.getNameLocalized() + " (" + lang.getNameEnglish() + ")";
        if (player != src)
            src.sendWithPrefix(IntegratedServerResources.LANG_SWITCHED_OTHER.format(player.getName(), languageLocalized));
        player.sendWithPrefix(IntegratedServerResources.LANG_SWITCHED.format(languageLocalized));
    }

    @Command(aliases = "demo", usage = "demo")
    public void demo(Player player, CommandContext context) {
        ChangeGameStatePacket packet = Selene.provide(ChangeGameStatePacket.class);
        packet.setWeather(Weather.RAIN);
        player.send(packet);
    }

}
