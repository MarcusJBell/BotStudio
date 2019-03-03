package org.amitynation.botstudio.command;

import net.dv8tion.jda.core.Permission;
import org.amitynation.botstudio.BotStudio;
import org.amitynation.botstudio.screenplay.Screenplay;

import java.io.File;

public class CommandDebugScreenplay extends CommandStartScreenplay {

    public CommandDebugScreenplay() {
        this.commandName = "DebugScreenplay";
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.help = this.commandName + " <Play Name>";
    }

    @Override
    protected void startPlay(DiscordCommandEvent event, File playFile) {
        if (BotStudio.getInstance().currentScreenplay != null) {
            reply(event, "Please stop the current screenplay before starting a new one.");
            return;
        }
        Screenplay screenplay = new Screenplay(playFile, true);
        BotStudio.getInstance().currentScreenplay = screenplay;
        screenplay.runScreenplay();
    }
}
