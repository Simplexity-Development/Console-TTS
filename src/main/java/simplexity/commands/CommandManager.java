package simplexity.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.config.locale.Message;
import simplexity.util.Logging;

import java.util.HashMap;

public class CommandManager {
    private final HashMap<String, Command> commands = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    public void registerCommand(Command command) {
        Logging.log(logger, "Registering command: " + command.name, Level.INFO);
        commands.put(command.getName(), command);
    }

    public boolean runCommand(String command) {
        if (command.startsWith("--") && !commands.containsKey(command)) {
            Logging.logAndPrint(logger, Message.UNKNOWN_COMMAND.getMessage().replace("%command%", command), Level.ERROR);
            return true;
        }
        if (!commands.containsKey(command)){
            return false;
        }
        commands.get(command).execute();
        return true;
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }
}
