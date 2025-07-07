package simplexity.config;

import simplexity.Main;

public class LocaleHandler {


    private static LocaleHandler instance;

    public static LocaleHandler getInstance() {
        if (instance == null) instance = new LocaleHandler();
        return instance;
    }

    private String errorGeneral, invalidInput, invalidVoice, invalidRegion, messageNotParsable, unknownCommand,
            nullAwsCreds, saveAwsInfo, helpHeader, helpCommands, feedbackReload, feedbackShuttingDown;

    public void reloadMessages() {
        YmlConfig config = Main.getLocaleConfig();
        errorGeneral = config.getOption("error.general", String.class, "<br-red>ERROR: %error%</color>");
        invalidInput = config.getOption("error.invalid-input", String.class, "<br-red>ERROR: Invalid input</color>");
        invalidVoice = config.getOption("error.invalid-voice", String.class, "<br-red>Error: '%voice%' is not a valid voice. \\nPlease make sure you are only choosing from standard voices.</color> \\nStandard voices can be found here: \\n<yellow>https://docs.aws.amazon.com/polly/latest/dg/voicelist.html</color>");
        invalidRegion = config.getOption("error.invalid-region", String.class, "<br-red>Error: '%region%' is not a valid region.</color> \\n<yellow>Regions can be found here: \\nhttps://aws.amazon.com/about-aws/global-infrastructure/regions_az/ \\nUsing default region of 'US_EAST_1'");
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

    public String getInvalidInput() {
        return invalidInput;
    }

    public String getInvalidVoice() {
        return invalidVoice;
    }

    public String getInvalidRegion() {
        return invalidRegion;
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
