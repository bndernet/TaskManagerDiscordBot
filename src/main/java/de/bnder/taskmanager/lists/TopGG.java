package de.bnder.taskmanager.lists;

import de.bnder.taskmanager.main.Main;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class TopGG {

    private static final String baseURL = "https://top.gg/api";
    private static final String apiKey = Main.dotenv.get("TOP.GG_API_KEY") != null ? Main.dotenv.get("TOP.GG_API_KEY") : System.getenv("TOP.GG_API_KEY");

    public static void sendServerCount(int serverCount) throws IOException {
        Jsoup.connect(baseURL + "/bots/602460094940184587/stats")
                .header("Authorization", apiKey)
                .data("server_count", String.valueOf(serverCount))
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .execute();
    }

}
