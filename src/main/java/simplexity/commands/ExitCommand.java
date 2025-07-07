package simplexity.commands;

import org.slf4j.event.Level;
import simplexity.Main;
import simplexity.config.locale.Message;
import simplexity.httpserver.LocalServer;
import simplexity.util.Logging;

public class ExitCommand extends Command {

    public ExitCommand(String name, String usage) {
        super(name, usage);
    }

    @Override
    public void execute() {
        Logging.logAndPrint(logger, Message.SHUTTING_DOWN.getMessage(), Level.INFO);
        Main.runApp = false;
        LocalServer.stop();
        System.exit(0);
    }
}
