package org.amitynation.botstudio.screenplay.command.special;

import org.amitynation.botstudio.BotStudio;
import org.amitynation.botstudio.screenplay.Bot;
import org.amitynation.botstudio.screenplay.DelayPreProcessor;
import org.amitynation.botstudio.screenplay.Screenplay;
import org.amitynation.botstudio.screenplay.util.DelayCalculator;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class SpecialCommandBotTalk extends SpecialCommand {

    @Override
    public boolean process(Screenplay screenplay, String inputCommand) {
        String command = inputCommand.replaceFirst(": ", ":");
        if (!command.contains(":")) {
            return false;
        }
        String botName = command.substring(0, command.indexOf(':'));
        if (!screenplay.getBots().containsKey(botName)) {
            return false;
        }
        String message = command.substring(command.indexOf(':') + 1);
        Bot bot = screenplay.getBots().get(botName);
        long runAt = System.currentTimeMillis() + screenplay.getMessageDelay();
        if (screenplay.isDebug() || runAt - System.currentTimeMillis() < 0) {
            bot.sendMessage(message);
            return true;
        }
        DelayPreProcessor delayPreProcessor = new DelayPreProcessor();
        delayPreProcessor.lockUntil(runAt);
        screenplay.preProcessors.add(delayPreProcessor);

        long timeToType = DelayCalculator.calculateTypingTime(screenplay, message);
        final BukkitTask task;
        if (timeToType == -1L) {
            task = Bukkit.getScheduler().runTaskTimer(BotStudio.getInstance(), bot::sendTyping, 0L, 7000 / 50);
        } else {
            task = Bukkit.getScheduler().runTaskTimer(BotStudio.getInstance(), bot::sendTyping, Math.max(0, (screenplay.getMessageDelay() - timeToType) / 50), 7000 / 50);
        }

        Bukkit.getScheduler().runTaskLater(BotStudio.getInstance(), () -> {
            screenplay.preProcessors.remove(delayPreProcessor);
            if (screenplay.isCancelled()) {
                return;
            }
            bot.sendMessage(message);
            task.cancel();
        }, (runAt - System.currentTimeMillis()) / 50);

        return true;
    }
}
