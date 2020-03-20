package com.darwinreforged.servermodifications.translations;

import com.intellectualcrafters.plot.util.StringMan;
import org.spongepowered.api.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Translations {

//    Default formats
    PREFIX("$3[] $1"),
    COLOR_PRIMARY("b"),
    COLOR_SECONDARY("3"),
    COLOR_MINOR("7"),
    COLOR_ERROR("c"),
    PLAYER_ONLY_COMMAND("$4This command can only be executed by players"),
    
//    Time differences
    TIME_DAYS_AGO("$1{0} $2days ago"),
    TIME_YESTERDAY("$1Yesterday"),
    TIME_HOURS_AGO("$1{0} $2hours ago"),
    TIME_HOUR_AGO("$1One $2hour ago"),
    TIME_MINUTES_AGO("$1{0} $2minutes ago"),
    TIME_MINUTE_AGO("$1One $2minute ago"),
    TIME_SECONDS_AGO("$1{0} $2seconds ago"),
    TIME_JUST_NOW("$1Just now"),

//    Tickets
    TICKET_STATUS_OPEN("$2Open"),
    TICKET_STATUS_HELD("$2Held"),
    TICKET_STATUS_CLAIMED("$2Claimed"),
    TICKET_STATUS_CLOSED("$2Closed"),
    REJECTED_TICKET_SOURCE("Rejected and closed ticket #{0}"),
    REJECTED_TICKET_TARGET("$4Your application was reviewed but was not yet approved, make sure to read the given feedback on your plot, and apply again once ready!"),
    TICKET_CLAIM_BUTTON("$1[$2Claim$1]"),
    TICKET_CLAIM_BUTTON_HOVER("$1Click here to claim this ticket."),
    TICKET_UNCLAIM_BUTTON("$1[$2Unclaim$1]"),
    TICKET_UNCLAIM_BUTTON_HOVER("$1Click here to unclaim this ticket."),
    TICKET_CLOSE_BUTTON("$1[$2Approve$1]"),
    TICKET_CLOSE_BUTTON_HOVER("$1Click here to approve and close this ticket."),
    TICKET_REOPEN_BUTTON("$1[$2Reopen$1]"),
    TICKET_REOPEN_BUTTON_HOVER("$1Click here to reopen this ticket."),
    TICKET_COMMENT_BUTTON("$1[$2Comment$1]"),
    TICKET_COMMENT_BUTTON_HOVER("$1Click here to put a comment on this ticket."),
    TICKET_HOLD_BUTTON("$1[$2Hold$1]"),
    TICKET_HOLD_BUTTON_HOVER("$1Click here to put this ticket on hold."),
    TICKET_YES_BUTTON("$1[$2Yes$1]"),
    TICKET_YES_BUTTON_HOVER("$1Click here to confirm the overwrite."),
    TICKET_REJECT_BUTTON("$1[$2Reject$1]"),
    TICKET_REJECT_BUTTON_HOVER("$1Click here to reject and close this ticket."),
    UNKNOWN_ERROR("$1An error occurred. {0}"),
    TICKET_ERROR_INCORRECT_USAGE("$1Incorrect Usage: {0}"),
    TICKET_ERROR_BANNED("$1You are not allowed to open new ticket."),
    TICKET_ERROR_BANNED_ALREADY("$1{0} is already banned from opening tickets."),
    TICKET_ERROR_BAN_USER("$1Cannot ban {0} from opening new ticket."),
    TICKET_ERROR_UNBAN_USER("$1Cannot unban {0} from opening new ticket."),
    TICKET_ERROR_NOT_BANNED("$1{0} is not banned from opening tickets."),
    TICKET_ERROR_PERMISSION("$1You need permission \"{0}\" to do that."),
    TICKET_ERROR_ALREADY_CLOSED("$1Ticket is already closed."),
    TICKET_ERROR_NOT_CLOSED("$1Ticket #{0} is not closed or on hold."),
    TICKET_ERROR_ALREADY_HOLD("$1Ticket is already on hold."),
    TICKET_ERROR_OWNER("$1You are not the owner of that ticket."),
    TICKET_ERROR_CLAIM("$1Ticket #{0} is already claimed by {1}."),
    TICKET_ERROR_UNCLAIM("$1Ticket #{0} is claimed by {1}."),
    TICKET_ERROR_USER_NOT_EXIST("$1The specified user {0} does not exist or contains invalid characters."),
    TICKET_ERROR_SERVER("$1Ticket #{0} was opened on another server."),
    TICKET_TELEPORT("$1Teleported to ticket #{0}."),
    TICKET_ASSIGN("$2{0} has been assigned to ticket #{1}."),
    TICKET_ASSIGN_USER("$2Your ticket #{0} has been assigned to {1}."),
    TICKET_COMMENT_EDIT("$2Ticket #{0} already has a comment attached. Do you wish to overwrite this?"),
    TICKET_COMMENT("$2A comment was added to ticket #{0} by {1}."),
    TICKET_COMMENT_USER("$2Your comment was added to ticket #{0}."),
    TICKET_CLAIM("$2{0} is now handling ticket #{1}."),
    TICKET_CLAIM_USER("$2{0} is now handling your ticket #{1}."),
    TICKET_CLOSE("$2Ticket #{0} was closed by {1}."),
    TICKET_CLOSE_OFFLINE("$2A ticket has been closed while you were offline."),
    TICKET_CLOSE_OFFLINE_MULTI("$2While you were gone, {0} tickets were closed. Use /{1} to see your currently open tickets."),
    TICKET_CLOSE_USER("$2Your Ticket #{0} has been closed by {1}."),
    TICKET_DUPLICATE("$1Your ticket has not been opened because it was detected as a duplicate."),
    TICKET_OPEN("$1A new ticket has been opened by {0}, id assigned #{1}."),
    TICKET_OPEN_USER("$2You opened a ticket, it has been assigned ID #{0}. A staff member should be with you soon."),
    TICKET_TITLE_NOTIFICATION("A new ticket has been opened by {0}, id assigned #{1}."),
    TICKET_TELEPORT_HOVER("Click here to teleport to this tickets location."),
    TICKET_READ_NONE_OPEN("$2There are no open tickets."),
    TICKET_READ_NONE_SELF("$2You have no open tickets."),
    TICKET_READ_NONE_CLOSED("$2There are no closed tickets."),
    TICKET_READ_NONE_HELD("$2There are no tickets currently on hold."),
    TICKET_HOLD("$2Ticket #{0} was put on hold by {1}"),
    TICKET_HOLD_USER("$2Your ticket #{0} was put on hold by {1}"),
    TICKET_UNRESOLVED("$1There are {0} open tickets. Type /{1} to see them."),
    TICKET_UNRESOLVED_HELD("$1There are {0} open tickets and {1} ticket on hold. Type /{2} to see them."),
    TICKET_UNCLAIM("$2{0} is no longer handling ticket #{1}."),
    TICKET_UNCLAIM_USER("$2{0} is no longer handling your ticket #{1}."),
    TICKET_NOT_EXIST("$1Ticket #{0} does not exist."),
    TICKET_NOT_CLAIMED("$1Ticket #{0} is not claimed."),
    TICKET_NOT_OPEN("$1The ticket #{0} is not open."),
    TICKET_REOPEN("$2{0} has reopened ticket #{1}"),
    TICKET_REOPEN_USER("$2{0} has reopened your ticket #{1}"),
    TICKET_TOO_SHORT("$1Your ticket needs to contain at least {0} words."),
    TICKET_TOO_MANY("$1You have too many open tickets, please wait before opening more."),
    TICKET_TOO_FAST("$1You need to wait {0} seconds before attempting to open another ticket."),
    TICKET_STAFF_LIST_SEPERATOR("$1, "),
    TICKET_STAFF_LIST_TITLE("$2Online Staff"),
    TICKET_STAFF_LIST_EMPTY("$1There are no staff members online."),
    TICKET_STAFF_LIST_PADDING("="),
    TICKET_PLUGIN_OUTDATED("$1You are not running the latest recommended build! Recommended build is: $2{0}"),

    //    BrushToolTips
    HOLDING_UNSET_TOOL("$4It seems you are holding a brush tool but it is not set. To suggest it, click this command : $2"),

    //    Friends
    ACCEPTING_TP("$2Accept teleports from friends : $1{0}"),
    FRIEND_TELEPORTED("$2{0} $1teleported to your location"),
    NO_TP_NOT_FRIENDS("$4You are not friends with that user so you cannot teleport to them"),
    ALREADY_FRIENDS("You are already friends with $2{0}"),
    FRIEND_ADDED("You are now freinds with $2{0}"),
    REQUEST_SENT("$1A friend request was sent to $2{0}"),
    REQUEST_RECEIVED("$2{0} $1has requested to befriend you"),
    FRIEND_REMOVED("$3{0} $1was removed as friend"),

    //    WeatherPlugin
    UNKNOWN_WEATHER_TYPE("$4That weather type is unknown"),
    PLOT_WEATHER_SET("$2Plot weather set to: $1{0}"),

    //    HeadsEvolved
    OPEN_GUI_ERROR("$4Failed to open Head Database GUI"),

    //    Layerheight
    HEIGHT_TOO_HIGH("$4Height cannot be above 8"),
    HEIGHT_TOO_LOW("$4Height cannot be below 1"),
    HEIGHTTOOL_NAME("$1Layer Height Tool: $2{0}"),
    HEIGHTTOOL_SET("Successfully set the layer height to $2{0}"),
    HEIGHTTOOL_FAILED_BIND("$4Tool cannot be bound to a block"),
    OUTSIDE_PLOT("$4You are not standing inside a plot"),

    //    HotbarShare
    SHARED_HOTBAR_WITH("You shared your hotbar with {0}"),
    PLAYER_SHARED_HOTBAR("$2{0} $1shared their hotbar"),
    FULL_HOTBAR("$4Your hotbar is full, please clear one or more slots"),
    HOTBAR_SHARE_HEADER("$1§m------- §r$2{0}'s Hotbar $1§m-------"),
    HOTBAR_SHARE_INDEX("$2#{0} : $1{1}"),
    HOTBAR_SHARE_ENCHANTED("$2  Enchanted : "),
    HOTBAR_SHARE_LORE("$2  Lore : "),

    //    Paintings
    PNG_URL_REQUIRED("$4URLs have to end with .png, please make sure to upload an image rather than a webpage including it"),
    PAINTING_TOO_BIG("$4Paintings can have a maximum size of 3x3"),
    PAINTING_EXEMPT("You are exempt from needing permission, check your list of paintings"),
    PAINTING_SUBMITTED("Submitted a new request, once accepted the painting will be automatically uploaded"),

    //    UserData
    USER_DATA_HEADER("$1§m------- §r$2{0} $1§m-------"),
    USER_DATA_FAILED_COLLECT("$4Could not collect data for {0}"),

//    MultiCommand
    MULTI_CMD_PERFORMING("Performing command : $2{0}")
    ;

    private String s;

    Translations(String s) {
        this.s = s;
    }

    public String s() {
        return parseColors(this.s);
    }

    public static String format(String m, Object... args) {
        if (args.length == 0) return m;
        Map<String, String> map = new LinkedHashMap<>();
        if (args.length > 0) {
            for (int i = args.length - 1; i >= 0; i--) {
                String arg = "" + args[i];
                if (arg == null || arg.isEmpty()) map.put(String.format("{%d}", i), "");
                if (i == 0) map.put("%s", arg);
            }
        }
        m = StringMan.replaceFromMap(m, map);
        return parseColors(m);
    }

    private static String parseColors(String m) {
        return m
                .replaceAll("\\$1", '§' + COLOR_PRIMARY.s)
                .replaceAll("\\$2", '§' + COLOR_SECONDARY.s)
                .replaceAll("\\$3", '§' + COLOR_MINOR.s)
                .replaceAll("\\$4", '§' + COLOR_ERROR.s);
    }

    public String f(final Object... args) {
        return format(this.s, args);
    }

    public static String shorten(String m, int maxChars) {
        if (m.length() > maxChars) m = String.format("%s...", m.substring(0, maxChars));
        return m;
    }

    public static String shorten(String m) {
        return shorten(m, 20);
    }

    public String sh() {
        return shorten(s());
    }

    public String sh(int maxChars) {
        return shorten(s(), maxChars);
    }

    public Text ft(final Object... args) {
        return Text.of(f(args));
    }

    public Text t() {
        return Text.of(s());
    }
}
