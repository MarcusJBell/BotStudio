package org.amitynation.botstudio.screenplay.command.special;

import org.amitynation.botstudio.screenplay.Screenplay;

/**
 * Command that doesn't take the standard !command <args> format.
 */
public abstract class SpecialCommand {

    /**
     * Tries to process the given command.
     *
     * @param command Command to process.
     * @return Returns true if command was processed. Returns false if this special command doesn't process the given command.
     */
    public abstract boolean process(Screenplay screenplay, String command);

}
