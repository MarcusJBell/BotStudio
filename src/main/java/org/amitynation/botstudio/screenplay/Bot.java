package org.amitynation.botstudio.screenplay;

import org.amitynation.botstudio.screenplay.frontend.BotFrontEnd;
import org.amitynation.botstudio.screenplay.frontend.DiscordFrontEnd;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bot {

    // Bot's 'key' that is used to look it up.
    public final String name;
    // Name that's shown in Minecraft.
    public String displayName;
    // Bot's prefix used for Minecraft chat.
    public String prefix;

    private List<BotFrontEnd> frontEnds = new ArrayList<>();

    /**
     * @param name Bot's 'key' that is used to look it up.
     */
    public Bot(String name) {
        this.name = name;
        this.displayName = name;
        prefix = ChatColor.BLUE + displayName + ": " + ChatColor.RESET;
    }

    /**
     * Sends message via registered front ends.
     *
     * @param message Message to send.
     */
    public void sendMessage(String message) {
        for (BotFrontEnd frontEnd : frontEnds) {
            frontEnd.sendMessage(message);
        }
    }

    public void sendTyping() {
        for (BotFrontEnd frontEnd : frontEnds) {
            if (frontEnd instanceof DiscordFrontEnd) {
                ((DiscordFrontEnd) frontEnd).sendTyping();
            }
        }
    }

    /**
     * Adds a new front end to this bot.
     * @param botFrontEnd Front end to add.
     */
    public void addFrontEnd(BotFrontEnd botFrontEnd) {
        frontEnds.add(botFrontEnd);
    }

    /**
     * Gets list of all front ends.
     * @return Returns unmodifiable copy of front ends.
     */
    public List<BotFrontEnd> getFrontEnds() {
        return Collections.unmodifiableList(frontEnds);
    }

}
