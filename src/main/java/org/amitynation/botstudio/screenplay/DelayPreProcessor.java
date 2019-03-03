package org.amitynation.botstudio.screenplay;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DelayPreProcessor implements ScreenplayPreProcessor {

    private AtomicBoolean isLocked = new AtomicBoolean(false);
    private AtomicLong lockUntil = new AtomicLong(-1L);

    public void lock() {
        isLocked.set(true);
    }

    public void unlock() {
        isLocked.set(false);
        this.lockUntil.set(-1L);
    }

    public void lockUntil(long lockUntil) {
        this.lockUntil.set(lockUntil);
    }

    @Override
    public boolean process(Screenplay screenplay) {
        if (isLocked.get()) return false;
        long lockUntil = this.lockUntil.get();
        return lockUntil == -1L || System.currentTimeMillis() >= lockUntil;
    }

}
