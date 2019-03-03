package org.amitynation.botstudio.screenplay.command;

import org.amitynation.botstudio.screenplay.Bot;
import org.amitynation.botstudio.screenplay.Screenplay;
import org.amitynation.botstudio.screenplay.frontend.MinecraftFrontEnd;
import org.amitynation.botstudio.util.StringUtil;

public class CommandAddMinecraftBot extends ScreenplayCommand {

    CommandAddMinecraftBot() {
        super("AddMinecraftBot");
    }

    @Override
    public void onCommand(Screenplay screenplay, String command, String[] args) {
        if (args.length < 1) {
            screenplay.logError(missingArgs("<Bot Name> <Optional prefix>"));
            return;
        }

        String botName = args[0].toLowerCase();

        Bot bot;
        if (!screenplay.getBots().containsKey(botName)) {
            bot = new Bot(botName);
            screenplay.addBot(bot);
        } else {
            bot = screenplay.getBots().get(botName);
        }

        if (args.length > 1) {
            bot.prefix = StringUtil.getFinalArg(args, 1);
        }
        if (screenplay.isDebug()) return; // Return so we don't register the front end!
        bot.addFrontEnd(new MinecraftFrontEnd(screenplay, bot));
    }

}
