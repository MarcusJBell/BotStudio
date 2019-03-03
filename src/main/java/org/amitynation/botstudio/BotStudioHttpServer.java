package org.amitynation.botstudio;

import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class BotStudioHttpServer extends NanoHTTPD {

    public static String code = null;
    private DebugSite debugSite;

    public BotStudioHttpServer() {
        super(5888);
        debugSite = new DebugSite();
    }

    public static void main(String[] args) throws IOException {
        new BotStudioHttpServer().start(3000, false);
    }

    public static String generateCode() {
        code = RandomStringUtils.randomAlphanumeric(5);
        return code;
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (code == null || StringUtils.isBlank(session.getQueryParameterString())) {
            return newFixedLengthResponse("");
        }
        if (code != null) {
            if (session.getQueryParameterString().equals(code)) {
                return debugSite.getDebugSite();
            }
        }

        return newFixedLengthResponse("");
//        if (needLogin(session.getUri())) {
//            try {
//                return newFixedLengthResponse(Response.Status.OK, MIME_HTML, new String(Files.readAllBytes(Paths.get("C:\\Users\\sinti\\Desktop\\login.html"))));
//            } catch (IOException e) {
//                e.printStackTrace();
//                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "");
//            }
//        }
//
//        return debugSite.getDebugSite();
    }

    private boolean needLogin(String uri) {
        return true;
    }
}
