package org.amitynation.botstudio.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import org.amitynation.botstudio.discordbot.DiscordBot;
import org.amitynation.botstudio.discordbot.DiscordBotManager;
import org.amitynation.botstudio.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandSay extends DiscordCommand {

    public CommandSay() {
        this.commandName = "say";
        this.help = "say <Channel> <Message> | will send the message to the provided channel";
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
    }

    @Override
    protected void onCommand(DiscordCommandEvent event, String command, String[] args) {
        if (args.length < 2) {
            reply(event, "Usage: say <Channel> <Message>");
            return;
        }
        List<TextChannel> potentionalChannels = event.getGuild().getTextChannels();
        TextChannel channel = null;
        for (TextChannel potentionalChannel : potentionalChannels) {
            if (potentionalChannel.getName().equalsIgnoreCase(args[0])) {
                channel = potentionalChannel;
                break;
            }
        }

        if (channel == null) {
            reply(event, "Channel " + args[0] + " not found.");
            return;
        }
        final String message = StringUtil.getFinalArg(args, 1);
        if (StringUtils.isBlank(message)) {
            reply(event, "Message must not be blank.");
            return;
        }

        // Handles sending messages as another bot.
        String botName = getBotName(command);
        if (botName != null) {
            DiscordBot discordBot = DiscordBotManager.getInstance().getDiscordBot(botName);
            if (discordBot == null) {
                reply(event, "Cannot find discord bot with name '" + botName + "'.");
                return;
            }
            discordBot.sendMessage(channel.getIdLong(), message);
            return;
        }
        sendMessage(event, channel.getIdLong(), message);
    }

    @Override
    public boolean matchCommand(String inputName) {
        if (StringUtils.startsWithIgnoreCase(inputName, this.commandName) && StringUtils.contains(inputName, ':')) {
            String botName = inputName.substring(inputName.indexOf(':') + 1);
            if (DiscordBotManager.getInstance().getDiscordBot(botName) != null) {
                return true;
            }
        }
        return super.matchCommand(inputName);
    }

    @Nullable
    private String getBotName(String inputName) {
        if (!StringUtils.contains(inputName, ':')) return null;
        return inputName.substring(inputName.indexOf(':') + 1);
    }
}
