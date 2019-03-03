package org.amitynation.botstudio.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.amitynation.botstudio.BotStudio;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiscordCommandProcessor {

    private static DiscordCommandProcessor instance;
    private List<DiscordCommand> registeredCommands = new ArrayList<>();

    public DiscordCommandProcessor() {
        registerCommands(
                new CommandPm(),
                new CommandSay(),
                new CommandStartScreenplay(),
                new CommandDebugScreenplay(),
                new CommandIgnore(),
                new CommandUnignore(),
                new CommandStop(),
                new CommandReload()
        );
    }

    public static DiscordCommandProcessor getInstance() {
        if (instance == null) {
            instance = new DiscordCommandProcessor();
        }

        return instance;
    }

    @Nullable
    private static String getUsedPrefix(@NotNull String message) {
        for (String prefix : BotStudio.getInstance().getBotStudioConfig().prefixes) {
            if (message.toLowerCase().startsWith(prefix.toLowerCase())) {
                return prefix.toLowerCase();
            }
        }
        return null;
    }

    public List<DiscordCommand> getRegisteredCommands() {
        return Collections.unmodifiableList(registeredCommands);
    }

    @Nullable
    public DiscordCommand getCommandByName(String name) {
        if (name == null) return null;
        for (DiscordCommand command : registeredCommands) {
            if (command.commandName.equalsIgnoreCase(name)) return command;
            for (String alias : command.aliases) {
                if (alias.equalsIgnoreCase(name)) return command;
            }
        }
        return null;
    }

    public void issueCommand(MessageReceivedEvent event) {
        String inputCommand = event.getMessage().getContentRaw();
        String prefix = getUsedPrefix(inputCommand);
        if (prefix == null) return;
        String[] split = inputCommand.substring(prefix.length()).split("\\s+");
        if (ArrayUtils.isNotEmpty(split)) {
            for (DiscordCommand command : registeredCommands) {
                if (command.matchCommand(split[0])) {
                    final String[] args = split.length == 1 ? new String[0] : Arrays.copyOfRange(split, 1, split.length);
                    final String commandName = split[0].toLowerCase();
                    command.execute(event, commandName, args);
                }
            }
        }
    }

    public void registerCommand(DiscordCommand command) {
        registeredCommands.add(command);
    }

    public void registerCommands(DiscordCommand... commands) {
        for (DiscordCommand command : commands) {
            registerCommand(command);
        }
    }

    public void unregisterCommand(DiscordCommand command) {
        registeredCommands.remove(command);
    }

}
