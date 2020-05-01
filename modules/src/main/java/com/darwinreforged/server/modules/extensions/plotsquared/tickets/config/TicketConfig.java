package com.darwinreforged.server.modules.extensions.plotsquared.tickets.config;

import com.darwinreforged.server.sponge.DarwinServer;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.modules.extensions.plotsquared.tickets.TicketModule;

import java.io.IOException;
import java.util.Optional;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class TicketConfig {

    // TODO : Throw this away and clean up

    private TicketModule module;

    public static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode config;

    public TicketConfig(TicketModule main) throws IOException, ObjectMappingException {
        Optional<TicketModule> moduleOptional = DarwinServer.getModule(TicketModule.class);
        if (moduleOptional.isPresent()) {
            module = main;

            loader = HoconConfigurationLoader.builder().setPath(module.defaultConf).build();
            config = loader.load();
            configCheck();
        }
    }

    public static Boolean soundNotification;
    public static Boolean staffNotification;
    public static Boolean titleNotification;

    public static int minWords;
    public static int delayTimer;
    public static int maxTickets;
    public static Boolean preventDuplicates;

    public static Boolean hideOffline;
    public static int ticketsPerPage;
    public static int nagTimer;
    public static Boolean nagHeld;

    public static Boolean checkForUpdate;

    public static String language;

    //Database
    public static String storageEngine;
    public static String databaseFile;
    public static String h2Prefix;
    public static String mysqlHost;
    public static int mysqlPort;
    public static String mysqlDatabase;
    public static String mysqlUser;
    public static String mysqlPass;
    public static String mysqlPrefix;
    public static String server;

    // TODO : What even.. split this up ASAP

    private void configCheck() throws IOException, ObjectMappingException {
        if (!module.defaultConf.toFile().exists()) {
            module.defaultConf.toFile().createNewFile();
        }

        //server
        server = check(config.getNode("server"), "", "Required, Name of the server. Used for ticket's originating server identification").getString();

        //locale
        language = check(config.getNode("language"), "EN", "Localization to be used, All available translations are in the 'localization' folder").getString();

        // notifications
        soundNotification = check(config.getNode("notifications", "sound"), true, "If true, a notification sound will be played when requests are created.").getBoolean();
        staffNotification = check(config.getNode("notifications", "staff"), true, "Notifies staff members when a new request is filed.").getBoolean();
        titleNotification = check(config.getNode("notifications", "title"), false, "Notifies staff members with a title message in the centre of the screen.").getBoolean();

        // ticket
        minWords = check(config.getNode("ticket", "user", "minimum-words"), 3).getInt();
        delayTimer = check(config.getNode("ticket", "user", "delay"), 60, "User has to wait this amount of seconds before opening another ticket.").getInt();
        maxTickets = check(config.getNode("ticket", "user", "max-tickets"), 5, "Maximum number of tickets a user may have open at the same time.").getInt();
        preventDuplicates = check(config.getNode("ticket", "user", "prevent-duplicates"), true, "Prevent duplicate tickets by the same user.").getBoolean();

        hideOffline = check(config.getNode("ticket", "user", "hide-offline"), false, "If set to true, hides all tickets in /ticket read from offline users.").getBoolean();
        ticketsPerPage = check(config.getNode("ticket", "user", "tickets-per-page"), 5, "This sets the total amount of tickets that should be shown on each page.").getInt();
        nagTimer = check(config.getNode("ticket", "user", "nag"), 5, "If above 0 (minutes), nag the online staff members about open tickets.").getInt();
        nagHeld = check(config.getNode("ticket", "user", "nag-held"), true, "If true, the nag feature will mention tickets on hold. ").getBoolean();

        checkForUpdate = check(config.getNode("update", "check"), true, "If true, will notify at startup and if a player with \"" + Permissions.TICKET_STAFF.p() + "\" logs in, if there is an update available.").getBoolean();

        //Database
        storageEngine = check(config.getNode("storage", "storage-engine"), "h2", "The stoage engine that should be used, Allowed values: h2 or mysql").getString();
        databaseFile = check(config.getNode("storage", "h2", "database-file"), "Database.db", "Where the databaseFile will be stored. Can be a relative or absolute path. An absolute path is recommended when using this to synchronize over several servers").getString();
        h2Prefix = check(config.getNode("storage", "h2", "prefix"), "mmctickets_", "Prefix for the plugin tables").getString();
        mysqlHost = check(config.getNode("storage", "mysql", "host"), "localhost", "Host of the MySQL Server").getString();
        mysqlPort = check(config.getNode("storage", "mysql", "port"), "3306", "Port of the MySQL server. Default: 3306").getInt();
        mysqlDatabase = check(config.getNode("storage", "mysql", "database"), "mmctickets", "The database to store in").getString();
        mysqlUser = check(config.getNode("storage", "mysql", "user"), "root", "The user for the database").getString();
        mysqlPass = check(config.getNode("storage", "mysql", "password"), "pass", "Password for that user").getString();
        mysqlPrefix = check(config.getNode("storage", "mysql", "table-prefix"), "mmctickets_", "Prefix for the plugin tables").getString();

        loader.save(config);

    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue, String comment) {
        if (node.isVirtual()) {
            node.setValue(defaultValue).setComment(comment);
        }
        return node;
    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue) {
        if (node.isVirtual()) {
            node.setValue(defaultValue);
        }
        return node;
    }
}
