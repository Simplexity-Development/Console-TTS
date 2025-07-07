package simplexity.config;

import simplexity.Main;

public class LocaleHandler {


    private static LocaleHandler instance;

    public static LocaleHandler getInstance() {
        if (instance == null) instance = new LocaleHandler();
        return instance;
    }

    private String errorGeneral, messageNotParsable, unknownCommand,
            nullAwsCreds, saveAwsInfo, helpHeader, helpCommands, feedbackReload, feedbackShuttingDown;

    public void reloadMessages() {
        YmlConfig config = Main.getLocaleConfig();
        errorGeneral = config.getOption("error.general", String.class, "<br-red>ERROR: %error%</color>");
        messageNotParsable = config.getOption("error.message-not-parsable", String.class, "<br-red>ERROR: Message was not parsable, attempted to send: %message%</color>");
        unknownCommand = config.getOption("error.unknown-command", String.class, "<br-red>ERROR: '%command%' is not a recognized command</color>");
        nullAwsCreds = config.getOption("error.null-aws-credentials", String.class, "<br-red>ERROR: AWS credentials are null, please fill them out</color>");
        saveAwsInfo = config.getOption("error.save-aws-info", String.class, "<br-yellow>Please save your AWS credentials in tts-config.conf, then click enter to continue</color>");
        helpHeader = config.getOption("help.header", String.class, "<br-cyan>Console Text To Speech</color>");
        helpCommands = config.getOption("help.commands", String.class, "<blue>%command_name%</color> <white>-</color> <br-blue>%command_description%</color>");
        feedbackReload = config.getOption("feedback.reload", String.class, "<green>Config reloaded</color>");
        feedbackShuttingDown = config.getOption("feedback.shutting-down", String.class, "<br-yellow>CLI Text To Speech is closing...</color>");
    }


    public String getErrorGeneral() {
        return errorGeneral;
    }

    public String getMessageNotParsable() {
        return messageNotParsable;
    }

    public String getUnknownCommand() {
        return unknownCommand;
    }

    public String getNullAwsCreds() {
        return nullAwsCreds;
    }

    public String getSaveAwsInfo() {
        return saveAwsInfo;
    }

    public String getHelpHeader() {
        return helpHeader;
    }

    public String getHelpCommands() {
        return helpCommands;
    }

    public String getFeedbackReload() {
        return feedbackReload;
    }

    public String getFeedbackShuttingDown() {
        return feedbackShuttingDown;
    }
}
