package de.bnder.taskmanager.commands.group;

import de.bnder.taskmanager.main.Main;
import de.bnder.taskmanager.utils.Connection;
import de.bnder.taskmanager.utils.Localizations;
import de.bnder.taskmanager.utils.MessageSender;
import de.bnder.taskmanager.utils.PermissionSystem;
import de.bnder.taskmanager.utils.permissions.GroupPermission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jsoup.Jsoup;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoveGroupMember {

    public static void removeGroupMember(Member member, TextChannel textChannel, String[] args, List<Member> mentionedMembers) throws IOException {
        final String langCode = Localizations.getGuildLanguage(member.getGuild());
        final String embedTitle = Localizations.getString("group_title", langCode);
        if (PermissionSystem.hasPermission(member, GroupPermission.REMOVE_MEMBERS)) {
            if (args.length >= 3) {
                if (mentionedMembers != null && mentionedMembers.size() > 0) {
                    final String groupName = Connection.encodeString(args[1 + mentionedMembers.size()]);
                    for (Member mentionedMember : mentionedMembers) {
                        final org.jsoup.Connection.Response res = Jsoup.connect(Main.requestURL + "/group/remove-member/" + member.getGuild().getId()).method(org.jsoup.Connection.Method.PUT).header("authorization", "TMB " + Main.authorizationToken).header("user_id", mentionedMember.getId()).data("group_name", groupName).postDataCharset("UTF-8").timeout(Connection.timeout).userAgent(Main.userAgent).ignoreContentType(true).ignoreHttpErrors(true).execute();
                        final int statusCode = res.statusCode();
                        if (statusCode == 200) {
                            MessageSender.send(embedTitle, Localizations.getString("nutzer_aus_gruppe_entfernt", langCode, new ArrayList<String>() {
                                {
                                    add(member.getUser().getName());
                                    add(groupName);
                                }
                            }), textChannel, Color.green);
                        } else if (statusCode == 404) {
                            MessageSender.send(embedTitle, Localizations.getString("group_with_name_doesnt_exist", langCode, new ArrayList<String>() {
                                {
                                    add(groupName);
                                }
                            }), textChannel, Color.red);
                            return;
                        } else if (statusCode == 400) {
                            MessageSender.send(embedTitle, Localizations.getString("nutzer_ist_in_keiner_gruppe", langCode, new ArrayList<String>() {
                                {
                                    add(member.getUser().getAsTag());
                                }
                            }), textChannel, Color.red);
                        } else {
                            MessageSender.send(embedTitle, Localizations.getString("nutzer_aus_gruppe_entfernen_unbekannter_fehler", langCode, new ArrayList<String>() {
                                {
                                    add(String.valueOf(statusCode));
                                }
                            }), textChannel, Color.red);
                        }
                    }
                } else {
                    MessageSender.send(embedTitle, Localizations.getString("user_needs_to_be_mentioned", langCode), textChannel, Color.red);
                }
            } else {
                MessageSender.send(embedTitle, Localizations.getString("group_name_needs_to_be_given", langCode), textChannel, Color.red);
            }
        } else {
            MessageSender.send(embedTitle, Localizations.getString("need_to_be_serveradmin_or_habe_admin_permissions", langCode), textChannel, Color.red);
        }
    }

}