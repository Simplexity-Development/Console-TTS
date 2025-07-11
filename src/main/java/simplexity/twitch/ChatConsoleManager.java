package simplexity.twitch;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.events.ChannelChatMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.amazon.SpeechHandler;
import simplexity.config.ConfigHandler;
import simplexity.config.LocaleHandler;
import simplexity.console.Logging;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ChatConsoleManager {

    private final TwitchClient twitchClient;
    private final String username;
    private final SpeechHandler speechHandler;
    private static final Logger logger = LoggerFactory.getLogger(ChatConsoleManager.class);

    private final BlockingQueue<String> ttsQueue = new LinkedBlockingQueue<>();
    private final AtomicReference<String> currentInput = new AtomicReference<>("");

    public ChatConsoleManager(TwitchClient twitchClient, String username, SpeechHandler speechHandler) {
        this.twitchClient = twitchClient;
        this.username = username;
        this.speechHandler = speechHandler;
    }

    public void start(){
        chatListener();
        startConsoleInput();
        startTtsProcessor();
    }

    private void chatListener(){
        twitchClient.getEventManager().onEvent(ChannelChatMessageEvent.class, event -> {
            String user = event.getChatterUserName();
            String message = event.getMessage().getCleanedText();
            String formattedMessage = String.format(ConfigHandler.getInstance().getTwitchChatFormat().replace("%user%", user).replace("%message%", message));

            synchronized (System.out) {
                System.out.print("\r" + formattedMessage + "\n");
                System.out.print("> " + currentInput.get());
            }
        });
    }

    private void startConsoleInput(){
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("> ");
                String input = scanner.nextLine();
                currentInput.set("");

                if (input.trim().isEmpty()) continue;

                twitchClient.getChat().sendMessage(username, input);
                ttsQueue.offer(input);
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }

    private void startTtsProcessor() {
        Thread ttsThread = new Thread(() -> {
            while (true) {
                try {
                    String message = ttsQueue.take();
                    speechHandler.processSpeech(message);
                } catch (Exception e) {
                    Logging.logAndPrint(logger, LocaleHandler.getInstance().getErrorGeneral().replace("%error%", Arrays.toString(e.getStackTrace())), Level.ERROR);
                }
            }
        });
        ttsThread.setDaemon(true);
        ttsThread.start();
    }
}
