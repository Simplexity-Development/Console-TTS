package simplexity.amazon;

import com.amazonaws.regions.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.config.ConfigHandler;
import simplexity.config.LocaleHandler;
import simplexity.console.Logging;

public class PollyInit {
    private static final Logger logger = LoggerFactory.getLogger(PollyInit.class);
    private static PollyHandler pollyHandler;

    public static void setupPollyAndSpeech() {
        createPollyHandler();
    }

    public static PollyHandler createPollyHandler() {
        PollyHandler handler = null;
        String awsAccessID = ConfigHandler.getInstance().getAwsAccessID();
        String awsSecretKey = ConfigHandler.getInstance().getAwsSecretKey();
        Region awsRegion = ConfigHandler.getInstance().getAwsRegion();
        if (awsAccessID.isEmpty() || awsSecretKey.isEmpty() || awsRegion == null) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getSaveAwsInfo(), Level.WARN);
            return null;
        }
        try {
            handler = new PollyHandler(awsAccessID, awsSecretKey, awsRegion);
        } catch (IllegalArgumentException e) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getNullAwsCreds(), Level.ERROR);
        }
        pollyHandler = handler;
        return handler;
    }

    public static PollyHandler getPollyHandler() {
        if (pollyHandler == null) return createPollyHandler();
        return pollyHandler;
    }
}
