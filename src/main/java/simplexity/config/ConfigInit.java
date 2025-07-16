package simplexity.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.console.Logging;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ConfigInit {

    private static final Logger logger = LoggerFactory.getLogger(ConfigInit.class);

    private static YmlConfig config;
    private static YmlConfig localeConfig;
    private static YmlConfig tokenConfig;

    public static void initializeConfigs() {
        File configFile = new File("config/config.yml");
        File localeFile = new File("config/locale.yml");
        File tokenFile = new File("config/tokens.yml");
        try {
            config = new YmlConfig(configFile, "config.yml");
            localeConfig = new YmlConfig(localeFile, "locale.yml");
            tokenConfig = new YmlConfig(tokenFile, "tokens.yml");
        } catch (IOException e) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getErrorGeneral().replace("%error%", Arrays.toString(e.getStackTrace())), Level.ERROR);
            System.exit(-1);
        }
        LocaleHandler.getInstance().reloadMessages();
        ConfigHandler.getInstance().reloadValues();
    }

    public static YmlConfig getConfig() {
        return config;
    }

    public static YmlConfig getLocaleConfig() {
        return localeConfig;
    }

    public static YmlConfig getTokenConfig() {
        return tokenConfig;
    }
}
