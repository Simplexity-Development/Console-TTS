package simplexity.config;

import java.util.regex.Pattern;

public class SpeechEffectRule {

    public final String markdown;
    public final String openingTag;
    public final String closingTag;
    public final Pattern wrappingPattern;

    public SpeechEffectRule(String markdown, String openingTag, String closingTag){
        this.markdown = markdown;
        this.openingTag = openingTag;
        this.closingTag = closingTag;
        this.wrappingPattern = Pattern.compile(
                "(?<=^|\\s)" + Pattern.quote(markdown) + "(.+?)" + Pattern.quote(markdown) + "(?=\\s|$)"
        );
    }

    public String applyRule(String input) {
        return wrappingPattern.matcher(input).replaceAll(openingTag + "$1" + closingTag);
    }
}
