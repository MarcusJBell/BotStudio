package org.amitynation.botstudio.command;

import net.dv8tion.jda.core.Permission;
import org.amitynation.botstudio.BotStudio;

public class CommandUnignore extends DiscordCommand {

    public CommandUnignore() {
        this.commandName = "UnignoreChannel";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.help = "Sets channel to not be ignored by WordFilter";
    }

    @Override
    protected void onCommand(DiscordCommandEvent event, String command, String[] args) {
        BotStudio.getInstance().getBotStudioConfig().chatFilterIgnoredChannels.remove(event.getChannel().getIdLong());
        BotStudio.getInstance().getBotStudioConfig().save();
        reply(event, "WordFilter will no longer ignore this channel.");
    }

}
