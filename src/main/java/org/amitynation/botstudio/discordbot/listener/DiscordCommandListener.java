package org.amitynation.botstudio.discordbot.listener;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.amitynation.botstudio.command.DiscordCommandProcessor;

public class DiscordCommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        DiscordCommandProcessor.getInstance().issueCommand(event);
    }

}
