package org.amitynation.botstudio.screenplay.frontend;

import org.amitynation.botstudio.screenplay.Bot;
import org.amitynation.botstudio.screenplay.Screenplay;
import org.amitynation.botstudio.util.FormatUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class DebugFileFrontEnd extends BotFrontEnd {

    public File file;

    public DebugFileFrontEnd(File file, Screenplay screenplay, Bot bot) {
        super(screenplay, bot);
        this.file = file;
    }

    @Override
    public void sendMessage(String message) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
            String output = bot.prefix + message;
            printWriter.println(FormatUtil.unreplaceColor(output));
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
