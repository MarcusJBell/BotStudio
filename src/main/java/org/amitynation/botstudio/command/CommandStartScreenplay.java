package org.amitynation.botstudio.command;

import net.dv8tion.jda.core.Permission;
import org.amitynation.botstudio.BotStudio;
import org.amitynation.botstudio.screenplay.Screenplay;
import org.amitynation.botstudio.util.StringUtil;
import org.apache.commons.lang.ArrayUtils;

import java.io.File;

public class CommandStartScreenplay extends DiscordCommand {

    public CommandStartScreenplay() {
        this.commandName = "StartScreenplay";
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.help = this.commandName + " <Play Name>";
    }

    @Override
    protected void onCommand(DiscordCommandEvent event, String command, String[] args) {
        if (ArrayUtils.isEmpty(args)) {
            reply(event, missingArgs("<Play Name>"));
            return;
        }
        String fileName = StringUtil.getFinalArg(args, 0);
        File file = new File(BotStudio.getInstance().playFolder, fileName);
        if (!file.exists()) {
            reply(event, "Cannot find file with name '" + fileName + "'");
        }
        startPlay(event, file);
    }

    protected void startPlay(DiscordCommandEvent event, File playFile) {
        if (BotStudio.getInstance().currentScreenplay != null) {
            reply(event, "Please stop the current screenplay before starting a new one.");
            return;
        }
        Screenplay screenplay = new Screenplay(playFile);
        BotStudio.getInstance().currentScreenplay = screenplay;
        screenplay.runScreenplay();
    }
}
