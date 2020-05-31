package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("tickets")
public class TicketTranslations {

    public static final Translation TICKET_STATUS_OPEN = Translation.create("status_open", "$2Open");
    public static final Translation TICKET_STATUS_HELD = Translation.create("status_held", "$2Held");
    public static final Translation TICKET_STATUS_CLAIMED = Translation.create("status_claimed", "$2Claimed");
    public static final Translation TICKET_STATUS_CLOSED = Translation.create("status_closed", "$2Closed");
    public static final Translation REJECTED_TICKET_SOURCE = Translation.create("rejected", "Rejected and closed ticket #{0}");
    public static final Translation REJECTED_TICKET_TARGET = Translation.create("not_approved", "$4Your application was reviewed but was not yet approved, make sure to read the given feedback on your plot, and apply again once ready!");
    public static final Translation TICKET_CLAIM_BUTTON = Translation.create("btn_claim", "$1[$2Claim$1]");
    public static final Translation TICKET_CLAIM_BUTTON_HOVER = Translation.create("btn_claim_hover", "$1Click here to claim this ticket.");
    public static final Translation TICKET_UNCLAIM_BUTTON = Translation.create("btn_unclaim", "$1[$2Unclaim$1]");
    public static final Translation TICKET_UNCLAIM_BUTTON_HOVER = Translation.create("btn_unclaim_hover", "$1Click here to unclaim this ticket.");
    public static final Translation TICKET_REOPEN_BUTTON = Translation.create("btn_reopen", "$1[$2Reopen$1]");
    public static final Translation TICKET_REOPEN_BUTTON_HOVER = Translation.create("btn_reopen_hover", "$1Click here to reopen this ticket.");
    public static final Translation TICKET_COMMENT_BUTTON = Translation.create("btn_comment", "$1[$2Comment$1]");
    public static final Translation TICKET_COMMENT_BUTTON_HOVER = Translation.create("btn_comment_hover", "$1Click here to put a comment on this ticket.");
    public static final Translation TICKET_HOLD_BUTTON = Translation.create("btn_hold", "$1[$2Hold$1]");
    public static final Translation TICKET_HOLD_BUTTON_HOVER = Translation.create("btn_hold_hover", "$1Click here to put this ticket on hold.");
    public static final Translation TICKET_YES_BUTTON = Translation.create("btn_yes", "$1[$2Yes$1]");
    public static final Translation TICKET_YES_BUTTON_HOVER = Translation.create("btn_yes_hover", "$1Click here to confirm the overwrite.");
    public static final Translation TICKET_REJECT_BUTTON = Translation.create("btn_reject", "$1[$2Reject$1]");
    public static final Translation TICKET_REJECT_BUTTON_HOVER = Translation.create("btn_reject_hover", "$1Click here to reject and close this ticket.");
    public static final Translation TICKET_ERROR_INCORRECT_USAGE = Translation.create("error_usage", "$1Incorrect Usage: {0}");
    public static final Translation TICKET_ERROR_BANNED = Translation.create("error_banned", "$1You are not allowed to open new ticket.");
    public static final Translation TICKET_ERROR_BANNED_ALREADY = Translation.create("error_already_banned", "$1{0} is already banned from opening tickets.");
    public static final Translation TICKET_ERROR_BAN_USER = Translation.create("error_cannot_ban", "$1Cannot ban {0} from opening new ticket.");
    public static final Translation TICKET_ERROR_UNBAN_USER = Translation.create("error_cannot_unban", "$1Cannot unban {0} from opening new ticket.");
    public static final Translation TICKET_ERROR_NOT_BANNED = Translation.create("error_not_banned", "$1{0} is not banned from opening tickets.");
    public static final Translation TICKET_ERROR_PERMISSION = Translation.create("error_not_permitted", "$1You need permission \"{0}\" to do that.");
    public static final Translation TICKET_ERROR_ALREADY_CLOSED = Translation.create("error_already_closed", "$1Ticket is already closed.");
    public static final Translation TICKET_ERROR_NOT_CLOSED = Translation.create("error_not_closed_held", "$1Ticket #{0} is not closed or on hold.");
    public static final Translation TICKET_ERROR_ALREADY_HOLD = Translation.create("error_already_held", "$1Ticket is already on hold.");
    public static final Translation TICKET_ERROR_OWNER = Translation.create("error_not_owned", "$1You are not the owner of that ticket.");
    public static final Translation TICKET_ERROR_CLAIM = Translation.create("error_already_claimed", "$1Ticket #{0} is already claimed by {1}.");
    public static final Translation TICKET_ERROR_UNCLAIM = Translation.create("error_claimed_by", "$1Ticket #{0} is claimed by {1}.");
    public static final Translation TICKET_ERROR_USER_NOT_EXIST = Translation.create("error_user_not_found", "$1The specified user {0} does not exist or contains invalid characters.");
    public static final Translation TICKET_ERROR_SERVER = Translation.create("error_opened_server", "$1Ticket #{0} was opened on another server.");
    public static final Translation TICKET_TELEPORT = Translation.create("teleported", "$1Teleported to ticket #{0}.");
    public static final Translation TICKET_ASSIGN = Translation.create("assigned", "$2{0} has been assigned to ticket #{1}.");
    public static final Translation TICKET_ASSIGN_USER = Translation.create("assigned_submitter", "$2Your ticket #{0} has been assigned to {1}.");
    public static final Translation TICKET_COMMENT_EDIT = Translation.create("override_comment", "$2Ticket #{0} already has a comment attached. Do you wish to overwrite this?");
    public static final Translation TICKET_COMMENT = Translation.create("commented", "$2A comment was added to ticket #{0} by {1}.");
    public static final Translation TICKET_COMMENT_USER = Translation.create("commented_submitter", "$2Your comment was added to ticket #{0}.");
    public static final Translation TICKET_CLAIM = Translation.create("claimed", "$2{0} is now handling ticket #{1}.");
    public static final Translation TICKET_CLAIM_USER = Translation.create("claimed_submitter", "$2{0} is now handling your ticket #{1}.");
    public static final Translation TICKET_CLOSE = Translation.create("closed", "$2Ticket #{0} was closed by {1}.");
    public static final Translation TICKET_CLOSE_OFFLINE = Translation.create("closed_offline_submitter", "$2A ticket has been closed while you were offline.");
    public static final Translation TICKET_CLOSE_OFFLINE_MULTI = Translation.create("closed_offline_submitter_multi", "$2While you were gone, {0} tickets were closed. Use /{1} to see your currently open tickets.");
    public static final Translation TICKET_CLOSE_USER = Translation.create("closed_submitter", "$2Your Ticket #{0} has been closed by {1}.");
    public static final Translation TICKET_DUPLICATE = Translation.create("error_duplicate", "$1Your ticket has not been opened because it was detected as a duplicate.");
    public static final Translation TICKET_OPEN = Translation.create("opened", "$1A new ticket has been opened by {0}, id assigned #{1}.");
    public static final Translation TICKET_OPEN_USER = Translation.create("opened_submitter", "$2You opened a ticket, it has been assigned ID #{0}. A staff member should be with you soon.");
    public static final Translation TICKET_TELEPORT_HOVER = Translation.create("teleport", "Click here to teleport to this tickets location.");
    public static final Translation TICKET_READ_NONE_OPEN = Translation.create("no_open_tickets", "$2There are no open tickets.");
    public static final Translation TICKET_READ_NONE_SELF = Translation.create("no_open_tickets_submitter", "$2You have no open tickets.");
    public static final Translation TICKET_READ_NONE_CLOSED = Translation.create("no_closed_tickets", "$2There are no closed tickets.");
    public static final Translation TICKET_READ_NONE_HELD = Translation.create("no_held_tickets", "$2There are no tickets currently on hold.");
    public static final Translation TICKET_HOLD = Translation.create("held", "$2Ticket #{0} was put on hold by {1}");
    public static final Translation TICKET_HOLD_USER = Translation.create("held_submitter", "$2Your ticket #{0} was put on hold by {1}");
    public static final Translation TICKET_UNRESOLVED = Translation.create("notification", "$1There are {0} open tickets. Type /{1} to see them.");
    public static final Translation TICKET_UNRESOLVED_HELD = Translation.create("notification_held", "$1There are {0} open tickets and {1} ticket on hold. Type /{2} to see them.");
    public static final Translation TICKET_UNCLAIM = Translation.create("unclaim", "$2{0} is no longer handling ticket #{1}.");
    public static final Translation TICKET_UNCLAIM_USER = Translation.create("unclaim_submitted", "$2{0} is no longer handling your ticket #{1}.");
    public static final Translation TICKET_NOT_EXIST = Translation.create("error_not_exist", "$1Ticket #{0} does not exist.");
    public static final Translation TICKET_NOT_CLAIMED = Translation.create("error_not_claimed", "$1Ticket #{0} is not claimed.");
    public static final Translation TICKET_NOT_OPEN = Translation.create("error_not_open", "$1The ticket #{0} is not open.");
    public static final Translation TICKET_REOPEN = Translation.create("reopened", "$2{0} has reopened ticket #{1}");
    public static final Translation TICKET_REOPEN_USER = Translation.create("reopened_submitted", "$2{0} has reopened your ticket #{1}");
    public static final Translation TICKET_TOO_SHORT = Translation.create("error_too_short", "$1Your ticket needs to contain at least {0} words.");
    public static final Translation TICKET_TOO_MANY = Translation.create("error_too_many", "$1You have too many open tickets, please wait before opening more.");
    public static final Translation TICKET_TOO_FAST = Translation.create("error_too_fast", "$1You need to wait {0} seconds before attempting to open another ticket.");
    public static final Translation PROMOTE_MEMBER_BUTTON = Translation.create("btn_promote_member", "$3[&bPromote - Member$3]");
    public static final Translation PROMOTE_BUTTON_HOVER = Translation.create("btn_promote_hover", "Promote to {0} and close ticket");
    public static final Translation PROMOTE_EXPERT_BUTTON = Translation.create("btn_promote_expert", "$3[&ePromote - Expert$3]");
    public static final Translation PROMOTE_MASTER_ARCHITECTURE_BUTTON = Translation.create("btn_promote_master_architecture", "$3[$1Promote - MS Arch$3]");
    public static final Translation PROMOTE_MASTER_NATURE_BUTTON = Translation.create("btn_promote_master_nature", "$3[$1Promote - MS Nature$3]");
    public static final Translation PROMOTE_MASTER_BOTH_BUTTON = Translation.create("btn_promote_master_both", "$3[$1Promote - MS Both$3]");
    public static final Translation TICKET_MORE_INFO = Translation.create("ticket_hover", "Click here to get more details for ticket #{0}");
    public static final Translation CLOSED_TICKETS_TITLE = Translation.create("title_closed", "$2Closed Tickets");
    public static final Translation HELD_TICKETS_TITLE = Translation.create("title_held", "$2Held Tickets");
    public static final Translation SELF_TICKETS_TITLE = Translation.create("title_self", "$2Your Tickets");
    public static final Translation TICKET_ROW_SINGLE = Translation.create("ticket_single", "{5} $2#{0} {1} by {2} $2on {3} $2- $3{4}");
    public static final Translation TICKET_HELP_TITLE = Translation.create("title_help", "$2Tickets Help");
    public static final Translation TICKET_SYNTAX_HINT = Translation.create("error_syntax", "$3[] = required  () = optional");
    public static final Translation TICKET_OPEN_TITLE = Translation.create("title_open", "$2{0} Open Tickets");
    public static final Translation TICKET_CLAIMED_BY = Translation.create("claimed_by", "$1Claimed by: $3{0}");
    public static final Translation TICKET_HANDLED_BY = Translation.create("handled_by", "$1Handled by: $3{0}");
    public static final Translation TICKET_COMMENT_CONTENT = Translation.create("comment", "$1Comment: $3{0}");
    public static final Translation TICKET_OPENED_BY = Translation.create("opened_by", "$1Opened by: {0} $2| Submission #{1}");
    public static final Translation TICKET_OPENED_WHEN = Translation.create("opened_when", "$1When: {0}");
    public static final Translation TICKET_OPENED_SERVER = Translation.create("opened_server", "$1Server: ");
    public static final Translation TICKET_MESSAGE_LONG = Translation.create("message_long", "$3{0}");
    public static final Translation TICKET_SINGLE_TITLE = Translation.create("ticket_title", "$2Ticket #{0} $1- $2{1}");
    public static final Translation TICKET_RELOAD_SUCCESS = Translation.create("reload_success", "$1Ticket and Player data reloaded");
    public static final Translation SUBMISSION_REJECTED = Translation.create("submission_rejected", "Submission rejected");
    public static final Translation SUBMISSION_APPROVED = Translation.create("submission_approved", "Submission approved");
    public static final Translation SUBMISSION_ON_HOLD = Translation.create("submission_held", "Submission on hold");
    public static final Translation SUBMISSION_NEW = Translation.create("submission_new", "New submission");
    public static final Translation TICKET_DISCORD_SUBMITTED_BY = Translation.create("discord_submission_by", "Submitted by : {0}");
    public static final Translation TICKET_DISCORD_CLOSED_COMBINED = Translation.create("discord_closed_combi", "ID : #{0}\nPlot : {1}\nClosed by : {2}\nComments : {3}\nTime closed : {4}\nTime opened : {5}");
    public static final Translation TICKET_DISCORD_NEW_COMBINED = Translation.create("discord_new_comb", "ID : #{0}\nPlot : {1}\nTime opened : {2}");
    // TODO : This should be a config setting
    public static final Translation TICKET_DISCORD_RESOURCE_REJECTED = Translation.create("discord_resource_rejected", "https://app.buildersrefuge.com/img/rejected.png");
    public static final Translation TICKET_DISCORD_RESOURCE_APPROVED = Translation.create("discord_resource_approved", "https://app.buildersrefuge.com/img/approved.png");
    public static final Translation TICKET_DISCORD_RESOURCE_HELD = Translation.create("discord_resource_held", "https://icon-library.net/images/stop-sign-icon-png/stop-sign-icon-png-8.jpg");
    public static final Translation TICKET_DISCORD_RESOURCE_NEW = Translation.create("discord_resource_new", "https://app.buildersrefuge.com/img/created.png");
    public static final Translation TICKET_DISCORD_PROMOTED_TO = Translation.create("discord_promoted", "\nPromoted to : {0}");
    public static final Translation TICKET_COMMAND_STAFFLIST = Translation.create("cmd_stafflist", "Display a list of online staff members.");
    public static final Translation TICKET_COMMAND_OPEN = Translation.create("cmd_open", "Open a ticket.");
    public static final Translation TICKET_COMMAND_CLOSE = Translation.create("cmd_close", "Close an open ticket.");
    public static final Translation TICKET_COMMAND_ASSIGN = Translation.create("cmd_assign", "Assign an open ticket to a specified user.");
    public static final Translation TICKET_COMMAND_HOLD = Translation.create("cmd_hold", "Put an open ticket on hold.");
    public static final Translation TICKET_COMMAND_CHECK = Translation.create("cmd_check", "Display a list of open tickets / Give more detail of a ticketID.");
    public static final Translation TICKET_COMMAND_REOPEN = Translation.create("cmd_reopen", "Reopen a closed ticket.");
    public static final Translation TICKET_COMMAND_TP = Translation.create("cmd_tp", "Teleport to where a ticket was created.");
    public static final Translation TICKET_COMMAND_CLAIM = Translation.create("cmd_claim", "Claim an open ticket to let people know you are working on it.");
    public static final Translation TICKET_COMMAND_UNCLAIM = Translation.create("cmd_unclaim", "Unclaim a claimed ticket");
    public static final Translation TICKET_COMMAND_BAN = Translation.create("cmd_ban", "Ban a player from opening new tickets");
    public static final Translation TICKET_COMMAND_UNBAN = Translation.create("cmd_unban", "Unban a player from opening new tickets");
    public static final Translation TICKET_COMMAND_COMMENT = Translation.create("cmd_comment", "Put a comment on a ticket");
    public static final Translation TICKET_COMMAND_RELOAD = Translation.create("cmd_reload", "Reload ticket and player data.");
    public static final Translation TICKET_ERROR_SUBMIT_OUTSIDE_PLOT = Translation.create("error_outside_plot", "$4You can only open a submission while standing inside your own plot!");
    public static final Translation TICKET_CLAIMED_PREFIX = Translation.create("prefix_claimed", "$1Claimed - ");
    public static final Translation TICKET_OPEN_PREFIX = Translation.create("prefix_open", "$1Open - ");
    public static final Translation TICKET_CLOSED_PREFIX = Translation.create("prefix_closed", "$1Closed - ");
    public static final Translation TICKET_HELD_PREFIX = Translation.create("prefix_held", "$2Held $1- ");
    public static final Translation TICKET_DISCORD_DETAILS_BODY = Translation.create("discord_detail_body", "Player : {0}\nSubmitted : {1}\nLocation : {2}\nPlot : {3}\nSubmission : #{4}\nComments : {5}");
    public static final Translation TICKET_DISCORD_DETAILS_TITLE = Translation.create("discord_detail_title", "Ticket : #{0}");
    public static final Translation TICKET_DISCORD_DETAIL_NOT_FOUND = Translation.create("error_discord_detail_not_found", ":no_entry: **Unable to find ticket**");
    public static final Translation TICKET_DISCORD_ROW_BODY = Translation.create("discord_row_body", "By : {0}\nSubmitted : {1}");
    public static final Translation TICKET_DISCORD_ROW_TITLE = Translation.create("discord_row_title", "Open tickets ({0})");

    private TicketTranslations() {
    }
}
