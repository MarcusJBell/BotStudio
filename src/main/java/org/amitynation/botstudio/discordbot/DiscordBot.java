package org.amitynation.botstudio.discordbot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.amitynation.botstudio.BotStudio;
import org.amitynation.botstudio.discordbot.listener.DiscordPmListener;

import javax.security.auth.login.LoginException;
import java.util.function.Consumer;

/**
 * General class that represents a wrapper for bots. Can be used in any context.
 */
public class DiscordBot {

    protected JDA jda;
    private boolean isReady = false;
    private final String botName;
    private String token;
    private final boolean unmanaged;

    public DiscordBot(String botName, String token) {
        this(botName, token, false);
    }

    public DiscordBot(String botName, String token, boolean unmanaged) {
        this.botName = botName.toLowerCase();
        this.token = token;
        this.unmanaged = unmanaged;
    }

    void startupBot() {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(token).setStatus(OnlineStatus.ONLINE).setGame(Game.playing("mc.amitynation.org"));
        try {
            jda = builder.build().awaitReady();
            jda.addEventListener(new DiscordPmListener());
            isReady = true;
        } catch (LoginException e) {
            BotStudio.getInstance().getLogger().warning("Bot with name '" + getName() + "' has invalid token!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void shutdownBot() {
        if (jda == null) return;
        if (jda.getStatus() == JDA.Status.SHUTTING_DOWN || jda.getStatus() == JDA.Status.SHUTDOWN) return;

        jda.shutdownNow();
        isReady = false;
    }

    public String getName() {
        return this.botName;
    }

    public String getToken() {
        return this.token;
    }

    public boolean isReady() {
        return isReady;
    }

    public boolean isUnmanaged() {
        return unmanaged;
    }

    public JDA getJda() {
        return jda;
    }

    public void sendMessage(long channelId, String message) {
        TextChannel channel = jda.getTextChannelById(channelId);
        channel.sendMessage(message).queue();
    }

    public void sendMessage(long channelId, String message, Consumer<? super Message> success) {
        TextChannel channel = jda.getTextChannelById(channelId);
        channel.sendMessage(message).queue(success);
    }

    public void sendPrivateMessage(long userId, String message) {
        jda.getUserById(userId).openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

    public void sendPrivateMessage(long userId, String message, Consumer<? super Message> success) {
        jda.getUserById(userId).openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue(success));
    }

    public TextChannel getDebugChannel() {
        return jda.getTextChannelById(BotStudio.getInstance().getBotStudioConfig().debugChannelId);
    }

    public void sendDebugMessage(String message) {
        getDebugChannel().sendMessage(message).queue();
    }
}
