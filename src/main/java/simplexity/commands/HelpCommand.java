package simplexity.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.Main;
import simplexity.config.locale.Message;
import simplexity.util.Logging;

public class HelpCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(HelpCommand.class);

    public HelpCommand(String name, String usage) {
        super(name, usage);
    }

    @Override
    public void execute() {
        Logging.logAndPrint(logger, Message.HELP_HEADER.getMessage(), Level.INFO);
        for (Command command : Main.getCommandManager().getCommands().values()) {
            String commandHelpMessage = Message.HELP_COMMAND_MESSAGE.getMessage().replace("%command_name%", command.getName())
                    .replace("%command_description%", command.getDescription());
            Logging.logAndPrint(logger, commandHelpMessage, Level.INFO);
        }
    }
}
