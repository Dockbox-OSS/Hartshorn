package com.darwinreforged.servermodifications.modules;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.listeners.PaintingsDiscordListener;
import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModule;
import com.darwinreforged.servermodifications.objects.PaintingSubmission;
import com.darwinreforged.servermodifications.resources.Permissions;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.todo.FileManager;
import com.darwinreforged.servermodifications.util.todo.PaintingsDatabaseUtil;
import com.magitechserver.magibridge.MagiBridge;

import net.dv8tion.jda.core.EmbedBuilder;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.awt.Color;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

@ModuleInfo(id = "darwinpaintings", name = "Darwin paintings approval", version = "1.0", description = "Approve a painting before uploading it")
public class PaintingsModule extends PluginModule {

    public static SqlService sql;

    private int id;
    public static HashMap<Integer, PaintingSubmission> submissions = new HashMap<>();

    public PaintingsModule() {
    }

    @Listener
    public void onServerFinishLoad ( GameStartingServerEvent event ) throws SQLException {
        DarwinServer.registerCommand(painting, "paintings");
        DarwinServer.registerListener(new PaintingsDiscordListener());
        Path dataPath = FileManager.getDataDirectory(DarwinServer.getModule(PaintingsModule.class).get());
        new PaintingsDatabaseUtil(sql, dataPath);

        String uri = "jdbc:sqlite:" + dataPath + "/DarwinPaintings.db";
        String query = String.format("SELECT * from Submissions where Status = '%s'", Translations.PAINTING_STATUS_SUBMITTED.s());
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
                    if (submission.getStatus().equals(Translations.PAINTING_STATUS_SUBMITTED.s())) {
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
            .arguments(
                    GenericArguments.string(Text.of("Name")),
                    GenericArguments.string(Text.of("URL")),
                    GenericArguments.optional(GenericArguments.seq(GenericArguments.integer(Text.of("MapsX")), GenericArguments.integer(Text.of("MapsY")))))

            .permission(Permissions.ADD_PAINTING.p())
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
    CommandSpec painting = CommandSpec.builder()
            .description(Text.of("Upload painting from web"))
            .child(addPainting, "add")
            //.child(listPainting, "list")
            .permission(Permissions.USE_PAINTING.p())
            .build();

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
        if (player.hasPermission(Permissions.PAINTING_EXEMPT.p())) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "uploadpainting " + player.getName() + " " + command);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(Translations.PAINTING_NEW_EXEMPT_SUBMISSION_TITLE.f(id));
            embed.setAuthor(Translations.PAINTING_SUBMISSION_AUTHOR.f(player.getName()));
            embed.addField(Translations.PAINTING_SUBMISSION_SIZE_TITLE.s(), Translations.PAINTING_SUBMISSION_SIZE_VALUE.f(mapsX, mapsY), false);
            embed.setColor(Color.CYAN);
            embed.setImage(url);

            MagiBridge.jda.getTextChannelById("555462653917790228").sendMessage(embed.build()).queue();
            PlayerUtils.tell(player, Translations.PAINTING_EXEMPT.s());
        } else {
            Path dataPath = FileManager.getDataDirectory(DarwinServer.getModule(PaintingsModule.class).get());
            String uri = "jdbc:sqlite:" + dataPath + "/DarwinPaintings.db";
            String query = "Insert into submissions (PlayerUUID, Command, Status) values (?, ?, ?)";
            Connection conn = getDataSource(uri).getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            {
                stmt.setString(1, playeruuid);
                stmt.setString(2, command);
                stmt.setString(3, Translations.PAINTING_STATUS_SUBMITTED.s());
                id += 1;
                //stmt.executeQuery();
                PaintingSubmission submission = new PaintingSubmission();
                submission.setCommand(command);
                submission.setPlayerUUID(UUID.fromString(playeruuid));
                submission.setID(id);
                submission.setStatus(Translations.PAINTING_STATUS_SUBMITTED.s());
                submissions.put(id, submission);
                stmt.execute();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(Translations.PAINTING_NEW_SUBMISSION_TITLE.f(id));
                embed.setAuthor(Translations.PAINTING_SUBMISSION_AUTHOR.f(player.getName()));
                embed.addField(Translations.PAINTING_SUBMISSION_SIZE_TITLE.s(), Translations.PAINTING_SUBMISSION_SIZE_VALUE.f(mapsX, mapsY), false);
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

