package org.amitynation.botstudio.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import org.amitynation.botstudio.discordbot.DiscordBot;
import org.amitynation.botstudio.discordbot.DiscordBotManager;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jetbrains.annotations.Nullable;

public abstract class DiscordCommand {

    protected String commandName = null;
    protected String help = "There is no help.";
    protected Permission[] userPermissions = new Permission[0];
    protected String[] aliases = new String[0];
    protected boolean guildOnly = false;

    /**
     * Bot that actually replies to this command. Be careful to send message with reply instead of sending messages through the MessageReceivedEvent
     */
    protected String delegateBotName = null;

    protected abstract void onCommand(DiscordCommandEvent event, String command, String[] args);

    public final void execute(MessageReceivedEvent messageReceivedEvent, String command, String[] args) {
        DiscordCommandEvent event = new DiscordCommandEvent(messageReceivedEvent);
        if (messageReceivedEvent.getAuthor().isBot()) {
            return;
        }
        if (event.getChannelType() == ChannelType.TEXT) {
            if (!hasPermission(event)) {
                reply(event, "You do not have permission to run this command.");
                return;
            }
        } else if (guildOnly) {
            reply(event, "Command can only be ran inside of a guild.");
            return;
        }

        try {
            onCommand(event, command, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasPermission(DiscordCommandEvent event) {
        Member member = event.getMember();
        for (Permission permission : userPermissions) {
            if (permission.isChannel()) {
                if (!member.hasPermission(event.getTextChannel(), permission)) {
                    return false;
                }
            } else {
                if (!event.getMember().hasPermission(permission)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean matchCommand(final String inputName) {
        if (inputName == null) return false;
        if (inputName.equalsIgnoreCase(this.commandName)) return true;
        for (String alias : aliases) {
            if (inputName.equalsIgnoreCase(alias)) return true;
        }

        return false;
    }

    public void reply(DiscordCommandEvent event, String message) {
        MessageAction action = replyQueue(event, message);
        if (action != null) {
            action.queue();
        }
    }

    public MessageAction replyQueue(DiscordCommandEvent event, String message) {
        if (delegateBotName == null) {
            return event.getChannel().sendMessage(message);
        }
        DiscordBot bot = getDelegateBot();
        if (bot == null) return null;

        long channelId = event.getChannel().getIdLong();
        TextChannel replyChannel = bot.getJda().getTextChannelById(channelId);
        if (replyChannel == null) {
            DiscordBotManager.getInstance().mainBot.sendDebugMessage("Tried to delegate command with name '" + commandName + "' with bot named '" + delegateBotName + "' but the bot doesn't have access to channel with id " + channelId + ".");
            return null;
        }
        return replyChannel.sendMessage(message);
    }


    public void sendMessage(DiscordCommandEvent event, long channelId, String message) {
        if (delegateBotName == null) {
            event.getJDA().getTextChannelById(channelId).sendMessage(message).queue();
            return;
        }
        DiscordBot bot = getDelegateBot();
        if (bot == null) return;

        bot.sendMessage(channelId, message);
    }

    public void privateMessage(DiscordCommandEvent event, long userId, String message) {
        User user;
        if (delegateBotName == null) {
            user = event.getJDA().getUserById(userId);
            user.openPrivateChannel().queue(
                    privateChannel -> privateChannel.sendMessage(message).queue(),
                    throwable -> onPrivateMessageError(event, throwable, user)
            );
            return;
        }
        DiscordBot bot = getDelegateBot();
        if (bot == null) return;

        user = bot.getJda().getUserById(userId);
        bot.getJda().getUserById(userId).openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessage(message).queue(),
                throwable -> onPrivateMessageError(event, throwable, user)
        );
    }

    private void onPrivateMessageError(DiscordCommandEvent event, Throwable throwable, User user) {
        reply(event, ExceptionUtils.getStackTrace(throwable));
    }

    public String missingArgs(String args) {
        return "Usage: " + commandName + " " + args;
    }

    public void setDelegate(String botName) {
        this.delegateBotName = botName;
    }

    @Nullable
    protected DiscordBot getDelegateBot() {
        if (delegateBotName == null) return null;
        DiscordBot bot = DiscordBotManager.getInstance().getDiscordBot(delegateBotName);
        if (bot == null) {
            DiscordBotManager.getInstance().mainBot.sendDebugMessage("Tried to delegate command with name '" + commandName + "' with bot named '" + delegateBotName + "' but no bot with that name is registered.");
            return null;
        }
        if (!bot.isReady()) {
            DiscordBotManager.getInstance().mainBot.sendDebugMessage("Tried to delegate command with name '" + commandName + "' with bot named '" + delegateBotName + "' but the bot isn't ready yet.");
            return null;
        }

        return bot;
    }

    public String getCommandName() {
        return commandName;
    }
}
