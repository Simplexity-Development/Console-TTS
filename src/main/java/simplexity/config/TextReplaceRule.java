package simplexity.config;

import java.util.regex.Pattern;

public class TextReplaceRule {

    private String replacementText;
    private Pattern textPattern;

    public TextReplaceRule(String textToFind, String replacementText) {
        this.replacementText = replacementText;
        textPattern = Pattern.compile("(?<=^|\\s)" + Pattern.quote(textToFind) + "(?=\\s|$)");
    }

    public String applyRule(String input){
        return textPattern.matcher(input).replaceAll(replacementText);
    }
}
