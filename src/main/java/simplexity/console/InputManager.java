package simplexity.console;

import com.github.twitch4j.TwitchClient;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import simplexity.Main;
import simplexity.config.ConfigHandler;
import simplexity.config.rules.SpeechEffectRule;
import simplexity.config.rules.VoicePrefixRule;

import java.io.IOException;

public class InputManager {

    private final TwitchClient twitchClient;
    private final String username;

    private LineReader reader;

    public InputManager(TwitchClient twitchClient, String username) {
        this.twitchClient = twitchClient;
        this.username = username;
    }

    public void start() throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(new DefaultParser())
                .build();
        startInputLoop();
    }

    private void startInputLoop() {
        Thread inputThread = new Thread(() -> {
            while (true) {
                try {
                    String line = reader.readLine("> ");
                    if (line.trim().isEmpty()) continue;
                    if (ConsoleInit.getCommandManager().runCommand(line)) {
                        continue;
                    }

                    if (twitchClient != null && ConfigHandler.getInstance().shouldSendMessages()) {
                        String messageToSend = line;
                        if (ConfigHandler.getInstance().shouldCleanMessages()) {
                            messageToSend = cleanMessage(messageToSend);
                        }
                        twitchClient.getChat().sendMessage(username, messageToSend);
                    }
                    ConfigHandler.getInstance().getSpeechHandler().processSpeech(line);
                } catch (UserInterruptException | EndOfFileException e) {
                    break;
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }

    private String cleanMessage(String input) {
        for (VoicePrefixRule prefixRule : ConfigHandler.getInstance().getVoicePrefixRules()) {
            if (!prefixRule.matches(input)) continue;
            input = prefixRule.applyRule(input);
            break;
        }
        for (SpeechEffectRule effectRule : ConfigHandler.getInstance().getEffectRules()) {
            if (!effectRule.matches(input)) continue;
            input = effectRule.clearMarkdown(input);
        }
        return input;
    }

    public LineReader getReader() {
        return reader;
    }
}
