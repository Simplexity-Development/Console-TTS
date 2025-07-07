package simplexity.config.rules;

import java.util.regex.Pattern;

public class TextReplaceRule {

    private final String replacementText;
    private final Pattern textPattern;

    public TextReplaceRule(String textToFind, String replacementText) {
        this.replacementText = replacementText;
        textPattern = Pattern.compile("(?<=^|\\s)" + Pattern.quote(textToFind) + "(?=\\s|$)");
    }

    public String applyRule(String input){
        return textPattern.matcher(input).replaceAll(replacementText);
    }
}
