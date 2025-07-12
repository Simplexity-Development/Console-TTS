package simplexity.config;

import com.github.twitch4j.common.enums.CommandPermission;

public class ChatFormat {

    private final String format;
    private final int weight;
    private final CommandPermission permission;

    public ChatFormat(String format, int weight, CommandPermission permission) {
        this.format = format;
        this.weight = weight;
        this.permission = permission;
    }

    public String applyFormat(String username, String message){
        return format.replace("%user%", username).replace("%message%", message);
    }

    public int getWeight() {
        return weight;
    }

    public CommandPermission getPermission(){
        return permission;
    }
}
