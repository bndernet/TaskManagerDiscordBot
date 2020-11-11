package de.bnder.taskmanager.utils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import de.bnder.taskmanager.main.Main;
import net.dv8tion.jda.api.entities.Guild;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Localizations {

    private static JsonObject toParse = null;

    public static String getString(String path, String languageCode) {
        if (toParse == null) {
            toParse = Json.parse(localizationsJSONSource()).asObject();
        }
        if (toParse.get(path) != null) {
            return toParse.get(path).asObject().getString(languageCode, "null");
        }
        return path;
    }

    public static String getString(String path, String languageCode, ArrayList<String> args) {
        if (toParse == null) {
            toParse = Json.parse(localizationsJSONSource()).asObject();
        }
        if (toParse.get(path) != null) {
            String toReturn = toParse.get(path).asObject().getString(languageCode, "null");
            int argCount = 0;
            while (toReturn.contains("$")) {
                if (args.size() >= argCount + 1) {
                    toReturn = toReturn.replaceFirst("\\$", args.get(argCount));
                    argCount++;
                } else {
                    toReturn = toReturn.replaceFirst("\\$", " ");
                }
            }
            return toReturn;
        }
        return path;
    }

    public static String getGuildLanguage(Guild guild) {
        try {
            final org.jsoup.Connection.Response res = Jsoup.connect(Main.requestURL + "/server/language/" + guild.getId()).method(org.jsoup.Connection.Method.GET).header("authorization", "TMB " + Main.authorizationToken).header("user_id", "---").timeout(Connection.timeout).userAgent(Main.userAgent).ignoreContentType(true).ignoreHttpErrors(true).execute();
            if (res.statusCode() == 200) {
                return Json.parse(res.parse().body().text()).asObject().getString("language", "en");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "en";
    }

    private static String localizationsJSONSource() {
        try {
            final BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream("teammanagerbotLocalizations.json")));
            final StringBuilder sb = new StringBuilder();
            String line = buf.readLine();
            while (line != null) {
                sb.append(line);
                line = buf.readLine();
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}