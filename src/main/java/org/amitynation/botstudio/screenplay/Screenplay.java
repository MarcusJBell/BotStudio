package org.amitynation.botstudio.screenplay;

import net.dv8tion.jda.core.entities.TextChannel;
import org.amitynation.botstudio.BotStudio;
import org.amitynation.botstudio.BotStudioHttpServer;
import org.amitynation.botstudio.discordbot.DiscordBotManager;
import org.amitynation.botstudio.screenplay.command.ScreenplayCommandProcessor;
import org.amitynation.botstudio.screenplay.frontend.BotFrontEnd;
import org.amitynation.botstudio.screenplay.frontend.DebugFileFrontEnd;
import org.amitynation.botstudio.screenplay.frontend.DiscordFrontEnd;
import org.amitynation.botstudio.util.FileUtil;
import org.amitynation.botstudio.util.FormatUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;

public class Screenplay {

    private final Map<String, Bot> bots = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final File playFile;
    public long discordChannelId = -1L;
    private final List<String> lines = new ArrayList<>();

    private int currentLine;
    private long delay = 0L;
    private final boolean isDebug;
    private boolean isCancelled = false;
    private BukkitTask task;
    public List<ScreenplayPreProcessor> preProcessors = new ArrayList<>();
    private File debugFile = new File(BotStudio.getInstance().getDataFolder(), "debug.txt");
    private File errorFile = new File(BotStudio.getInstance().getDataFolder(), "error.txt");
    private List<ScreenplayError> errors = new ArrayList<>();
    private long defaultDelay = 1000L;

    public Screenplay(File playFile, boolean isDebug) {
        this.playFile = playFile;
        FileUtil.createOrClearFile(debugFile, errorFile);
        loadLines();
        this.isDebug = isDebug;
    }

    public Screenplay(File playFile) {
        this(playFile, false);
    }

    /**
     * Runs the screenplay from the start. Registers a task that will run through the entire screnplay. Should only be called once per instance.
     */
    public void runScreenplay() {
        task = Bukkit.getServer().getScheduler().runTaskTimer(BotStudio.getInstance(), this::tick, 0L, 0L);
    }

    /**
     * Method that update's the bot and runs the commands. Should be called from a bukkit task timer.
     */
    private void tick() {
        for (Bot bot : bots.values()) {
            for (BotFrontEnd frontEnd : bot.getFrontEnds()) {
                if (!frontEnd.isReadyForNext()) {
                    return;
                }
            }
        }

        if (isCancelled()) {
            try {
                onFinish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            task.cancel();
            return;
        }

        for (ScreenplayPreProcessor preProcessor : preProcessors) {
            if (!preProcessor.process(this)) {
                return;
            }
        }

        if (hasStoppedOrCancelled()) {
            // Try catch to avoid never ending loop of errors.
            try {
                onFinish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            task.cancel();
            return;
        }

        nextLine();
    }

    /**
     * Stops the screenplay and removes all tasks.
     */
    public void stop() {
        isCancelled = true;
        BotStudio.getInstance().currentScreenplay = null;
    }

    /**
     * Processes the next line of the screenplay and sends the line to the command processor.
     */
    private void nextLine() {
        if (isFinished()) return;
        String line = FormatUtil.replaceColor(lines.get(currentLine)); //TODO: Find way to not strip everything since some commands might use & symbol!
        ScreenplayCommandProcessor.instance.issueCommand(this, line);
        currentLine++;
    }

    /**
     * Checks if the screenplay is finished.
     *
     * @return Returns true if screenplay is finished. False if not.
     */
    private Boolean isFinished() {
        return currentLine > lines.size() - 1 || currentLine == -1;
    }

    private boolean hasStoppedOrCancelled() {
        return isFinished() || isCancelled;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public Boolean isDebug() {
        return isDebug;
    }

    /**
     * Unregisters used bots for the screenplay. Should always be called when the screenplay is finished.
     */
    private void onFinish() {
        for (Map.Entry<String, Bot> set : bots.entrySet()) {
            Bot bot = set.getValue();
            for (BotFrontEnd frontEnd : bot.getFrontEnds()) {
                if (frontEnd instanceof DiscordFrontEnd) {
                    DiscordBotManager.getInstance().unregisterDiscordBot(((DiscordFrontEnd) frontEnd).getDiscordBot());
                }
            }
        }

        TextChannel debugChannel = DiscordBotManager.getInstance().mainBot.getDebugChannel();
        debugChannel.sendMessage("Debug: http://mc.amitynation.org:5888/debug.html?" + BotStudioHttpServer.generateCode()).queue(message -> {
            if (!errors.isEmpty()) {
                writeErrorsToFile();
                debugChannel.sendFile(errorFile).queue();
            }
        });
        BotStudio.getInstance().currentScreenplay = null;
    }

    private void writeErrorsToFile() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(errorFile, true));
            for (ScreenplayError error : errors) {
                printWriter.println("[" + error.line + "] " + lines.get(error.line));
                printWriter.println("    - " + error.error);
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the bot map.
     *
     * @return Returns an unmodifiable map of the bots. <Bot Key><Bot>
     */
    public Map<String, Bot> getBots() {
        return Collections.unmodifiableMap(bots);
    }

    /**
     * Adds a bot to the screenplay.
     *
     * @param bot Bot to add.
     */
    public void addBot(Bot bot) {
        bots.put(bot.name, bot);
        bot.addFrontEnd(new DebugFileFrontEnd(debugFile, this, bot));
    }

    /**
     * Sets the default delay before sending next message.
     */
    public void setDefaultDelay(long delay) {
        this.defaultDelay = delay;
    }

    public long getDelay() {
        return this.delay;
    }

    /**
     * Makes the bot wait before sending next message.
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getMessageDelay() {
        long delay;
        if (this.delay > 0) {
            delay = this.delay;
        } else {
            delay = this.defaultDelay;
        }
        return delay;
    }

    /**
     * Method to load all the lines of the screenplay file. All lines are loaded into ArrayList called 'lines'.
     */
    private void loadLines() {
        BufferedReader reader = null;
        try {
            FileReader fileReader = new FileReader(playFile);
            reader = new BufferedReader(fileReader);

            String next;
            while ((next = reader.readLine()) != null) {
                if (StringUtils.isEmpty(next) || next.startsWith("##")) continue;
                lines.add(next);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a message to someplace (discord or file) for debugging. Currently prints to console.
     *
     * @param message Message to send.
     */
    public void logError(String message) {
        errors.add(new ScreenplayError(currentLine, message));
    }

    private class ScreenplayError {
        int line;
        String error;

        public ScreenplayError(int line, String error) {
            this.line = line;
            this.error = error;
        }
    }
}
