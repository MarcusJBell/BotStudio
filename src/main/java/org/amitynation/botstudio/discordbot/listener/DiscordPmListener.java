package org.amitynation.botstudio.discordbot.listener;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.amitynation.botstudio.BotStudio;

public class DiscordPmListener extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String user = event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator();
        String message = event.getMessage().getContentRaw();
        TextChannel logChannel = event.getJDA().getTextChannelById(BotStudio.getInstance().getBotStudioConfig().debugChannelId);
        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
            logChannel.sendMessage(attachment.getProxyUrl()).queue();
        }
        logChannel.sendMessage(user + ": " + message).queue();
    }

}
