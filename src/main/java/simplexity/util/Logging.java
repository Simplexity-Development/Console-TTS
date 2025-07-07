package simplexity.util;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.regex.Pattern;

public class Logging {

    public static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");

    public static void logAndPrint(Logger logger, String message, Level level) {
        String logMessage = stripAnsiCodes(message);
        logger.atLevel(level).log(logMessage);
        System.out.println(ColorTags.parse(message));
    }

    public static void log(Logger logger, String message, Level level) {
        logger.atLevel(level).log(message);
    }

    public static String stripAnsiCodes(String text) {
        return ANSI_PATTERN.matcher(text).replaceAll("");
    }
}
