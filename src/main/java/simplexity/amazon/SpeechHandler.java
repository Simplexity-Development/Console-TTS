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
import simplexity.Main;
import simplexity.config.config.ConfigHandler;
import simplexity.config.config.SpeechEffectRule;
import simplexity.config.locale.Message;
import simplexity.util.Logging;

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
        // Replace text and determine if SSML is needed
        String processedText = replaceText(text);
        boolean useSSML = !text.equals(processedText);

        //Synthesize speech
        InputStream speechStream;
        if (useSSML) {
            speechStream = synthesizeSSMLSpeech(processedText, voiceId);
        } else {
            speechStream = synthesizeSpeech(processedText, voiceId);
        }
        if (speechStream == null) {
            Logging.logAndPrint(logger, Message.GENERAL_ERROR.getMessage().replace("%error%", "Speech stream is null"), Level.ERROR);
            return;
        }

        //play it
        playSpeech(speechStream);
    }

    /**
     * Replaces text based on replacement mappings and updates the voice if a prefix is matched.
     */

    public String replaceText(String text) {
        for (SpeechEffectRule effectRule : ConfigHandler.getInstance().getEffectRules()) {
            text = effectRule.applyRule(text);
        }
        for (String key : ConfigHandler.getInstance().getVoicePrefixes().keySet()) {
            if (text.startsWith(key)) {
                text = text.replace(key, "");
                voiceId = ConfigHandler.getInstance().getVoicePrefixes().get(key);
            }
        }
        return text;
    }

    public InputStream synthesizeSSMLSpeech(String text, VoiceId voice) {
        String ssmlText = "<speak>" + text + "</speak>";
        try {
            SynthesizeSpeechRequest request = new SynthesizeSpeechRequest()
                    .withText(ssmlText)
                    .withTextType(TextType.Ssml)
                    .withVoiceId(voice)
                    .withOutputFormat(OutputFormat.Mp3);
            SynthesizeSpeechResult result = Main.getPollyHandler().getPolly().synthesizeSpeech(request);
            return result.getAudioStream();
        } catch (RuntimeException exception) {
            logSynthesisError(exception, ssmlText);
            return null;
        }
    }

    public InputStream synthesizeSpeech(String text, VoiceId voice) {
        try {
            SynthesizeSpeechRequest request = new SynthesizeSpeechRequest()
                    .withText(text)
                    .withVoiceId(voice)
                    .withOutputFormat(OutputFormat.Mp3);
            SynthesizeSpeechResult result = Main.getPollyHandler().getPolly().synthesizeSpeech(request);
            return result.getAudioStream();
        } catch (RuntimeException exception) {
            logSynthesisError(exception, text);
            return null;
        }
    }

    /**
     * Plays the text as speech
     */
    public void playSpeech(InputStream speechStream) {
        AdvancedPlayer player;
        try {
            player = new AdvancedPlayer(speechStream, FactoryRegistry.systemRegistry().createAudioDevice());
            player.play();
        } catch (Exception exception) {
            Logging.logAndPrint(logger, Message.GENERAL_ERROR.getMessage().replace("%error%", exception.getMessage()), Level.ERROR);
        }
    }

    /**
     * Logs errors during speech synthesis.
     */
    private void logSynthesisError(Exception e, String text) {
        Logging.logAndPrint(logger, Message.GENERAL_ERROR.getMessage().replace("%error%", e.getMessage()), Level.ERROR);
        Logging.logAndPrint(logger, Message.MESSAGE_NOT_PARSABLE.getMessage().replace("%message%", text), Level.ERROR);
    }
}
