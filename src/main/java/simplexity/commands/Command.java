package simplexity.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Command {
    public String name;
    public String description;
    public final Logger logger;

    public Command(String name, String usage) {
        this.name = name;
        this.description = usage;
        this.logger = LoggerFactory.getLogger(this.getClass().getName());
    }

    public abstract void execute();

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
