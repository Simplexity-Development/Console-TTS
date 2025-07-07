package simplexity.util;

public class ColorTags {

    public static String parse(String input) {
        String parsedText = input;
        for (ConsoleColor color : ConsoleColor.values()) {
            if (input.contains(color.getTag())) {
                String ansiCode = "\033[" + color.getAnsiCode() + "m";
                parsedText = parsedText.replace(color.getTag(), ansiCode);
            }
        }
        parsedText = parsedText + "\033[0m";
        return parsedText;
    }
}
