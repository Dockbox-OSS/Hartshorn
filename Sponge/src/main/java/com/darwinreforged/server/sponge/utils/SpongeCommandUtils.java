package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.init.UtilityImplementation;
import com.darwinreforged.server.core.util.CommandUtils;
import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.core.util.commands.annotation.Permission;
import com.darwinreforged.server.core.util.commands.command.CommandExecutor;
import com.darwinreforged.server.core.util.commands.command.CommandFactory;
import com.darwinreforged.server.core.util.commands.element.ElementFactory;
import com.darwinreforged.server.core.util.commands.utils.MarkdownWriter;
import com.darwinreforged.server.modules.internal.darwin.DarwinServerModule;
import com.darwinreforged.server.sponge.commands.SpongeCommand;
import com.darwinreforged.server.sponge.commands.SpongeElementFactory;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
@UtilityImplementation(CommandUtils.class)
public class SpongeCommandUtils extends CommandUtils<SpongeCommand> {

    private final PluginContainer plugin;

    public SpongeCommandUtils() {
        this(SpongeCommandUtils.builder().owner(DarwinServer.getServer()));
    }

    private SpongeCommandUtils(CommandUtils.Builder<SpongeCommand> builder) {
        super(builder);
        Object owner = getOwner();
        if (owner instanceof PluginContainer) {
            plugin = (PluginContainer) owner;
        } else {
            plugin = Sponge.getPluginManager().fromInstance(owner)
                    .orElseThrow(() -> new IllegalArgumentException("Provided object is not a plugin instance"));
        }
    }

    @Override
    public String getOwnerId() {
        return plugin.getId();
    }

    @Override
    public void info(String message, Object... args) {
        System.out.println(String.format(message, args));
    }

    @Override
    public void warn(String message, Object... args) {
        System.err.println(String.format(message, args));
    }

    @Override
    protected void submit(Object owner, SpongeCommand command) {
        Sponge.getCommandManager().register(owner, command, command.getAliases());
        registerPermissions(command);
        generateDocs(command);
    }

    private void registerPermissions(SpongeCommand command) {
        PermissionService service = Sponge.getServiceManager().provideUnchecked(PermissionService.class);

        for (CommandExecutor e : command.getExecutors()) {
            Permission permission = e.getPermission();
            PermissionDescription.Builder builder = service.newDescriptionBuilder(getOwner());
            builder.description(Text.of("Allows use of /", e.getUsage().value()));
            builder.id(permission.value().p());
            if (!permission.role().value().isEmpty()) {
                builder.assign(permission.role().value(), permission.role().permit());
            }

            builder.register();
        }
    }

    private void generateDocs(SpongeCommand command) {
        Optional<DarwinServerModule> module = DarwinServer.getModule(DarwinServerModule.class);
        module.ifPresent(darwinServerModule -> {
            Path commandBus = DarwinServer.getUtilChecked(FileUtils.class).getDataDirectory(darwinServerModule, "commands");
            Path file = commandBus.resolve(
                    String.format("%s-%s.md",
                            plugin.getId(),
                            command.getAlias()
                    )
            );

            try {
                Files.createDirectories(commandBus);
                try (Writer writer = Files.newBufferedWriter(file)) {
                    try (MarkdownWriter mdwriter = new MarkdownWriter(writer)) {
                        mdwriter.writeHeaders();
                        for (CommandExecutor e : command.getExecutors()) {
                            mdwriter.writeCommand(e);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (!module.isPresent()) {
            DarwinServer.getServer().getLogger().error("Could not obtain config module");
        }
    }

    public static ElementFactory.Builder elements() {
        return SpongeElementFactory.builder();
    }

    public static Builder builder() {
        return new Builder()
                .elements(elements().build())
                .commands(SpongeCommand::new);
    }

    public static class Builder extends CommandUtils.Builder<SpongeCommand> {

        @Override
        public Builder commands(CommandFactory<SpongeCommand> command) {
            super.commands(command);
            return this;
        }

        @Override
        public Builder elements(ElementFactory factory) {
            super.elements(factory);
            return this;
        }

        @Override
        public Builder owner(Object plugin) {
            super.owner(plugin);
            return this;
        }
    }
}
