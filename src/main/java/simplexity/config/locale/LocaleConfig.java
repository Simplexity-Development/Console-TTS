package simplexity.config.locale;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.config.AbstractConfig;
import simplexity.util.ColorTags;
import simplexity.util.Logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LocaleConfig extends AbstractConfig {
    private static final Logger logger = LoggerFactory.getLogger(LocaleConfig.class);
    private static LocaleConfig instance;

    public LocaleConfig() {
        super();
        Logging.log(logger, "Initializing LocaleConfig class", Level.INFO);
    }

    public static LocaleConfig getInstance() {
        if (instance == null) {
            instance = new LocaleConfig();
        }
        return instance;
    }

    @Override
    public void createDefaultConfig(File configFile) {
        try (FileWriter writer = new FileWriter(configFile)) {
            for (Message message : Message.values()) {
                String localeMessage = "\"%path%\"= \"%message%\"\n";
                localeMessage = localeMessage.replace("%path%", message.getPath());
                localeMessage = localeMessage.replace("%message%", message.getMessage());
                writer.write(localeMessage);
            }
        } catch (IOException exception) {
            Logging.logAndPrint(logger, "Failed to create default locale config: " + exception.getMessage(), Level.ERROR);
        }
    }

    @Override
    public void reloadConfig() {
        Logging.log(logger, "Reloading config file locale.conf", Level.INFO);
        loadConfig("locale.conf", "configs");
        Config localeConfig = getConfig();
        for (Message message : Message.values()) {
            String localeMessage = localeConfig.getString(message.getPath());
            if (localeMessage == null) continue;
            localeMessage = ColorTags.parse(localeMessage);
            message.setMessage(localeMessage);
            Logging.log(logger, "Setting " + message + " to " + localeMessage, Level.DEBUG);
        }
    }

}
