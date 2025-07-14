package simplexity.twitch;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import simplexity.config.ChatFormat;
import simplexity.config.ConfigHandler;
import simplexity.console.ColorTags;
import simplexity.console.ConsoleInit;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TwitchListeners {

    public static void chatListener() {
        TwitchInit.getTwitchClient().getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            String user = event.getUser().getName();
            String message = event.getMessage();
            String formattedMessage =  getFormat(user, message, event.getPermissions());
            String colorParsed = ColorTags.parse(formattedMessage);
            ConsoleInit.getInputManager().getReader().printAbove(colorParsed);
            //todo have options for tts when things happen
        });
        TwitchInit.getTwitchClient().getEventManager().onEvent(SubscriptionEvent.class, event -> {
            String user = event.getUser().getName();
            Optional<String> messageOptional = event.getMessage();
            if (messageOptional.isEmpty()) return;
            String message = messageOptional.get();
            Set<CommandPermission> permissions = event.getMessageEvent().getClientPermissions();
            String formattedMessage = getFormat(user, message, permissions);
            String colorParsed = ColorTags.parse(formattedMessage);
            ConsoleInit.getInputManager().getReader().printAbove(colorParsed);
        });
    }

    private static String getFormat(String user, String message, Set<CommandPermission> permissions) {
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
