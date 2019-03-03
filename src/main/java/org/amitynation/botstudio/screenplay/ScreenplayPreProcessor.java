package org.amitynation.botstudio.screenplay;

public interface ScreenplayPreProcessor {

    /**
     * Used to process before the screenplay attempts to run next line.
     *
     * @return Returns true if screenplay can continue. False if screenplay should 'return'.
     */
    boolean process(Screenplay screenplay);

}
