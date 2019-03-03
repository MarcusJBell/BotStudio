package org.amitynation.botstudio.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import org.amitynation.botstudio.discordbot.DiscordBot;
import org.amitynation.botstudio.discordbot.DiscordBotManager;
import org.amitynation.botstudio.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandPm extends DiscordCommand {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^.+?#[0-9]+");

    public CommandPm() {
        this.commandName = "pm";
        this.help = "pm <@User> <Message> | will send the user the message in a private message";
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
    }

    @Override
    protected void onCommand(DiscordCommandEvent event, String command, String[] args) {
        if (args.length < 2) {
            reply(event, "Usage: pm <@User> <Message>");
            return;
        }
        Matcher matcher = USERNAME_PATTERN.matcher(event.getMessage().getContentRaw().substring(event.getMessage().getContentRaw().indexOf(' ') + 1));
        if (!matcher.find()) {
            reply(event, "Error parsing username. Please use the format Username#0000 and try again.");
            return;
        }
        String usernameRaw = matcher.group();
        String username = StringUtil.substringBeforeLast(usernameRaw, "#");
        String discriminator = StringUtil.substringAfterLast(usernameRaw, "#");
        List<Member> potentialUsers = event.getGuild().getMembers();
        Member member = null;
        for (Member potentialUser : potentialUsers) {
            if (potentialUser.getUser().getName().equalsIgnoreCase(username) && potentialUser.getUser().getDiscriminator().equals(discriminator)) {
                member = potentialUser;
                break;
            }
        }
        if (member == null) {
            reply(event, "User " + usernameRaw + " not found.");
            return;
        }
        final String message = StringUtils.substringAfter(event.getMessage().getContentRaw(), usernameRaw).trim();
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
            discordBot.sendPrivateMessage(member.getUser().getIdLong(), message);
            return;
        }

        privateMessage(event, member.getUser().getIdLong(), message);
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