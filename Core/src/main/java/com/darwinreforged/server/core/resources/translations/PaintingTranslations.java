package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("paintings")
public class PaintingTranslations {

    public static final Translation PNG_URL_REQUIRED = Translation.create("$4URLs have to end with .png, please make sure to upload an image rather than a webpage including it");
    public static final Translation PAINTING_TOO_BIG = Translation.create("$4Paintings can have a maximum size of 3x3");
    public static final Translation PAINTING_EXEMPT = Translation.create("You are exempt from needing permission, check your list of paintings");
    public static final Translation PAINTING_SUBMITTED = Translation.create("Submitted a new request, once accepted the painting will be automatically uploaded");
    public static final Translation PAINTING_STATUS_SUBMITTED = Translation.create("Submitted");
    public static final Translation PAINTING_STATUS_REJECTED = Translation.create("Rejected");
    public static final Translation PAINTING_STATUS_APPROVED = Translation.create("Approved");
    public static final Translation PAINTING_DISCORD_TITLE = Translation.create("Submissions");
    public static final Translation PAINTING_DISCORD_FIELD_TITLE = Translation.create("Submission ID : #{0}");
    public static final Translation PAINTING_DISCORD_FIELD_VALUE = Translation.create("Submitted by : {0}\nStatus : {1}");
    public static final Translation PAINTING_SUBMISSION_NOT_FOUND = Translation.create("Cannot find submission : {0}");
    public static final Translation PAINTING_SUBMISSION_LIST = Translation.create("Submission IDs : {0}");
    public static final Translation PAINTING_APPROVING = Translation.create("Approving Submission : {0}");
    public static final Translation PAINTING_REJECTING = Translation.create("Rejecting Submission : {0}");
    public static final Translation PAINTING_CANNOT_UPDATE_STATUS = Translation.create("Cannot {0} this submission (#{1}). Are you sure it exists?");
    public static final Translation PAINTING_NEW_SUBMISSION_TITLE = Translation.create("New submission : #{0}");
    public static final Translation PAINTING_NEW_EXEMPT_SUBMISSION_TITLE = Translation.create("New exempt submission : #{0}");
    public static final Translation PAINTING_SUBMISSION_AUTHOR = Translation.create("Submitted by : {0}");
    public static final Translation PAINTING_SUBMISSION_SIZE_TITLE = Translation.create("Size");
    public static final Translation PAINTING_SUBMISSION_SIZE_VALUE = Translation.create("X: {0}\nY: {1}");

    private PaintingTranslations() {
    }
}
