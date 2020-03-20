package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.listeners.PaintingsDiscordListener;
import com.darwinreforged.servermodifications.objects.PaintingSubmission;
import com.darwinreforged.servermodifications.translations.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.todo.PaintingsDatabaseUtil;
import com.google.inject.Inject;
import com.magitechserver.magibridge.MagiBridge;
import net.dv8tion.jda.core.EmbedBuilder;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.awt.*;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Plugin (id = "darwinpaintings", name = "Darwin paintings approval", version = "1.0", description = "Approve a painting before uploading it")
public class PaintingsPlugin {
    @Inject
    @ConfigDir (sharedRoot = false)
    public Path root;

    public static Path staticRoots;
    @Inject
    @DefaultConfig (sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    public static SqlService sql;

    private int id;
    public static HashMap<Integer, PaintingSubmission> submissions = new HashMap<>();

    public PaintingsPlugin() {
    }

    @Listener
    public void onServerFinishLoad ( GameStartingServerEvent event ) throws SQLException {
        Sponge.getCommandManager().register(this, painting, "paintings");
        Sponge.getEventManager().registerListeners(this, new PaintingsDiscordListener());
        staticRoots = root;
        PaintingsDatabaseUtil dbCreate = new PaintingsDatabaseUtil(sql, staticRoots);

        String uri = "jdbc:sqlite:" + staticRoots + "/DarwinPaintings.db";
        ArrayList<String> queries = new ArrayList<>();
        String query = "SELECT * from Submissions where Status = 'Submitted'";
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        {
            ResultSet results = stmt.executeQuery();
            {
                while (results.next()) {
                    id = results.getInt("ID");
                    PaintingSubmission submission = new PaintingSubmission();
                    submission.setID(id);
                    submission.setCommand(results.getString("Command"));
                    submission.setPlayerUUID(UUID.fromString(results.getString("PlayerUUID")));
                    submission.setStatus(results.getString("Status"));
                    System.out.println(submission.getCommand());
                    System.out.println(submission.getID());
                    System.out.println(submission.getPlayerUUID());
                    System.out.println(submission.getStatus());
                    if (submission.getStatus().equals("Submitted")) {
                        submissions.put(id, submission);
                    }
                }
            }
        }
        conn.close();
        System.out.println(submissions);
    }

    public static DataSource getDataSource ( String jdbcUrl ) throws SQLException {
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(jdbcUrl);
    }

    public static Optional<User> getUser ( UUID owner ) {
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        return userStorage.get().get(owner);
    }

    CommandSpec addPainting = CommandSpec.builder()
            .description(Text.of("Upload painting from web"))
            .extendedDescription(Text.of("Enter URL of a picture, map(s) containing painting will be generated"))
            .arguments(
                    GenericArguments.string(Text.of("Name")),
                    GenericArguments.string(Text.of("URL")),
                    GenericArguments.optional(GenericArguments.seq(GenericArguments.integer(Text.of("MapsX")), GenericArguments.integer(Text.of("MapsY")))))

            // GenericArguments.optional(GenericArguments.enumValue(Text.of("ScaleMode"), ScaleMode.class), ScaleMode.Lanczos3)/*,
            //GenericArguments.optional(GenericArguments.enumValue(Text.of("UnsharpenMode"), UnsharpenMask.class), UnsharpenMask.None))
            //GenericArguments.optional(GenericArguments.enumValue(Text.of("DitherMode"), DitherMode.class), DitherMode.FloydSteinberg),
            // GenericArguments.optional(GenericArguments.doubleNum(Text.of("ColorBleedReductionPercent")), 0.0d))
            .permission("paintings.add")
            .executor(( src, args ) -> {
                try {
                    return cmdUpldPainting(src, args);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            })
            .build();
    CommandSpec listPainting = CommandSpec.builder()
            .description(Text.of("Upload painting from web"))
            .extendedDescription(Text.of("Enter URL of a picture, map(s) containing painting will be generated"))
            .permission("paintings.use")
            .executor(new listCmd())
            .build();
    CommandSpec painting = CommandSpec.builder()
            .description(Text.of("Upload painting from web"))
            .extendedDescription(Text.of("Enter URL of a picture, map(s) containing painting will be generated"))
            .child(addPainting, "add")
            //.child(listPainting, "list")
            .permission("paintings.use")
            .build();

    public class listCmd implements CommandExecutor {
        @Override
        public CommandResult execute ( CommandSource src, CommandContext args ) {
            Player player = (Player) src;
            Sponge.getCommandManager().process(player, "paintingslist " + "this broke");

            return CommandResult.success();

        }
    }

    @Nonnull
    private CommandResult cmdUpldPainting ( CommandSource cmdSource, CommandContext commandContext ) throws SQLException {
        Player player = (Player) cmdSource;
        String playeruuid = player.getUniqueId().toString();
        String name = commandContext.<String>getOne("Name").get();
        String url = commandContext.<String>getOne("URL").get();

        if (!url.endsWith(".png")) {
            PlayerUtils.tell(cmdSource, Translations.PNG_URL_REQUIRED.s());
            return CommandResult.success();
        }

        int mapsX = commandContext.<Integer>getOne("MapsX").orElse(1);
        int mapsY = commandContext.<Integer>getOne("MapsY").orElse(1);
        if (mapsX > 3 || mapsY > 3) {
            PlayerUtils.tell(player, Translations.PAINTING_TOO_BIG.s());
            return CommandResult.success();
        }

        String command = name + " " + url + " " + mapsX + " " + mapsY;
        if (player.hasPermission("paintings.exempt")) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "uploadpainting " + player.getName() + " " + command);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("New exempt submission : #" + id);
            embed.setAuthor("Submitted by : " + player.getName());
            embed.addField("Size", "X: " + mapsX + "\nY: " + mapsY, false);
            embed.setColor(Color.CYAN);
            embed.setImage(url);

            MagiBridge.jda.getTextChannelById("555462653917790228").sendMessage(embed.build()).queue();
            PlayerUtils.tell(player, Translations.PAINTING_EXEMPT.s());
        } else {

            String uri = "jdbc:sqlite:" + staticRoots + "/DarwinPaintings.db";
            ArrayList<String> queries = new ArrayList<>();
            String query = "Insert into submissions (PlayerUUID, Command, Status) values (?, ?, ?)";
            Connection conn = getDataSource(uri).getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            {
                stmt.setString(1, playeruuid);
                stmt.setString(2, command);
                stmt.setString(3, "Submitted");
                id += 1;
                //stmt.executeQuery();
                PaintingSubmission submission = new PaintingSubmission();
                submission.setCommand(command);
                submission.setPlayerUUID(UUID.fromString(playeruuid));
                submission.setID(id);
                submission.setStatus("Submitted");
                submissions.put(id, submission);
                stmt.execute();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("New submission : #" + id);
                embed.setAuthor("Submitted by : " + player.getName());
                embed.addField("Size", "X: " + mapsX + "\nY: " + mapsY, false);
                embed.setColor(Color.YELLOW);
                embed.setImage(url);

                MagiBridge.jda.getTextChannelById("555462653917790228").sendMessage(embed.build()).queue();
            }
            PlayerUtils.tell(player, Translations.PAINTING_SUBMITTED.s());
            conn.close();
        }
        return CommandResult.success();
    }
}

