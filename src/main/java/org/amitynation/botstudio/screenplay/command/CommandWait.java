package org.amitynation.botstudio.screenplay.command;

import org.amitynation.botstudio.screenplay.Screenplay;
import org.amitynation.botstudio.util.IntUtils;

public class CommandWait extends ScreenplayCommand {

    CommandWait() {
        super("Wait", new String[]{"Sleep", "Delay"});
    }

    @Override
    public void onCommand(Screenplay screenplay, String command, String[] args) {
        if (screenplay.isDebug()) return;

        if (args.length < 1) {
            screenplay.logError(missingArgs("<Delay>"));
            return;
        }

        Long wait = IntUtils.tryParseLong(args[0]);
        if (wait == null) {
            screenplay.logError("Delay must be a number.");
            return;
        }
        screenplay.setDelay(wait);
    }

}
