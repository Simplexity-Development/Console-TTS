package simplexity.commands;

import org.slf4j.event.Level;
import simplexity.Main;
import simplexity.config.LocaleHandler;
import simplexity.httpserver.LocalServer;
import simplexity.console.Logging;

public class ExitCommand extends Command {

    public ExitCommand(String name, String usage) {
        super(name, usage);
    }

    @Override
    public void execute() {
        Logging.logAndPrint(logger, LocaleHandler.getInstance().getFeedbackShuttingDown(), Level.INFO);
        LocalServer.stop();
        System.exit(0);
    }
}
