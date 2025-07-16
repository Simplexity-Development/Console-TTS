package simplexity.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.config.ConfigHandler;
import simplexity.config.LocaleHandler;
import simplexity.console.Logging;
import simplexity.httpserver.AuthServer;
import simplexity.httpserver.TokenValidator;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class TwitchInit {

    private static TwitchClient twitchClient;
    private static final Logger logger = LoggerFactory.getLogger(TwitchInit.class);

    public static void initializeTwitch() {
        if (!ConfigHandler.getInstance().shouldUseTwitch()) return;
        if (ConfigHandler.getInstance().getTwitchAccessToken() == null || ConfigHandler.getInstance().getTwitchAccessToken().isEmpty()) {
            AuthServer.run();
            openBrowser();
        }
        twitchClient = getTwitchClient();
        twitchClient.getChat().joinChannel(ConfigHandler.getInstance().getTwitchChannel());
        TwitchListeners.chatListener();
    }

    private static void initTwitchClient() {
        String clientId = ConfigHandler.getInstance().getTwitchClientId();
        String clientSecret = ConfigHandler.getInstance().getTwitchClientSecret();
        String accessToken = ConfigHandler.getInstance().getTwitchAccessToken();

        if (clientId == null || clientSecret == null || accessToken == null || clientId.isEmpty() || clientSecret.isEmpty() || accessToken.isEmpty()) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getInitializationFailed(), Level.WARN);
            return;
        }

        if (!TokenValidator.isTokenValid()) {
            try {
                boolean refreshed = TokenValidator.refreshToken();
                if (!refreshed) return;
            } catch (IOException e) {
                Logging.log(logger, LocaleHandler.getInstance().getErrorGeneral().replace("%error%", Arrays.toString(e.getStackTrace())), Level.WARN);
                return;
            }

            accessToken = ConfigHandler.getInstance().getTwitchAccessToken();
        }
        OAuth2Credential credential = new OAuth2Credential("twitch", accessToken);

        twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableChat(true)
                .withEnableEventSocket(true)
                .withChatAccount(credential)
                .withDefaultAuthToken(credential)
                .build();
    }

    public static TwitchClient getTwitchClient() {
        if (twitchClient == null) initTwitchClient();
        return twitchClient;
    }

    private static void openBrowser() {
        String clientId = ConfigHandler.getInstance().getTwitchClientId();
        int port = ConfigHandler.getInstance().getAuthPort();
        String redirectUri = "http://localhost:" + port;
        String authUrl = String.format(
                "https://id.twitch.tv/oauth2/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=chat%%3Aread+chat%%3Aedit&force_verify=true",
                clientId, redirectUri
        );

        try {
            Desktop.getDesktop().browse(new URI(authUrl));
        } catch (Exception e) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getUnableToOpenAuthPage(), Level.ERROR);
        }
    }
}
