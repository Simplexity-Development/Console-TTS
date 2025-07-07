package simplexity;

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
import simplexity.httpserver.LocalServer;

import java.io.File;
import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static YmlConfig config;
    private static YmlConfig localeConfig;
    private static CommandManager commandManager;
    public static PollyHandler pollyHandler;
    private static SpeechHandler speechHandler;
    private static InputHandler inputHandler;

    public static boolean runApp = true;

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        Logging.log(logger, "Starting application", Level.INFO);
        File file = new File("config/config.yml");
        File localeFile = new File("config/locale.yml");
        try {
            config = new YmlConfig(file, "config.yml");
            localeConfig = new YmlConfig(localeFile, "locale.yml");
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
        inputHandler = new InputHandler(commandManager, speechHandler);
        inputHandler.runLoop();
    }

    private static void registerCommands(CommandManager commandManager) {
        commandManager.registerCommand(new HelpCommand("--help", "Displays the help messages"));
        commandManager.registerCommand(new ExitCommand("--exit", "Terminates the program"));
        commandManager.registerCommand(new ReloadCommand("--reload", "Reloads the configuration"));
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

}
