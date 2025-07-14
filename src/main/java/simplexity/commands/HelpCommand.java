package simplexity.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.config.LocaleHandler;
import simplexity.console.ConsoleInit;
import simplexity.console.Logging;

public class HelpCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(HelpCommand.class);

    public HelpCommand(String name, String usage) {
        super(name, usage);
    }

    @Override
    public void execute() {
        Logging.logAndPrint(logger, LocaleHandler.getInstance().getHelpHeader(), Level.INFO);
        for (Command command : ConsoleInit.getCommandManager().getCommands().values()) {
            String commandHelpMessage = LocaleHandler.getInstance().getHelpCommands().replace("%command_name%", command.getName())
                    .replace("%command_description%", command.getDescription());
            Logging.logAndPrint(logger, commandHelpMessage, Level.INFO);
        }
    }
}
