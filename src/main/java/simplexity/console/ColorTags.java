package simplexity.console;

import java.awt.Color;

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

    public static ConsoleColor getClosestConsoleColor(Color userColor) {
        ConsoleColor closest = ConsoleColor.WHITE;
        double minDistance = Double.MAX_VALUE;

        for (ConsoleColor consoleColor : ConsoleColor.values()) {
            if (!consoleColor.getTag().startsWith("<") || consoleColor.getTag().endsWith("-bg>")) continue;

            Color color = mapTagToRGB(consoleColor.getTag());
            if (color == null) continue;
            double distance = colorDistance(userColor, color);
            if (distance < minDistance) {
                minDistance = distance;
                closest = consoleColor;
            }
        }
        return closest;
    }

    private static double colorDistance(Color color1, Color color2) {
        int rDiff = color1.getRed() - color2.getRed();
        int gDiff = color2.getGreen() - color2.getGreen();
        int bDiff = color1.getBlue() - color2.getBlue();
        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }

    private static Color mapTagToRGB(String tag) {
        return switch (tag) {
            case "<red>" -> new Color(128, 0, 0);
            case "<br-red>" -> new Color(255, 0, 0);
            case "<green>" -> new Color(0, 128, 0);
            case "<br-green>" -> new Color(0, 255, 0);
            case "<blue>" -> new Color(0, 0, 128);
            case "<br-blue>" -> new Color(0, 0, 255);
            case "<purple>" -> new Color(128, 0, 128);
            case "<br-purple>" -> new Color(255, 0, 255);
            case "<cyan>" -> new Color(0, 128, 128);
            case "<br-cyan>" -> new Color(0, 255, 255);
            case "<yellow>" -> new Color(128, 128, 0);
            case "<br-yellow>" -> new Color(255, 255, 0);
            case "<white>" -> new Color(192, 192, 192);
            case "<br-white>" -> new Color(255, 255, 255);
            case "<black>" -> new Color(0, 0, 0);
            case "<br-black>" -> new Color(128, 128, 128);
            default -> null;
        };
    }

}
