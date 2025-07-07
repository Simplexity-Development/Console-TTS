package simplexity.config;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.model.VoiceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.Main;
import simplexity.util.Logging;

import java.util.HashSet;
import java.util.Map;

public class ConfigHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConfigHandler.class);
    private final HashSet<VoicePrefixRule> voicePrefixRules = new HashSet<>();
    private final HashSet<SpeechEffectRule> effectRules = new HashSet<>();
    private final HashSet<TextReplaceRule> textReplaceRules = new HashSet<>();

    private Region awsRegion;
    private VoiceId defaultVoice;
    private String awsAccessID, awsSecretKey;
    private Integer serverPort;
    private static ConfigHandler instance;

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
            Logging.log(logger, "Generating new instance of AwsConfig", Level.INFO);
        }
        return instance;
    }

    public void reloadValues() {
        YmlConfig config = Main.getConfig();
        reloadSpeechEffects(config);
        reloadAwsValues(config);
        reloadTextReplace(config);
        serverPort = config.getOption("internal-settings.server-port", Integer.class, 3000);
    }

    public void reloadAwsValues(YmlConfig config) {
        awsAccessID = config.getOption("aws-api.access-key", String.class, "");
        awsSecretKey = config.getOption("aws-api.secret-key", String.class, "");
        Regions region = config.getOption("aws-api.region", Regions.class, Regions.US_EAST_1);
        if (region != null) awsRegion = Region.getRegion(region);
        defaultVoice = config.getOption("aws-api.default-voice", VoiceId.class, VoiceId.Brian);
        reloadVoicePrefixes(config);
    }

    public void reloadVoicePrefixes(YmlConfig config) {
        voicePrefixRules.clear();
        for (String key : config.getKeys("aws-api.voice-prefixes")) {
            String voiceName = config.getOption("aws-api.voice-prefixes." + key, String.class);
            if (voiceName == null) continue;
            try {
                VoiceId voiceId = VoiceId.valueOf(voiceName);
                VoicePrefixRule prefixRule = new VoicePrefixRule(key, voiceId);
                voicePrefixRules.add(prefixRule);
            } catch (IllegalArgumentException e) {
                Logging.logAndPrint(logger, "issue", Level.WARN); //todo actual warning
            }
        }
    }

    public void reloadSpeechEffects(YmlConfig config) {
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

    public void reloadTextReplace(YmlConfig config) {
        textReplaceRules.clear();
        for (String key : config.getKeys("text-replacements")) {
            String replacementText = config.getOption("text-replacements." + key, String.class);
            if (replacementText == null) continue;
            TextReplaceRule replaceRule = new TextReplaceRule(key, replacementText);
            textReplaceRules.add(replaceRule);
        }
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

    public Integer getServerPort() {
        return serverPort;
    }
}
