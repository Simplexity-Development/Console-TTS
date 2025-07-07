package simplexity.console;

import simplexity.amazon.SpeechHandler;
import simplexity.commands.CommandManager;

import java.util.Scanner;

public class InputHandler {

    private final Scanner scanner = new Scanner(System.in);
    private final CommandManager commandManager;
    private final SpeechHandler speechHandler;

    public InputHandler(CommandManager commandManager, SpeechHandler speechHandler) {
        this.commandManager = commandManager;
        this.speechHandler = speechHandler;
    }

    @SuppressWarnings("InfiniteLoopStatement") //It exits in the command --exit
    public void runLoop() {
        while (true) {
            String input = scanner.nextLine();
            input = stripInvalidCharacters(input);
            if (input.isEmpty()) continue;
            if (!commandManager.runCommand(input)) {
                speechHandler.processSpeech(input);
            }
        }

    }

    private String stripInvalidCharacters(String input) {
        if (input == null) return null;
        return input.replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "")
                .replaceAll("\\p{C}", "");
    }

    public Scanner getScanner(){
        return scanner;
    }
}
