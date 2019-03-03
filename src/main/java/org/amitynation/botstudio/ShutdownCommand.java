package org.amitynation.botstudio;

import org.amitynation.botstudio.discordbot.DiscordBotManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShutdownCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.isOp()) return true;
        DiscordBotManager.getInstance().onDisable();
        return true;
    }
}
