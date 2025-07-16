package simplexity.amazon;

import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.TextType;
import com.amazonaws.services.polly.model.VoiceId;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.config.ConfigHandler;
import simplexity.config.LocaleHandler;
import simplexity.config.rules.SpeechEffectRule;
import simplexity.config.rules.TextReplaceRule;
import simplexity.config.rules.VoicePrefixRule;
import simplexity.console.Logging;

import java.io.InputStream;

public class SpeechHandler {
    private static final Logger logger = LoggerFactory.getLogger(SpeechHandler.class);
    private VoiceId voiceId;

    public SpeechHandler() {
        this.voiceId = ConfigHandler.getInstance().getDefaultVoice();
        Logging.log(logger, "Initialized SpeechHandler with default voice: " + voiceId.toString(), Level.INFO);
    }

    /**
     * Processes the given text, optionally replacing it based on configurations,
     * and synthesizes and plays the speech.
     */
    public void processSpeech(String text) {
        String processedText = replaceText(text);
        InputStream speechStream = synthesizeSSMLSpeech(processedText, voiceId);
        if (speechStream == null) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getErrorGeneral().replace("%error%", "Speech stream is null"), Level.ERROR);
            return;
        }
        playToDefaultOutput(speechStream);

    }

    /**
     * Replaces text based on replacement mappings and updates the voice if a prefix is matched.
     */

    public String replaceText(String text) {
        for (VoicePrefixRule prefixRule : ConfigHandler.getInstance().getVoicePrefixRules()) {
            if (!prefixRule.matches(text)) continue;
            text = prefixRule.applyRule(text);
            voiceId = prefixRule.getVoiceId();
            break;
        }
        for (SpeechEffectRule effectRule : ConfigHandler.getInstance().getEffectRules()) {
            if (!effectRule.matches(text)) continue;
            text = effectRule.applySSMLRule(text);
        }
        for (TextReplaceRule replaceRule : ConfigHandler.getInstance().getTextReplaceRules()) {
            if (!replaceRule.matches(text)) continue;
            text = replaceRule.applyRule(text);
        }
        return text;
    }

    public InputStream synthesizeSSMLSpeech(String text, VoiceId voice) {
        String ssmlText = "<speak>"
                          + ConfigHandler.getInstance().getDefaultOpeningTag()
                          + text
                          + ConfigHandler.getInstance().getDefaultClosingTag()
                          + "</speak>";
        OutputFormat format = OutputFormat.Mp3;
        try {
            SynthesizeSpeechRequest request = new SynthesizeSpeechRequest()
                    .withText(ssmlText)
                    .withTextType(TextType.Ssml)
                    .withVoiceId(voice)
                    .withOutputFormat(format);
            SynthesizeSpeechResult result = PollyInit.getPollyHandler().getPolly().synthesizeSpeech(request);
            return result.getAudioStream();
        } catch (RuntimeException exception) {
            logSynthesisError(exception, ssmlText);
            return null;
        }
    }

    /**
     * Plays the text as speech
     */
    public void playToDefaultOutput(InputStream speechStream) {
        try {
            AdvancedPlayer player = new AdvancedPlayer(speechStream, FactoryRegistry.systemRegistry().createAudioDevice());
            player.play();
        } catch (Exception exception) {
            Logging.logAndPrint(logger, LocaleHandler.getInstance().getErrorGeneral().replace("%error%", exception.getMessage()), Level.ERROR);
        }
    }

    /**
     * Logs errors during speech synthesis.
     */
    private void logSynthesisError(Exception e, String text) {
        Logging.logAndPrint(logger, LocaleHandler.getInstance().getErrorGeneral().replace("%error%", e.getMessage()), Level.ERROR);
        Logging.logAndPrint(logger, LocaleHandler.getInstance().getMessageNotParsable().replace("%message%", text), Level.ERROR);
    }
}
