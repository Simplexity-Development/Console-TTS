package simplexity.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.commands.CommandManager;
import simplexity.commands.ExitCommand;
import simplexity.commands.HelpCommand;
import simplexity.commands.ReloadCommand;
import simplexity.config.ConfigHandler;
import simplexity.config.LocaleHandler;
import simplexity.twitch.TwitchInit;

import java.io.IOException;
import java.util.Arrays;

public class ConsoleInit {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleInit.class);
    private static CommandManager commandManager;
    private static InputManager inputManager;

    public static void initializeConsoleStuff() {
        initializeCommands();
        inputManager = new InputManager(TwitchInit.getTwitchClient(), ConfigHandler.getInstance().getTwitchUsername());
        try {
            inputManager.start();
        } catch (IOException e) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getErrorGeneral().replace("%error%", Arrays.toString(e.getStackTrace())), Level.ERROR);
        }
    }

    private static void initializeCommands() {
        commandManager = new CommandManager();
        commandManager.registerCommand(new HelpCommand("--help", "Displays the help messages"));
        commandManager.registerCommand(new ExitCommand("--exit", "Terminates the program"));
        commandManager.registerCommand(new ReloadCommand("--reload", "Reloads the configuration"));
    }

    public static InputManager getInputManager() {
        return inputManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }
}
