package org.amitynation.botstudio.screenplay.command;

import org.amitynation.botstudio.screenplay.Screenplay;
import org.amitynation.botstudio.util.IntUtils;
import org.apache.commons.lang.ArrayUtils;

public class CommandSetDefaultDelay extends ScreenplayCommand {

    public CommandSetDefaultDelay() {
        super("SetDefaultDelay", new String[]{"DefaultDelay"});
    }

    @Override
    public void onCommand(Screenplay screenplay, String command, String[] args) {
        if (ArrayUtils.isEmpty(args)) {
            screenplay.logError(missingArgs("<Delay>"));
            return;
        }
        Long delay = IntUtils.tryParseLong(args[0]);
        if (delay == null) {
            screenplay.logError("Delay must be a number.");
            return;
        }
        screenplay.setDefaultDelay(delay);
    }

}
