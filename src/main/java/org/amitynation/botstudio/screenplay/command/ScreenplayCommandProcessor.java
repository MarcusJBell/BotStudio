package org.amitynation.botstudio.screenplay.command;

import org.amitynation.botstudio.screenplay.Screenplay;
import org.amitynation.botstudio.screenplay.command.special.SpecialCommand;
import org.amitynation.botstudio.screenplay.command.special.SpecialCommandBotTalk;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class used to process each line of a Screenplay file and pass the processed line to the proper command.
 */
public class ScreenplayCommandProcessor {

    private List<ScreenplayCommand> registeredCommands = new ArrayList<>();
    private List<SpecialCommand> specialCommands = new ArrayList<>();
    public static ScreenplayCommandProcessor instance;

    public ScreenplayCommandProcessor() {
        instance = this;

        // Built-in commands.
        registerCommand(
                new CommandAddMinecraftBot(),
                new CommandAddDiscordBot(),
                new CommandSetChannel(),
                new CommandWait(),
                new CommandSetDefaultDelay()
        );

        registerSpecialCommand(
                new SpecialCommandBotTalk()
        );
    }

    /**
     * Tries to issue command or send a bot message.
     *
     * @param screenplay   Screenplay instance.
     * @param inputCommand Original command to be processed.
     */
    public void issueCommand(Screenplay screenplay, String inputCommand) {
        String[] split = inputCommand.split("\\s+");
        String commandName = ArrayUtils.isNotEmpty(split) ? split[0] : inputCommand;
        for (ScreenplayCommand command : registeredCommands) {
            if (command.matchCommand(commandName)) {
                issueCommand(screenplay, inputCommand, command);
                return;
            }
        }

        for (SpecialCommand specialCommand : specialCommands) {
            if (specialCommand.process(screenplay, inputCommand)) {
                return;
            }
        }

        screenplay.logError("No command found for: " + inputCommand);
    }

    /**
     * Processes given command and sends it to the given [ScreenplayCommand].
     *
     * @param screenplay   Screenplay instance.
     * @param inputCommand Original command to process.
     * @param command      DiscordCommand to be issued.
     */
    private void issueCommand(Screenplay screenplay, String inputCommand, ScreenplayCommand command) {
        final String[] split = inputCommand.split("\\s+");
        if (ArrayUtils.isEmpty(split) || split.length == 1) {
            command.onCommand(screenplay, inputCommand, new String[]{});
            return;
        }
        final String commandName = split[0].toLowerCase();
        final String[] args = Arrays.copyOfRange(split, 1, split.length);
        command.onCommand(screenplay, commandName, args);
    }

    /**
     * Registers multiple commands for screenplays.
     *
     * @param commands ScreenplayCommands to add.
     */
    public void registerCommand(ScreenplayCommand... commands) {
        registeredCommands.addAll(Arrays.asList(commands));
    }

    /**
     * Registers multiple special commands for screenplays.
     *
     * @param commands SpecialCommand to add.
     */
    public void registerSpecialCommand(SpecialCommand... commands) {
        specialCommands.addAll(Arrays.asList(commands));
    }

}
