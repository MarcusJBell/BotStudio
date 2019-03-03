package org.amitynation.botstudio.command;

import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DiscordCommandEvent {

    private final MessageReceivedEvent event;

    public DiscordCommandEvent(MessageReceivedEvent event) {
        this.event = event;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public SelfUser getSelfUser() {
        return event.getJDA().getSelfUser();
    }

    public Member getSelfMember() {
        return event.getGuild() == null ? null : event.getGuild().getSelfMember();
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public User getAuthor() {
        return event.getAuthor();
    }

    public Member getMember() {
        return event.getMember();
    }

    public boolean isWebhookMessage() {
        return event.isWebhookMessage();
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public String getMessageId() {
        return event.getMessageId();
    }

    public long getMessageIdLong() {
        return event.getMessageIdLong();
    }

    public boolean isFromType(ChannelType type) {
        return event.isFromType(type);
    }

    public ChannelType getChannelType() {
        return event.getChannelType();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public TextChannel getTextChannel() {
        return event.getTextChannel();
    }

    public PrivateChannel getPrivateChannel() {
        return event.getPrivateChannel();
    }

    public Group getGroup() {
        return event.getGroup();
    }

    public JDA getJDA() {
        return event.getJDA();
    }

    public long getResponseNumber() {
        return event.getResponseNumber();
    }

}
