package de.bnder.taskmanager.listeners;
/*
 * Copyright (C) 2021 Jan Brinkmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import de.bnder.taskmanager.commands.Language;
import de.bnder.taskmanager.utils.Localizations;
import de.bnder.taskmanager.utils.Task;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class TaskLogReaction extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!event.getMember().getId().equalsIgnoreCase(event.getJDA().getSelfUser().getId())) {
            try {
                final Message message = event.retrieveMessage().complete();
                if (event.getReaction().getReactionEmote().getAsReactionCode().equals("⏭️") || event.getReaction().getReactionEmote().getAsReactionCode().equals("❌")) {
                    if (isRightMessage(message)) {
                        final String userName = message.getEmbeds().get(0).getFields().get(1).getValue();
                        if (userName.equals(event.getMember().getUser().getAsTag())) {
                            event.getReaction().removeReaction(event.retrieveUser().complete()).queue();
                            final String taskID = event.retrieveMessage().complete().getEmbeds().get(0).getFields().get(3).getValue();
                            final Task task = new Task(taskID, event.getGuild());
                            if (event.getReaction().getReactionEmote().getAsReactionCode().equals("⏭️")) {
                                task.proceed(event.getMember());
                            } else if (event.getReaction().getReactionEmote().getAsReactionCode().equals("↩️")) {
                                task.undo(event.getMember());
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    boolean isRightMessage(Message message) {
        if (message.getAuthor().getId().equals(message.getJDA().getSelfUser().getId())) {
            if (message.getEmbeds().size() == 1) {
                final MessageEmbed embed = message.getEmbeds().get(0);
                final List<MessageEmbed.Field> fields = embed.getFields();
                for (final String langCode : Language.validLangCodes) {
                    if (fields.get(0).getName().equals(Localizations.getString("task_info_field_task", langCode))) {
                        if (fields.get(1).getName().equals(Localizations.getString("task_info_field_type_group", langCode))) {
                            return true;
                        } else if (fields.get(1).getName().equals(Localizations.getString("task_info_field_type_user", langCode))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
