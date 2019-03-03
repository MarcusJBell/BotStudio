package org.amitynation.botstudio.screenplay.frontend;

import org.amitynation.botstudio.screenplay.Bot;
import org.amitynation.botstudio.screenplay.Screenplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MinecraftFrontEnd extends BotFrontEnd {

    public MinecraftFrontEnd(Screenplay screenplay, Bot bot) {
        super(screenplay, bot);
    }

    @Override
    public void sendMessage(String message) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(bot.prefix + message);
        }
    }

}
