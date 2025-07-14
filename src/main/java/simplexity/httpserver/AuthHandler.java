package simplexity.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.config.ConfigHandler;
import simplexity.config.ConfigInit;
import simplexity.config.LocaleHandler;
import simplexity.console.Logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AuthHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);


    @Override
    public void handle(HttpExchange exchange) {
        try {
            String query = exchange.getRequestURI().getQuery();
            String[] parameters = query.split("&");
            String code = "";
            for (String parameter : parameters) {
                if (parameter.startsWith("code=")) {
                    code = parameter.split("=")[1];
                }
            }
            if (code == null || code.isEmpty()) {
                respondWith(exchange, 400, "<h1>No Authorization Code FoundA</h1>");
                return;
            }
            String clientId = ConfigHandler.getInstance().getTwitchClientId();
            String clientSecret = ConfigHandler.getInstance().getTwitchClientSecret();
            if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
                respondWith(exchange, 500, "<h1>Twitch app credentials (Client ID and Client Secret) not found!</h1>");
                Logging.logAndPrint(logger, LocaleHandler.getInstance().getAuthorizationFailed(), Level.WARN);
                Logging.log(logger, "Client ID or Client Secret is unavailable", Level.WARN);
                return;
            }

            String redirect = Constants.REDIRECT_URI + ConfigHandler.getInstance().getAuthPort();
            String postData = String.format(
                    "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
                    clientId, clientSecret, code, redirect
            );

            JSONObject tokenResponse = sendPostRequest(postData);
            if (tokenResponse.has("access_token")) {
                String accessToken = tokenResponse.getString("access_token");
                String refreshToken = tokenResponse.optString("refresh_token", "");
                ConfigInit.getTokenConfig().setValue("tokens.twitch.access-token", accessToken);
                ConfigInit.getTokenConfig().setValue("tokens.twitch.refresh-token", refreshToken);
                ConfigInit.getTokenConfig().saveConfig();
                ConfigHandler.getInstance().reloadValues();
                respondWith(exchange, 200, "<h1>Authorization successful! You can now close this window!</h1>");
            } else {
                respondWith(exchange, 500, "<h1>Authorization Failed!</h1>");
                Logging.log(logger, "response did not have Access Token, response: " + tokenResponse, Level.ERROR);
            }
        } catch (IOException e) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getAuthorizationFailed(), Level.WARN);
            Logging.log(logger, Arrays.toString(e.getStackTrace()), Level.WARN);
        }
    }

    public static JSONObject refreshAccessToken(String refreshToken) {
        try {
            String clientId = ConfigHandler.getInstance().getTwitchClientId();
            String clientSecret = ConfigHandler.getInstance().getTwitchClientSecret();
            if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
                Logging.logAndPrint(logger, LocaleHandler.getInstance().getAuthorizationFailed(), Level.WARN);
                Logging.log(logger, "Client ID or Client Secret is unavailable", Level.WARN);
                return null;
            }

            String postData = String.format(
                    "client_id=%s&client_secret=%s&refresh_token=%s&grant_type=refresh_token",
                    clientId, clientSecret, refreshToken
            );
            return sendPostRequest(postData);
        } catch (Exception e) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getAuthRefreshFailed(), Level.WARN);
        }
        return null;
    }


    private static JSONObject sendPostRequest(String postData) throws IOException, JSONException {
        URL url = new URL(Constants.TOKEN_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        byte[] postBytes = postData.getBytes(StandardCharsets.UTF_8);
        connection.getOutputStream().write(postBytes);

        int responseCode = connection.getResponseCode();
        InputStream stream = (responseCode == HttpURLConnection.HTTP_OK)
                ? connection.getInputStream()
                : connection.getErrorStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) responseBuilder.append(line);
            return new JSONObject(responseBuilder.toString());
        }

    }


    private void respondWith(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream stream = exchange.getResponseBody()) {
            stream.write(bytes);
        }
        AuthServer.stop();
    }
}
