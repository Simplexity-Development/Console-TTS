package simplexity.config.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class SpeechEffectRule {

    private static Logger logger = LoggerFactory.getLogger(SpeechEffectRule.class);

    public final String markdown;
    public final String openingTag;
    public final String closingTag;
    public final Pattern wrappingPattern;

    public SpeechEffectRule(String markdown, String openingTag, String closingTag){
        this.markdown = markdown;
        this.openingTag = openingTag;
        this.closingTag = closingTag;
        // I stole this from chat GPT I am not gonna lie
        this.wrappingPattern = Pattern.compile(
                "(?<!"+Pattern.quote(markdown)+")" +
                Pattern.quote(markdown) +
                "(.+?)" +
                Pattern.quote(markdown) +
                "(?!"+Pattern.quote(markdown)+")"
        );
    }

    public boolean matches(String input) {
        return wrappingPattern.matcher(input).find();
    }

    public String applySSMLRule(String input) {
        return wrappingPattern.matcher(input).replaceAll(openingTag + "$1" + closingTag);
    }

    public String clearMarkdown(String input){
        return wrappingPattern.matcher(input).replaceAll("$1");
    }
}
