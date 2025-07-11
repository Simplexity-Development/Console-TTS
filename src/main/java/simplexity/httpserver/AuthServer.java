package simplexity.httpserver;


import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.config.ConfigHandler;
import simplexity.config.LocaleHandler;
import simplexity.console.Logging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class AuthServer {
    private static final Logger logger = LoggerFactory.getLogger(AuthServer.class);
    public static HttpServer server;

    public static void run(){
        try {
            setupServer();
        } catch (IOException e) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getAuthServerFailed(), Level.WARN);
            Logging.log(logger, Arrays.toString(e.getStackTrace()), Level.WARN);
        }

    }

    public static void stop(){
        Logging.log(logger, "Shutting down auth server", Level.INFO);
        server.stop(0);
    }

    private static void setupServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(ConfigHandler.getInstance().getAuthPort()), 0);
        server.createContext("/", new AuthHandler());
        server.setExecutor(null);
        server.start();
    }



}
