package org.amitynation.botstudio.screenplay.command;

import org.amitynation.botstudio.discordbot.DiscordBot;
import org.amitynation.botstudio.discordbot.DiscordBotManager;
import org.amitynation.botstudio.screenplay.Bot;
import org.amitynation.botstudio.screenplay.Screenplay;
import org.amitynation.botstudio.screenplay.frontend.DiscordFrontEnd;

public class CommandAddDiscordBot extends ScreenplayCommand {

    CommandAddDiscordBot() {
        super("AddDiscordBot");
    }

    @Override
    public void onCommand(Screenplay screenplay, String command, String[] args) {
        if (args.length < 2) {
            screenplay.logError(missingArgs("<Bot Name> <Bot Token>"));
            return;
        }
        if (screenplay.discordChannelId == -1L) {
            screenplay.logError("Please add a discord channel with: setchannel <Channel ID>");
            return;
        }

        String botName = args[0].toLowerCase();
        String botToken = args[1];

        Bot bot;
        if (!screenplay.getBots().containsKey(botName)) {
            bot = new Bot(botName);
            screenplay.addBot(bot);
        } else {
            bot = screenplay.getBots().get(botName);
        }

        if (screenplay.isDebug()) return; // Return so we don't register the front end!
        DiscordBot discordBot;
        if (!DiscordBotManager.getInstance().getDiscordBots().containsKey(botName)) {
            discordBot = new DiscordBot(botName, botToken);
            DiscordBotManager.getInstance().registerDiscordBot(discordBot);
        } else {
            discordBot = DiscordBotManager.getInstance().getDiscordBots().get(botName);
        }
        bot.addFrontEnd(new DiscordFrontEnd(screenplay, screenplay.discordChannelId, bot, discordBot));
    }

}
