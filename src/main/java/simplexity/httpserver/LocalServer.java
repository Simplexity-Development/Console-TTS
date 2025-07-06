package simplexity.httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.config.config.ConfigHandler;
import simplexity.config.locale.Message;
import simplexity.util.Logging;

import java.io.IOException;
import java.net.InetSocketAddress;

public class LocalServer {
    private static final Logger logger = LoggerFactory.getLogger(LocalServer.class);
    public static com.sun.net.httpserver.HttpServer server;

    public static void run() {
        try {
            setupServer();
        } catch (Exception exception) {
            Logging.logAndPrint(logger, Message.GENERAL_ERROR.getMessage().replace("%error%", exception.getMessage()), Level.TRACE);
        }
    }

    public static void stop() {
        server.stop(0);
    }

    private static void setupServer() {
        try {
            server = com.sun.net.httpserver.HttpServer.create(
                    new InetSocketAddress(ConfigHandler.getInstance().getServerPort()), 0);
            server.setExecutor(null);
            server.createContext("/", new ChatHandler());
            server.start();
        } catch (IOException exception) {
            Logging.logAndPrint(logger, Message.GENERAL_ERROR.getMessage().replace("%error%", exception.getMessage()), Level.TRACE);
        }
    }


}
