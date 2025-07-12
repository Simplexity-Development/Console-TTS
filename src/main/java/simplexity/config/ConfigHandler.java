package simplexity.config;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.model.VoiceId;
import com.github.twitch4j.common.enums.CommandPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.Main;
import simplexity.config.rules.SpeechEffectRule;
import simplexity.config.rules.TextReplaceRule;
import simplexity.config.rules.VoicePrefixRule;
import simplexity.console.Logging;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConfigHandler.class);
    private final HashSet<VoicePrefixRule> voicePrefixRules = new HashSet<>();
    private final HashSet<SpeechEffectRule> effectRules = new HashSet<>();
    private final HashSet<TextReplaceRule> textReplaceRules = new HashSet<>();
    private final HashSet<ChatFormat> chatFormats = new HashSet<>();

    private Region awsRegion;
    private VoiceId defaultVoice;
    private String awsAccessID, awsSecretKey, twitchChannel, twitchClientId, twitchClientSecret, twitchAccessToken,
            twitchRefreshToken, twitchChatFormat, twitchUsername;
    private Integer serverPort, authPort;
    private Boolean useTwitch;
    private static ConfigHandler instance;

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

    public void reloadValues() {
        YmlConfig config = Main.getConfig();
        YmlConfig keyConfig = Main.getTokenConfig();
        reloadSpeechEffects(config);
        reloadAwsValues(config);
        reloadVoicePrefixes(config);
        reloadTextReplace(config);
        reloadTwitchValues(config);
        reloadChatFormats(config);
        reloadKeys(keyConfig);
    }

    private void reloadAwsValues(YmlConfig config) {
        Logging.log(logger, "Reloading AWS Values", Level.INFO);

        Regions region = config.getOption("aws-api.region", Regions.class, Regions.US_EAST_1);
        if (region != null) awsRegion = Region.getRegion(region);
        defaultVoice = config.getOption("aws-api.default-voice", VoiceId.class, VoiceId.Brian);
        serverPort = config.getOption("internal-settings.server-port", Integer.class, 3000);

    }

    private void reloadVoicePrefixes(YmlConfig config) {
        Logging.log(logger, "Reloading Voice Prefixes", Level.INFO);
        voicePrefixRules.clear();
        for (String key : config.getKeys("aws-api.voice-prefixes")) {
            String voiceName = config.getOption("aws-api.voice-prefixes." + key, String.class);
            if (voiceName == null) continue;
            try {
                VoiceId voiceId = VoiceId.valueOf(voiceName);
                VoicePrefixRule prefixRule = new VoicePrefixRule(key, voiceId);
                voicePrefixRules.add(prefixRule);
            } catch (IllegalArgumentException e) {
                String message = "[Config] Config value at 'aws-api.voice-prefixes." + key + "' is invalid";
                Logging.log(logger, message, Level.WARN);
            }
        }
    }

    private void reloadSpeechEffects(YmlConfig config) {
        Logging.log(logger, "Reloading Speech Effects", Level.INFO);
        effectRules.clear();
        for (String key : config.getKeys("speech-effect-markdown")) {
            StringBuilder openingBuilder = new StringBuilder();
            StringBuilder closingBuilder = new StringBuilder();
            openingBuilder.append("<");
            closingBuilder.append("</");
            Map<String, String> options = config.getHashMap("speech-effect-markdown." + key, String.class, String.class);
            for (String option : options.keySet()) {
                if (option.equals("type")) {
                    openingBuilder.append(options.get(option));
                    closingBuilder.append(options.get(option));
                    continue;
                }
                openingBuilder.append(" ").append(option).append("=\"").append(options.get(option)).append("\"");
            }
            openingBuilder.append(">");
            closingBuilder.append(">");
            SpeechEffectRule effectRule = new SpeechEffectRule(key, openingBuilder.toString(), closingBuilder.toString());
            effectRules.add(effectRule);
        }
    }

    private void reloadTextReplace(YmlConfig config) {
        Logging.log(logger, "Reloading Text Replacements", Level.INFO);
        textReplaceRules.clear();
        for (String key : config.getKeys("text-replacements")) {
            String replacementText = config.getOption("text-replacements." + key, String.class);
            if (replacementText == null) continue;
            TextReplaceRule replaceRule = new TextReplaceRule(key, replacementText);
            textReplaceRules.add(replaceRule);
        }
    }

    private void reloadChatFormats(YmlConfig config){
        Logging.log(logger, "Reloading chat formats", Level.INFO);
        chatFormats.clear();
        Set<String> keySet = config.getKeys("twitch-api.chat");
        if (keySet == null || keySet.isEmpty()) return;
        for (String key : keySet) {
            String formatString = config.getOption("twitch-api.chat." + key + ".format", String.class, "%user% ➜ %message%");
            int weight = config.getOption("twitch-api.chat." + key + ".weight", Integer.class, 0);
            String permissionString = config.getOption("twitch-api.chat." + key + "permission", String.class, "EVERYONE");
            CommandPermission permission;
            try {
                permission = CommandPermission.valueOf(permissionString);
            } catch (IllegalArgumentException e) {
                Logging.log(logger, "Permission type: " + permissionString + " is invalid. Check https://twitch4j.github.io/javadoc/com/github/twitch4j/common/enums/CommandPermission.html for valid permissions", Level.WARN);
                continue;
            }
            ChatFormat format = new ChatFormat(formatString, weight, permission);
            chatFormats.add(format);
        }
    }

    private void reloadTwitchValues(YmlConfig config) {
        Logging.log(logger, "Reloading twitch values", Level.INFO);
        useTwitch = config.getOption("twitch-api.enable", Boolean.class, Boolean.FALSE);
        if (!useTwitch) return;
        twitchChannel = config.getOption("twitch-api.channel", String.class);
        twitchUsername = config.getOption("twitch-api.username", String.class, "");
        authPort = config.getOption("internal-settings.twitch-auth-port", Integer.class, 8080);

    }

    private void reloadKeys(YmlConfig config) {
        Logging.log(logger, "Loading Keys", Level.INFO);
        awsAccessID = config.getOption("keys.aws.access-key", String.class, "");
        awsSecretKey = config.getOption("keys.aws.secret-key", String.class, "");
        twitchClientId = config.getOption("keys.twitch.client-id", String.class, "");
        twitchClientSecret = config.getOption("keys.twitch.client-secret", String.class, "");
        twitchAccessToken = config.getOption("tokens.twitch.access-token", String.class, "");
        twitchRefreshToken = config.getOption("tokens.twitch.refresh-token", String.class, "");
    }


    public String getAwsAccessID() {
        return awsAccessID;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public HashSet<VoicePrefixRule> getVoicePrefixRules() {
        return voicePrefixRules;
    }

    public Region getAwsRegion() {
        return awsRegion;
    }

    public VoiceId getDefaultVoice() {
        return defaultVoice;
    }

    public HashSet<SpeechEffectRule> getEffectRules() {
        return effectRules;
    }

    public HashSet<TextReplaceRule> getTextReplaceRules() {
        return textReplaceRules;
    }

    public HashSet<ChatFormat> getChatFormats() {
        return chatFormats;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public String getTwitchChannel() {
        return twitchChannel;
    }

    public String getTwitchClientId() {
        return twitchClientId;
    }

    public String getTwitchClientSecret() {
        return twitchClientSecret;
    }

    public Integer getAuthPort() {
        return authPort;
    }

    public Boolean shouldUseTwitch() {
        return useTwitch;
    }

    public String getTwitchAccessToken() {
        return twitchAccessToken;
    }

    public String getTwitchRefreshToken() {
        return twitchRefreshToken;
    }

    public String getTwitchChatFormat() {
        return twitchChatFormat;
    }

    public String getTwitchUsername() {
        return twitchUsername;
    }
}
