package de.bnder.taskmanager.commands;

import de.bnder.taskmanager.main.Command;
import de.bnder.taskmanager.main.Main;
import de.bnder.taskmanager.utils.Localizations;
import de.bnder.taskmanager.utils.MessageSender;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.awt.*;
import java.io.IOException;

public class Language implements Command {
    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) throws IOException {
        final Guild guild = event.getGuild();
        if (args.length == 0) {
            final String langCode = Localizations.getGuildLanguage(guild);
            final String embedTitle = Localizations.getString("language_message_title", langCode);
            MessageSender.send(embedTitle, Localizations.getString("language_message_invalid_params", langCode), event.getMessage(), Color.red);
        } else if (args.length == 1) {
            final String language = args[0].toLowerCase();
            if (language.equalsIgnoreCase("de") || language.equalsIgnoreCase("en") || language.equalsIgnoreCase("bg")
                    || language.equalsIgnoreCase("fr") || language.equalsIgnoreCase("ru") || language.equalsIgnoreCase("pl")) {
                if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    final Connection.Response res = Jsoup.connect(Main.requestURL + "/server/language/" + event.getGuild().getId()).method(org.jsoup.Connection.Method.POST).header("authorization", "TMB " + Main.authorizationToken).data("language", language).postDataCharset("UTF-8").header("user_id", event.getMember().getId()).timeout(de.bnder.taskmanager.utils.Connection.timeout).userAgent(Main.userAgent).ignoreContentType(true).ignoreHttpErrors(true).execute();
                    final String embedTitle = Localizations.getString("language_message_title", language);
                    if (res.statusCode() == 200) {
                        MessageSender.send(embedTitle, Localizations.getString("sprache_geändert", language), event.getMessage(), Color.green);
                    } else {
                        MessageSender.send(embedTitle, Localizations.getString("abfrage_unbekannter_fehler", language), event.getMessage(), Color.red);
                    }
                } else {
                    final String langCode = Localizations.getGuildLanguage(event.getGuild());
                    final String embedTitle = Localizations.getString("language_message_title", langCode);
                    MessageSender.send(embedTitle, Localizations.getString("muss_serverbesitzer_oder_adminrechte_haben", langCode), event.getMessage(), Color.red);
                }
            } else {
                final String langCode = Localizations.getGuildLanguage(event.getGuild());
                final String embedTitle = Localizations.getString("language_message_title", langCode);
                MessageSender.send(embedTitle, Localizations.getString("language_message_invalid_params", langCode), event.getMessage(), Color.red);
            }
        }
    }
}