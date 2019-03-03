package org.amitynation.botstudio.screenplay.command;

import org.amitynation.botstudio.screenplay.Screenplay;
import org.amitynation.botstudio.util.IntUtils;
import org.apache.commons.lang.ArrayUtils;

public class CommandSetChannel extends ScreenplayCommand {

    CommandSetChannel() {
        super("SetChannel");
    }

    @Override
    public void onCommand(Screenplay screenplay, String command, String[] args) {
        if (ArrayUtils.isEmpty(args)) {
            screenplay.logError(missingArgs("<Discord Channel ID>"));
            return;
        }
        Long channelId = IntUtils.tryParseLong(args[0]);
        if (channelId == null) {
            screenplay.logError("Channel ID must be a number!");
            return;
        }
        screenplay.discordChannelId = channelId;
    }
}
