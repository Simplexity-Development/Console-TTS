package simplexity.config;

public class LocaleHandler {


    private static LocaleHandler instance;

    public static LocaleHandler getInstance() {
        if (instance == null) instance = new LocaleHandler();
        return instance;
    }

    private String errorGeneral, messageNotParsable, unknownCommand, authServerFailed, authorizationFailed, authRefreshFailed,
            feedbackRefreshingToken, feedbackTokenRefreshed, initializationFailed, unableToCheckAuth, noRefreshToken,
            unableToOpenAuthPage, nullAwsCreds, saveAwsInfo, helpHeader, helpCommands, feedbackReload, feedbackShuttingDown,
            feedbackEnterConfigValue, feedbackUpdatedConfig, unableToSetConfigValue, configCommandUsage;

    public void reloadMessages() {
        YmlConfig config = ConfigInit.getLocaleConfig();
        errorGeneral = config.getOption("error.general", String.class, "<br-red>ERROR: %error%</color>");
        messageNotParsable = config.getOption("error.message-not-parsable", String.class, "<br-red>ERROR: Message was not parsable, attempted to send: %message%</color>");
        unknownCommand = config.getOption("error.unknown-command", String.class, "<br-red>ERROR: '%command%' is not a recognized command</color>");
        nullAwsCreds = config.getOption("error.null-aws-credentials", String.class, "<br-red>ERROR: AWS credentials are null, please fill them out</color>");
        saveAwsInfo = config.getOption("error.save-aws-info", String.class, "<br-yellow>Please save your AWS credentials in tts-config.conf, then click enter to continue</color>");
        authServerFailed = config.getOption("error.auth-server-failed", String.class, "<br-red>ERROR: Auth server initialization failed, please check logs for more info</color>");
        authorizationFailed = config.getOption("error.authorization-failed", String.class, "<br-red>ERROR: Authorization failed. Please check logs for more info</color>");
        authRefreshFailed = config.getOption("error.refresh-auth-failed", String.class, "<br-red>ERROR: Failed to refresh authorization token, please check logs for more info</color>");
        initializationFailed = config.getOption("error.initialization-failed", String.class, "<br-red>ERROR: Failed to initialize Twitch Client</color>");
        unableToCheckAuth = config.getOption("error.unable-to-check-auth", String.class, "<br-red>ERROR: Failed to connect to Twitch to check authorization</color>");
        noRefreshToken = config.getOption("error.no-refresh-token-provided", String.class, "<br-red>ERROR: No refresh token was found - please clear the twitch tokens and go through initialization again</color>");
        unableToOpenAuthPage = config.getOption("error.unable-to-open-auth-page", String.class, "<br-red>ERROR: Unable to open auth page</color>");
        helpHeader = config.getOption("help.header", String.class, "<br-cyan>Console Text To Speech</color>");
        helpCommands = config.getOption("help.commands", String.class, "<blue>%command_name%</color> <white>-</color> <br-blue>%command_description%</color>");
        configCommandUsage = config.getOption("help.config-usage", String.class, "<blue>Usage: list | set <path> <value> | exit</color>");
        feedbackRefreshingToken = config.getOption("feedback.refreshing-token", String.class, "<br-yellow>Twitch Auth token has expired - Attempting to refresh token...");
        feedbackTokenRefreshed = config.getOption("feedback.token-refreshed", String.class, "<br-green>Successfully Refreshed Twitch Auth Token</color>");
        feedbackReload = config.getOption("feedback.reload", String.class, "<green>Config reloaded</color>");
        feedbackShuttingDown = config.getOption("feedback.shutting-down", String.class, "<br-yellow>CLI Text To Speech is closing...</color>");
        feedbackEnterConfigValue = config.getOption("feedback.enter-config-value", String.class, "<blue>Enter config command (i.e., 'list', 'set path.to.key <value>', or 'exit'):</color>");
        feedbackUpdatedConfig = config.getOption("feedback.updated-config", String.class, "<br-green>Updated config value: %s to %s</color>");
        unableToSetConfigValue = config.getOption("error.unable-to-set-config-value", String.class, "<br-red>ERROR: Unable to set config value at %s to %s");

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

    public String getAuthServerFailed() {
        return authServerFailed;
    }

    public String getAuthorizationFailed() {
        return authorizationFailed;
    }

    public String getAuthRefreshFailed() {
        return authRefreshFailed;
    }

    public String getInitializationFailed() {
        return initializationFailed;
    }

    public String getUnableToCheckAuth() {
        return unableToCheckAuth;
    }

    public String getFeedbackRefreshingToken() {
        return feedbackRefreshingToken;
    }

    public String getFeedbackTokenRefreshed() {
        return feedbackTokenRefreshed;
    }

    public String getNoRefreshToken() {
        return noRefreshToken;
    }

    public String getUnableToOpenAuthPage() {
        return unableToOpenAuthPage;
    }

    public String getFeedbackEnterConfigValue() {
        return feedbackEnterConfigValue;
    }

    public String getFeedbackUpdatedConfig() {
        return feedbackUpdatedConfig;
    }

    public String getUnableToSetConfigValue() {
        return unableToSetConfigValue;
    }

    public String getConfigCommandUsage() {
        return configCommandUsage;
    }
}
