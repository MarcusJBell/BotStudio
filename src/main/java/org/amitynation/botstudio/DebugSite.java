package org.amitynation.botstudio;

import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

public class DebugSite {

    private static Map<String, String> colorReplacements = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private int index = 1;

    public DebugSite() {
        colorReplacements.put("&0", i());
        colorReplacements.put("&1", i());
        colorReplacements.put("&2", i());
        colorReplacements.put("&3", i());
        colorReplacements.put("&4", i());
        colorReplacements.put("&5", i());
        colorReplacements.put("&6", i());
        colorReplacements.put("&7", i());
        colorReplacements.put("&8", i());
        colorReplacements.put("&9", i());
        colorReplacements.put("&a", i());
        colorReplacements.put("&b", i());
        colorReplacements.put("&c", i());
        colorReplacements.put("&d", i());
        colorReplacements.put("&e", i());
        colorReplacements.put("&f", i());
    }

    private String i() {
        return "c-" + index++;
    }

    public NanoHTTPD.Response getDebugSite() {
        File file = new File(BotStudio.getInstance().getDataFolder(), "debug.html");
        try {
            String text = new String(Files.readAllBytes(file.toPath()));
            String type = Files.probeContentType(file.toPath());
            List<String> lines = Files.readAllLines(new File(BotStudio.getInstance().getDataFolder(), "debug.txt").toPath());
            StringBuilder msg = new StringBuilder();
            for (String line : lines) {
                msg.append(convertHtml(line + "&r"));
                msg.append("<br/>");
            }
            text = text.replace("$text", msg.toString());
            byte[] bytes = text.getBytes();
            return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, type, new ByteArrayInputStream(bytes), bytes.length);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse("");
    }

    public String convertHtml(String msg) {
        for (Map.Entry<String, String> replacements : colorReplacements.entrySet()) {
            msg = replaceColor(msg, replacements.getKey(), replacements.getValue());
        }
        msg = msg.replaceAll("&l", styleSpan("font-weight:900;"));
        msg = msg.replaceAll("&o", styleSpan("font-style:italic;"));
        msg = msg.replaceAll("&m", styleSpan("text-decoration:line-through;"));
        msg = msg.replaceAll("&n", styleSpan("text-decoration:underline;"));
        msg = msg.replaceAll("&k", "<span class='obfuscated'>");
        msg = msg.replaceAll("&r", "</span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span>");
        return msg;
    }

    private String replaceColor(String msg, String colorCode, String replacement) {
        return msg.replaceAll(colorCode, "</span>&r<span class=\"" + replacement + "\">");
    }

    private String styleSpan(String style) {
        return "<span style='" + style + "'>";
    }
}
