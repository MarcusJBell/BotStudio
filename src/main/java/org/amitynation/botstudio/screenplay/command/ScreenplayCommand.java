package org.amitynation.botstudio.screenplay.command;

import org.amitynation.botstudio.screenplay.Screenplay;

public abstract class ScreenplayCommand {

    public final String commandName;
    public final String[] aliases;

    ScreenplayCommand(String commandName, String[] aliases) {
        this.commandName = commandName;
        this.aliases = aliases;
    }

    ScreenplayCommand(String commandName) {
        this.commandName = commandName;
        this.aliases = new String[]{};
    }

    /**
     * Issues command to the used ScreenplayCommand instance.
     *
     * @param screenplay Screenplay instance.
     * @param command    DiscordCommand used. Will show used alias if an alias was used.
     * @param args       Array of arguments that was split from the original command.
     */
    public abstract void onCommand(Screenplay screenplay, String command, String[] args);

    /**
     * Checks if given command uses this command processor.
     * @param commandName DiscordCommand to process.
     * @return Returns true if the command uses this processor and false if not.
     */
    public boolean matchCommand(final String commandName) {
        if (commandName == null) return false;
        if (commandName.equalsIgnoreCase(this.commandName)) return true;
        for (String alias : aliases) {
            if (commandName.equalsIgnoreCase(alias)) return true;
        }
        return false;
    }

    /**
     * Combines args after [start] into a single string.
     * @param args Input args.
     * @param start Start index of args to join to string.
     * @return Returns all the args after [start] joined as a single string.
     * @deprecated
     * @see org.amitynation.botstudio.util.StringUtil#getFinalArg(String[], int)
     */
    @Deprecated
    protected String getFinalArg(String[] args, int start) {
        final StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start) {
                builder.append(" ");
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

    /**
     * Helper method to show arg error in this format: "Usage: command <Arg1> <Arg2>
     * @param args A string of the missing args. Example: <Arg1> <Arg2>
     * @return Returns an error message to be sent.
     */
    protected String missingArgs(String args) {
        return "Usage: " + commandName + " " + args;
    }

}
