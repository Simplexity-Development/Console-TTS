package simplexity.httpserver;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.config.ConfigHandler;
import simplexity.config.ConfigInit;
import simplexity.config.LocaleHandler;
import simplexity.console.Logging;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TokenValidator {

    private static final Logger logger = LoggerFactory.getLogger(TokenValidator.class);

    public static boolean isTokenValid() {
        String accessToken = ConfigHandler.getInstance().getTwitchAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            Logging.log(logger, "Access token null or empty, token is invalid", Level.INFO);
            return false;
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(Constants.TOKEN_VALIDATE_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "OAuth " + accessToken);

            int responseCode = connection.getResponseCode();
            Logging.log(logger, "Token validation response: " + connection.getResponseMessage(), Level.INFO);
            if (responseCode == 200) {
                return true;
            }
        } catch (Exception e) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getUnableToCheckAuth(), Level.ERROR);
        }
        return false;
    }

    public static boolean refreshToken() throws IOException {
        Logging.logAndPrint(logger, LocaleHandler.getInstance().getFeedbackRefreshingToken(), Level.INFO);
        String refreshToken = ConfigHandler.getInstance().getTwitchRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getNoRefreshToken(), Level.WARN);
            return false;
        }
        JSONObject refreshed = AuthHandler.refreshAccessToken(refreshToken);
        if (refreshed == null || !refreshed.has("access_token")) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getAuthRefreshFailed(), Level.WARN);
            if (refreshed != null) {
                Logging.log(logger, "Token Response: " + refreshed, Level.WARN);
            }
            return false;
        }
        String newAccessToken = refreshed.getString("access_token");
        String newRefreshToken = refreshed.optString("refresh_token", refreshToken);

        ConfigInit.getTokenConfig().setValue("tokens.twitch.access-token", newAccessToken);
        ConfigInit.getTokenConfig().setValue("tokens.twitch.refresh-token", newRefreshToken);
        ConfigInit.getTokenConfig().saveConfig();
        ConfigHandler.getInstance().reloadValues();

        Logging.logAndPrint(logger, LocaleHandler.getInstance().getFeedbackTokenRefreshed(), Level.INFO);
        return true;
    }
}
