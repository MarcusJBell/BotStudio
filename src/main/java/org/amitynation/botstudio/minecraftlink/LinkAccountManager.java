package org.amitynation.botstudio.minecraftlink;

public class LinkAccountManager {

    private static LinkAccountManager linkAccountManager;

    private LinkAccountManager() {

    }

    public static LinkAccountManager getInstance() {
        if (linkAccountManager == null) {
            linkAccountManager = new LinkAccountManager();
        }
        return linkAccountManager;
    }


    public void save() {

    }

    public void load() {

    }
}
