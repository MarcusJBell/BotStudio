package org.amitynation.botstudio.screenplay.frontend;

import org.amitynation.botstudio.discordbot.DiscordBot;
import org.amitynation.botstudio.screenplay.Bot;
import org.amitynation.botstudio.screenplay.Screenplay;
import org.bukkit.ChatColor;

public class DiscordFrontEnd extends BotFrontEnd {

    // DiscordBot that is used to represent the given bot.
    private DiscordBot discordBot;
    // Channel this front end sends to.
    private long channelId;

    public DiscordFrontEnd(Screenplay screenplay, long channelId, Bot bot, DiscordBot discordBot) {
        super(screenplay, bot);
        this.channelId = channelId;
        this.discordBot = discordBot;
    }

    @Override
    public void sendMessage(String message) {
        readyForNext = false;
        discordBot.sendMessage(channelId, ChatColor.stripColor(message), sentMessage -> {
            readyForNext = true;
        });
    }

    public void sendTyping() {
        discordBot.getJda().getTextChannelById(channelId).sendTyping().queue();
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }
}
