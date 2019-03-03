package org.amitynation.botstudio.command;

import net.dv8tion.jda.core.Permission;
import org.amitynation.botstudio.BotStudio;

public class CommandIgnore extends DiscordCommand {

    public CommandIgnore() {
        this.commandName = "IgnoreChannel";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.help = "Sets channel to be ignored by WordFilter";
    }

    @Override
    protected void onCommand(DiscordCommandEvent event, String command, String[] args) {
        BotStudio.getInstance().getBotStudioConfig().chatFilterIgnoredChannels.add(event.getChannel().getIdLong());
        BotStudio.getInstance().getBotStudioConfig().save();
        reply(event, "WordFilter will now ignore this channel.");
    }

}
