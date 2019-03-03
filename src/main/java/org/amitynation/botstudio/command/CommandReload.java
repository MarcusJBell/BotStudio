package org.amitynation.botstudio.command;

import net.dv8tion.jda.core.Permission;
import org.amitynation.botstudio.BotStudio;
import org.amitynation.botstudio.discordbot.DiscordBotManager;

public class CommandReload extends DiscordCommand {

    public CommandReload() {
        this.commandName = "Reload";
        this.help = "Reloads config file.";
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }

    @Override
    protected void onCommand(DiscordCommandEvent event, String command, String[] args) {
        BotStudio.getInstance().getBotStudioConfig().reload();
        BotStudio.getInstance().getBotStudioConfig().reloadBots();
        DiscordBotManager.getInstance().mainBot.sendMessage(event.getChannel().getIdLong(), "Reload complete.");
//        replyQueue(event, "Reload complete.").queue(message -> {
//        });
    }
}
