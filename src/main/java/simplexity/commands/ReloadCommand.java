package simplexity.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.Main;
import simplexity.config.ConfigHandler;
import simplexity.config.LocaleHandler;
import simplexity.httpserver.LocalServer;
import simplexity.util.Logging;

import java.io.IOException;

public class ReloadCommand extends Command {

    private static final Logger logger = LoggerFactory.getLogger(ReloadCommand.class);

    public ReloadCommand(String name, String usage) {
        super(name, usage);
    }

    @Override
    public void execute() {
        Logging.log(logger, "Reloading configs", Level.INFO);
        try {
            Main.getConfig().reloadConfig();
            Main.getLocaleConfig().reloadConfig();
            ConfigHandler.getInstance().reloadValues();
            LocaleHandler.getInstance().reloadMessages();
        } catch (IOException e) {
            Logging.logAndPrint(logger, "Fatal Error Reloading Config Files", Level.WARN);
            Logging.logAndPrint(logger, e.getStackTrace().toString(), Level.WARN);
            System.exit(-1);
        }
        Logging.log(logger, "Stopping local server", Level.INFO);
        LocalServer.stop();
        Logging.log(logger, "Starting local server", Level.INFO);
        LocalServer.run();
        Logging.logAndPrint(logger, LocaleHandler.getInstance().getFeedbackReload(), Level.INFO);
    }
}
