package simplexity.util;

public enum ConsoleColor {
    // Reset
    RESET("0", "<reset>"), // Text Reset
    BOLD("1", "<bold>"),
    NOT_BOLD("22","</bold>"),
    ITALIC("3", "<i>"),
    NOT_ITALIC("23","</i>"),
    UNDERLINE("4", "<u>"),
    NOT_UNDERLINED("24", "</u>"),
    STRIKETHROUGH("9", "<s>"),
    NOT_STRIKETHROUGH("29", "</s>"),
    // Regular Colors
    DEFAULT_COLOR("39", "</color>"),
    BLACK("30", "<black>"),   // BLACK
    RED("31", "<red>"),     // RED
    GREEN("32", "<green>"),   // GREEN
    YELLOW("33", "<yellow>"),  // YELLOW
    BLUE("34", "<blue>"),    // BLUE
    PURPLE("35", "<purple>"),  // PURPLE
    CYAN("36", "<cyan>"),    // CYAN
    WHITE("37", "<white>"),   // WHITE

    // Background
    DEFAULT_BACKGROUND("48", "</color-bg>"),
    BLACK_BACKGROUND("40", "<black-bg>"),  // BLACK
    RED_BACKGROUND("41", "<red-bg>"),    // RED
    GREEN_BACKGROUND("42", "<green-bg>"),  // GREEN
    YELLOW_BACKGROUND("43", "<yellow-bg>"), // YELLOW
    BLUE_BACKGROUND("44", "<blue-bg>"),   // BLUE
    PURPLE_BACKGROUND("45", "<purple-bg>"), // PURPLE
    CYAN_BACKGROUND("46", "<cyan-bg>"),   // CYAN
    WHITE_BACKGROUND("47", "<white-bg>"),  // WHITE

    // High Intensity
    BLACK_BRIGHT("90", "<br-black>"),  // BLACK
    RED_BRIGHT("91", "<br-red>"),    // RED
    GREEN_BRIGHT("92", "<br-green>"),  // GREEN
    YELLOW_BRIGHT("93", "<br-yellow>"), // YELLOW
    BLUE_BRIGHT("94", "<br-blue>"),   // BLUE
    PURPLE_BRIGHT("95", "<br-purple>"), // PURPLE
    CYAN_BRIGHT("96", "<br-cyan>"),   // CYAN
    WHITE_BRIGHT("97", "<br-white>"),  // WHITE

    // High Intensity backgrounds
    BLACK_BACKGROUND_BRIGHT("100", "<br-black-bg>"),// BLACK
    RED_BACKGROUND_BRIGHT("101", "<br-red-bg>"),// RED
    GREEN_BACKGROUND_BRIGHT("102", "<br-green-bg>"),// GREEN
    YELLOW_BACKGROUND_BRIGHT("103", "<br-yellow-bg>"),// YELLOW
    BLUE_BACKGROUND_BRIGHT("104", "<br-blue-bg>"),// BLUE
    PURPLE_BACKGROUND_BRIGHT("105", "<br-purple-bg>"), // PURPLE
    CYAN_BACKGROUND_BRIGHT("106", "<br-cyan-bg>"),  // CYAN
    WHITE_BACKGROUND_BRIGHT("107", "<br-white-bg>");

    // WHITE

    private final String ansiCode;
    private final String tag;

    ConsoleColor(String ansiCode, String tag) {
        this.ansiCode = ansiCode;
        this.tag = tag;
    }

    public String getAnsiCode() {
        return ansiCode;
    }

    public String getTag() {
        return tag;
    }


    public static ConsoleColor fromTag(String tag) {
        for (ConsoleColor color : values()) {
            if (color.getTag().equalsIgnoreCase(tag)) {
                return color;
            }
        }
        return null; // Tag not found
    }
}
