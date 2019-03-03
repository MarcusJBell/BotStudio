package org.amitynation.botstudio.screenplay.frontend;

import org.amitynation.botstudio.screenplay.Bot;
import org.amitynation.botstudio.screenplay.Screenplay;

/**
 * A front end for bots to use to send messages to various platforms.
 */
public abstract class BotFrontEnd {

    protected Screenplay screenplay;
    // Bot that this front end represents.
    protected Bot bot;
    // Used for async front ends. Should be set to false to make screenplay wait until message is sent to avoid ordering problems.
    protected boolean readyForNext = true;

    public BotFrontEnd(Screenplay screenplay, Bot bot) {
        this.screenplay = screenplay;
        this.bot = bot;
    }

    /**
     * Checks if this front end is ready for next message.
     *
     * @return Returns true if front end is ready for next message. False if not.
     */
    public boolean isReadyForNext() {
        return readyForNext;
    }

    public abstract void sendMessage(String message);

}
