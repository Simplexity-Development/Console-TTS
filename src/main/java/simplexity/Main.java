package simplexity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.amazon.PollyInit;
import simplexity.config.ConfigInit;
import simplexity.console.ConsoleInit;
import simplexity.console.Logging;
import simplexity.httpserver.LocalServer;
import simplexity.twitch.TwitchInit;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Logging.log(logger, "Starting application", Level.INFO);
        ConfigInit.initializeConfigs();
        ConsoleInit.initializeConsoleStuff();
        PollyInit.setupPollyAndSpeech();
        TwitchInit.initializeTwitch();
        LocalServer.run();
    }

}
