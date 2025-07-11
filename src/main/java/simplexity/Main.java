package simplexity;

import com.github.twitch4j.TwitchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.amazon.PollyHandler;
import simplexity.amazon.PollySetup;
import simplexity.amazon.SpeechHandler;
import simplexity.commands.CommandManager;
import simplexity.commands.ExitCommand;
import simplexity.commands.HelpCommand;
import simplexity.commands.ReloadCommand;
import simplexity.config.ConfigHandler;
import simplexity.config.LocaleHandler;
import simplexity.config.YmlConfig;
import simplexity.console.InputHandler;
import simplexity.console.Logging;
import simplexity.httpserver.AuthServer;
import simplexity.httpserver.LocalServer;
import simplexity.twitch.ChatConsoleManager;
import simplexity.twitch.TwitchInitializer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static YmlConfig config;
    private static YmlConfig localeConfig;
    private static YmlConfig tokenConfig;
    private static CommandManager commandManager;
    public static PollyHandler pollyHandler;
    private static SpeechHandler speechHandler;
    private static TwitchClient twitchClient;
    private static InputHandler inputHandler;

    public static boolean runApp = true;

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        Logging.log(logger, "Starting application", Level.INFO);
        File configFile = new File("config/config.yml");
        File localeFile = new File("config/locale.yml");
        File tokenFile = new File("config/tokens.yml");
        try {
            config = new YmlConfig(configFile, "config.yml");
            localeConfig = new YmlConfig(localeFile, "locale.yml");
            tokenConfig = new YmlConfig(tokenFile, "tokens.yml");
        } catch (IOException e) {
            System.out.println("Fatal Error: Config was unable to be generated.");
            e.printStackTrace();
            return;
        }
        LocaleHandler.getInstance().reloadMessages();
        ConfigHandler.getInstance().reloadValues();
        commandManager = new CommandManager();
        speechHandler = new SpeechHandler();
        registerCommands(commandManager);
        PollySetup.setupPollyAndSpeech();
        LocalServer.run();
        if (ConfigHandler.getInstance().shouldUseTwitch() && (ConfigHandler.getInstance().getTwitchAccessToken() == null || ConfigHandler.getInstance().getTwitchAccessToken().isEmpty())) {
            AuthServer.run();
            openBrowser();
        }
        if (ConfigHandler.getInstance().shouldUseTwitch()) {
            twitchClient = TwitchInitializer.getTwitchClient();
            twitchClient.getChat().joinChannel(ConfigHandler.getInstance().getTwitchChannel());
            ChatConsoleManager consoleManager = new ChatConsoleManager(twitchClient, ConfigHandler.getInstance().getTwitchUsername(), speechHandler);
            consoleManager.start();
        }
        inputHandler = new InputHandler(commandManager, speechHandler);
        inputHandler.runLoop();
    }

    private static void registerCommands(CommandManager commandManager) {
        commandManager.registerCommand(new HelpCommand("--help", "Displays the help messages"));
        commandManager.registerCommand(new ExitCommand("--exit", "Terminates the program"));
        commandManager.registerCommand(new ReloadCommand("--reload", "Reloads the configuration"));
    }

    private static void openBrowser() {
        String clientId = ConfigHandler.getInstance().getTwitchClientId();
        int port = ConfigHandler.getInstance().getAuthPort();
        String redirectUri = "http://localhost:" + port;
        String authUrl = String.format(
                "https://id.twitch.tv/oauth2/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=chat%%3Aread+chat%%3Aedit&force_verify=true",
                clientId, redirectUri
        );

        try {
            Desktop.getDesktop().browse(new URI(authUrl));
        } catch (Exception e) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getUnableToOpenAuthPage(), Level.ERROR);
        }
    }


    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static InputHandler getInputHandler() {
        return inputHandler;
    }

    public static PollyHandler getPollyHandler() {
        return pollyHandler;
    }

    public static void setSpeechHandler(SpeechHandler speechHandlerToSet) {
        speechHandler = speechHandlerToSet;
    }

    public static void setPollyHandler(PollyHandler pollyHandlerToSet) {
        pollyHandler = pollyHandlerToSet;
    }

    public static YmlConfig getConfig() {
        return config;
    }

    public static YmlConfig getLocaleConfig() {
        return localeConfig;
    }

    public static YmlConfig getTokenConfig() {
        return tokenConfig;
    }
}
