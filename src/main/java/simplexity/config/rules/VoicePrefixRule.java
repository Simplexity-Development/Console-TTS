package simplexity.config.rules;

import com.amazonaws.services.polly.model.VoiceId;

import java.util.regex.Pattern;

public class VoicePrefixRule {
    private final VoiceId voiceId;
    private final Pattern prefixPattern;

    public VoicePrefixRule(String prefix, VoiceId voiceId) {
        this.voiceId = voiceId;
        this.prefixPattern = Pattern.compile("(?i)^" + Pattern.quote(prefix) + "[:\\s-]?");
    }

    public VoiceId getVoiceId() {
        return voiceId;
    }

    public boolean matches(String input) {
        return prefixPattern.matcher(input).find();
    }

    public String applyRule(String input) {
        return prefixPattern.matcher(input).replaceAll("");
    }

}
