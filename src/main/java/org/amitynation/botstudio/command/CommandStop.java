package org.amitynation.botstudio.command;

import net.dv8tion.jda.core.Permission;
import org.amitynation.botstudio.BotStudio;

public class CommandStop extends DiscordCommand {

    public CommandStop() {
        this.commandName = "Stop";
        this.aliases = new String[]{"StopScreenplay"};
        this.guildOnly = true;
        this.help = "Stops current screenplay.";
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }

    @Override
    protected void onCommand(DiscordCommandEvent event, String command, String[] args) {
        if (BotStudio.getInstance().currentScreenplay == null) {
            reply(event, "No screenplay is playing.");
            return;
        }
        reply(event, "Stopping current screenplay.");
        BotStudio.getInstance().currentScreenplay.stop();
    }
}
