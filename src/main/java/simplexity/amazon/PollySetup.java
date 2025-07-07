package simplexity.amazon;

import com.amazonaws.regions.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.Main;
import simplexity.config.ConfigHandler;
import simplexity.config.LocaleHandler;
import simplexity.console.Logging;

public class PollySetup {
    private static final Logger logger = LoggerFactory.getLogger(PollySetup.class);

    public static void setupPollyAndSpeech() {
        connectToPolly();
        Main.setSpeechHandler(new SpeechHandler());
    }

    public static PollyHandler createPollyHandler() {
        PollyHandler pollyHandler = null;
        String awsAccessID = ConfigHandler.getInstance().getAwsAccessID();
        String awsSecretKey = ConfigHandler.getInstance().getAwsSecretKey();
        Region awsRegion = ConfigHandler.getInstance().getAwsRegion();
        if (awsAccessID.isEmpty() || awsSecretKey.isEmpty() || awsRegion == null) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getSaveAwsInfo(), Level.WARN);
            return null;
        }
        try {
            pollyHandler = new PollyHandler(awsAccessID, awsSecretKey, awsRegion);
        } catch (IllegalArgumentException e) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getNullAwsCreds(), Level.ERROR);
        }
        return pollyHandler;
    }

    public static void connectToPolly() {
        Main.setPollyHandler(createPollyHandler());
        if (Main.getPollyHandler() != null) {
            return;
        }
        Logging.logAndPrint(logger, LocaleHandler.getInstance().getSaveAwsInfo(), Level.INFO);
        System.exit(-1);
    }
}
