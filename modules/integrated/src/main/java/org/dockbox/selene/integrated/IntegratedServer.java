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

package org.dockbox.selene.integrated;

import com.google.inject.Singleton;

import org.dockbox.selene.core.annotations.command.Arg;
import org.dockbox.selene.core.annotations.command.Command;
import org.dockbox.selene.core.annotations.module.Module;
import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.command.context.CommandContext;
import org.dockbox.selene.core.command.context.CommandValue.Argument;
import org.dockbox.selene.core.events.EventBus;
import org.dockbox.selene.core.events.server.ServerEvent.ServerReloadEvent;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.module.ModuleContext;
import org.dockbox.selene.core.module.ModuleManager;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.targets.AbstractIdentifiable;
import org.dockbox.selene.core.objects.targets.MessageReceiver;
import org.dockbox.selene.core.server.IntegratedModule;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.ServerType;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.actions.ClickAction;
import org.dockbox.selene.core.text.actions.HoverAction;
import org.dockbox.selene.core.text.pagination.PaginationBuilder;
import org.dockbox.selene.core.util.Reflect;
import org.dockbox.selene.core.util.SeleneUtils;

import java.util.List;

@Module(
        id = "selene",
        name = "Selene",
        description = "Integrated features of Selene",
        authors = "GuusLieben"
)
@Command(aliases = {"selene", "darwin"}, usage = "selene")
@Singleton
public class IntegratedServer implements IntegratedModule {

    // Parent command
    @Command(aliases = "", usage = "")
    public void debugModules(MessageReceiver source) {
        Reflect.runWithInstance(ModuleManager.class, em -> {
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
            content.add(IntegratedServerResources.SERVER_MODULES.translate(source).asText());

            em.getRegisteredModuleIds()
                    .forEach(id -> em.getHeader(id)
                            .map(e -> this.generateText(e, source))
                            .ifPresent(content::add)
                    );

            pb.title(IntegratedServerResources.PAGINATION_TITLE.translate(source).asText());
            pb.content(content);

            source.sendPagination(pb.build());
        });
    }

    private Text generateText(Module e, MessageReceiver source) {
        Text line = IntegratedServerResources.MODULE_ROW
                .format(e.name(), e.id())
                .translate(source)
                .asText();
        line.onClick(ClickAction.runCommand("/dserver module " + e.id()));
        line.onHover(HoverAction.showText(IntegratedServerResources.MODULE_ROW_HOVER
                .format(e.name())
                .translate(source)
                .asText()
        ));
        return line;
    }

    @Command(aliases = "module", usage = "module <id{Module}>")
    public void debugModule(MessageReceiver src, CommandContext ctx) {
        Reflect.runWithInstance(ModuleManager.class, em -> {
            Exceptional<Argument<Module>> oarg = ctx.argument("id");
            if (!oarg.isPresent()) {
                src.send(IntegratedServerResources.MISSING_ARGUMENT.format("id"));
                return;
            }

            Module e = oarg.get().getValue();
            Exceptional<ModuleContext> oec = em.getContext(e.id());

            if (!oec.isPresent()) {
                src.sendWithPrefix(IntegratedServerResources.UNKNOWN_MODULE.format(oarg.get().getValue()));
            } else {
                ModuleContext ec = oec.get();

                src.send(IntegratedServerResources.MODULE_INFO_BLOCK.format(
                        e.name(), e.id(), e.description(),
                        0 == e.dependencies().length ? "None" : String.join("$3, $1", e.dependencies()),
                        String.join("$3, $1", e.authors()),
                        ec.getSource()
                ));
            }
        });
    }

    @Command(aliases = "reload", usage = "reload [id{Module}]", confirm = true)
    public void reload(MessageReceiver src, CommandContext ctx) {
        EventBus eb = Selene.provide(EventBus.class);
        if (ctx.has("id")) {
            Exceptional<Argument<Module>> oarg = ctx.argument("id");
            if (!oarg.isPresent()) {
                src.send(IntegratedServerResources.MISSING_ARGUMENT.format("id"));
                return;
            }

            Module e = oarg.get().getValue();
            Exceptional<?> oi = Selene.provide(ModuleManager.class).getInstance(e.id());

            oi.ifPresent(o -> {
                eb.post(new ServerReloadEvent(), o.getClass());
                src.send(IntegratedServerResources.MODULE_RELOAD_SUCCESSFUL.format(e.name()));
            }).ifAbsent(() ->
                    src.send(IntegratedServerResources.NODULE_RELOAD_FAILED.format(e.name())));
        } else {
            eb.post(new ServerReloadEvent());
            src.send(IntegratedServerResources.FULL_RELOAD_SUCCESSFUL);
        }
    }

    @Override
    @Command(aliases = "confirm", usage = "confirm <cooldownId{String}>")
    public void confirm(MessageReceiver src, CommandContext ctx) {
        if (!(src instanceof AbstractIdentifiable)) {
            src.send(IntegratedServerResources.CONFIRM_WRONG_SOURCE);
            return;
        }
        Exceptional<Argument<String>> optionalCooldownId = ctx.argument("cooldownId");

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
                           @Arg(value = "player", optional = true) Player player) {
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

}
