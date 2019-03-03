package org.amitynation.botstudio.discordbot.listener;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.amitynation.botstudio.BotStudio;
import org.amitynation.botstudio.discordbot.DiscordBot;
import org.amitynation.wordfilter.api.WordFilterAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.regex.Pattern;

public class DiscordMessageFilterListener extends ListenerAdapter {

    private DiscordBot bot;

    public DiscordMessageFilterListener(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (BotStudio.getInstance().getWordFilterApi() == null) return;

        Bukkit.getServer().getScheduler().runTask(BotStudio.getInstance(), () -> handleMessage(event));
    }

    private void handleMessage(GuildMessageReceivedEvent event) {
        if (BotStudio.getInstance().getBotStudioConfig().chatFilterIgnoredChannels.contains(event.getChannel().getIdLong())) return;
        if (event.getAuthor().isBot()) return;
        if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE) || event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }
        WordFilterAPI api = BotStudio.getInstance().getWordFilterApi();

        String rawMessage = event.getMessage().getContentStripped();
        boolean shouldBeFiltered = false;
        try {
            shouldBeFiltered = api.checkFilter(rawMessage.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!shouldBeFiltered) {
            return;
        }

        try {
            event.getMessage().delete().queue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String log = createLog(event);
        bot.sendDebugMessage(log);

        String multipleWords = "a blocked word";
        if (api.foundWhole.size() + api.foundSimilar.size() > 1) {
            multipleWords = "blocked words";
        }

        String notification = "Your message was automatically deleted for using " + multipleWords + ".\n";
        String message = "  \"" + event.getMessage().getContentDisplay() + "\"\n";
//        String disclaimer = "\n\nBeep boop much like a human I am not perfect. If you feel this was a mistake please contact a staff member!";


        event.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(notification + message + getCustomMessage()).queue());
    }

    @NotNull
    private String getCustomMessage() {
        WordFilterAPI api = BotStudio.getInstance().getWordFilterApi();

        HashSet<String> allWords = new HashSet<>();
        allWords.addAll(api.foundSimilar);
        allWords.addAll(api.foundWhole);

        if (allWords.isEmpty() || allWords.size() > 1) {
            return BotStudio.getInstance().getBotStudioConfig().worldFitlerConfig.getRandomResponse();
        } else {
            String response = BotStudio.getInstance().getBotStudioConfig().worldFitlerConfig.getRandomWordBasedResponse(allWords.iterator().next());
            if (response == null) {
                response = BotStudio.getInstance().getBotStudioConfig().worldFitlerConfig.getRandomResponse();
            }
            return response;
        }
    }

    private String createLog(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentStripped();
        WordFilterAPI api = BotStudio.getInstance().getWordFilterApi();
        for (String it : api.foundWhole) {
            Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(it));
            message = pattern.matcher(message).replaceAll("__**" + it + "**__");
        }

        String multipleWords = "word";
        if (api.foundWhole.size() + api.foundSimilar.size() > 1) {
            multipleWords = "words";
        }

        StringBuilder messageBuilder = new StringBuilder();
        String channelName;
        if (event.getMessage().getChannelType() == ChannelType.PRIVATE) {
            channelName = "Amy-PM";
        } else {
            channelName = "#" + event.getMessage().getChannel().getName();
        }
        String blockedMessage = String.format("**%s#%s** used blocked %s in channel %s:\n \"%s\"",
                event.getAuthor().getName(), event.getAuthor().getDiscriminator(), multipleWords, channelName, message);

        messageBuilder.append(blockedMessage);

        if (!api.foundSimilar.isEmpty()) {
            messageBuilder.append("\n").append("Found similar: ").append("_").append(api.foundSimilar.toString()).append("_");
        }
        messageBuilder.append("\n").append("Debug ID: **").append(api.debugLogger.getLogID()).append("**");

        return messageBuilder.toString();
    }
}
