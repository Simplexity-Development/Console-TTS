package simplexity.twitch;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import simplexity.Main;
import simplexity.amazon.SpeechHandler;
import simplexity.config.ChatFormat;
import simplexity.config.ConfigHandler;
import simplexity.console.ColorTags;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ChatConsoleManager {

    private final TwitchClient twitchClient;
    private final String username;
    private final SpeechHandler speechHandler;

    private LineReader reader;

    public ChatConsoleManager(TwitchClient twitchClient, String username, SpeechHandler speechHandler) {
        this.twitchClient = twitchClient;
        this.username = username;
        this.speechHandler = speechHandler;
    }

    public void start() throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(new DefaultParser())
                .build();

        chatListener();
        startInputLoop();
    }

    private void startInputLoop() {
        Thread inputThread = new Thread(() -> {
            while (true) {
                try {
                    String line = reader.readLine("> ");
                    if (line.trim().isEmpty()) continue;
                    if (Main.getCommandManager().runCommand(line)) {
                        continue;
                    }

                    twitchClient.getChat().sendMessage(username, line);
                    speechHandler.processSpeech(line);
                } catch (UserInterruptException | EndOfFileException e) {
                    break;
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }

    private void chatListener() {
        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            String user = event.getUser().getName();
            String message = event.getMessage();
            String formattedMessage = getFormat(user, message, event.getPermissions());
            String colorParsed = ColorTags.parse(formattedMessage);
            reader.printAbove(colorParsed);
            //todo have options for tts when things happen
        });
    }

    private String getFormat(String user, String message, Set<CommandPermission> permissions) {
        int weight = 0;
        ChatFormat formatToUse = null;
        HashSet<ChatFormat> formats = ConfigHandler.getInstance().getChatFormats();
        for (CommandPermission permission : permissions) {
            for (ChatFormat format : formats) {
                if (!format.getPermission().equals(permission)) continue;
                if (weight > format.getWeight()) continue;
                weight = format.getWeight();
                formatToUse = format;
            }
        }
        if (formatToUse == null) return String.format("%s ➜ %s", user, message);
        return formatToUse.applyFormat(user, message);
    }

}
