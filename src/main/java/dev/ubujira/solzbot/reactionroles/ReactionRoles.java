package dev.ubujira.solzbot.reactionroles;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vdurmont.emoji.EmojiManager;
import dev.ubujira.solzbot.SolzBot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ReactionRoles {
    private final File reactionRolesFile = new File("reactionRoles.json");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<Long, ReactionChannel> reactionRolesMap = new HashMap<>();

    public ReactionRoles() {
        logger.info("Initializing reaction roles!");
        if (reactionRolesFile.exists()) {
            try {
                JsonElement jsonElement = JsonParser.parseReader(new FileReader(reactionRolesFile));

                if (jsonElement.isJsonObject()) {
                    JsonObject reactionRoles = jsonElement.getAsJsonObject();

                    if (reactionRoles.has("reactionRoles")) {
                        reactionRolesMap.putAll(SolzBot.getGson().fromJson(reactionRoles.get("reactionRoles"), HashMap.class));
                    }

                    logger.info("Loaded reaction roles!");
                } else {
                    logger.error("Expected json object, got json element!");
                    SolzBot.getInstance().getJda().shutdownNow();
                    System.exit(1);
                }
            } catch (FileNotFoundException e) {
                logger.error("Somehow couldn't find the file after checking if it exists??", e);
            }
        }
    }

    public void addReactionRole(TextChannel textChannel, Emoji emote, Role role) {
        if (!reactionRolesMap.containsKey(textChannel.getIdLong())) {
            reactionRolesMap.put(textChannel.getIdLong(), new ReactionChannel(textChannel.getIdLong(), 0L));
        }

        if (emote.isCustom()) {
            ReactionChannel.CompactEmoji compactEmoji = new ReactionChannel.CompactEmoji(false, "", emote.getIdLong());
            Long roleId = role.getIdLong();

            reactionRolesMap.get(textChannel.getIdLong()).addReactionRole(compactEmoji, roleId);
        } else {
            ReactionChannel.CompactEmoji compactEmoji = new ReactionChannel.CompactEmoji(true, EmojiManager.getByUnicode(emote.getName()).getUnicode(), 0L);
            Long roleId = role.getIdLong();

            reactionRolesMap.get(textChannel.getIdLong()).addReactionRole(compactEmoji, roleId);
        }

        save();
    }

    public void removeReactionRole(TextChannel textChannel, Emoji emote, Role role) {
        if (!reactionRolesMap.containsKey(textChannel.getIdLong())) {
            return;
        }

        if (emote.isCustom()) {
            ReactionChannel.CompactEmoji compactEmoji = new ReactionChannel.CompactEmoji(false, "", emote.getIdLong());
            Long roleId = role.getIdLong();

            reactionRolesMap.get(textChannel.getIdLong()).removeReactionRole(compactEmoji);
        } else {
            ReactionChannel.CompactEmoji compactEmoji = new ReactionChannel.CompactEmoji(true, EmojiManager.getByUnicode(emote.getName()).getUnicode(), 0L);
            Long roleId = role.getIdLong();

            reactionRolesMap.get(textChannel.getIdLong()).removeReactionRole(compactEmoji);
        }

        save();
    }

    private void save() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("reactionRoles", SolzBot.getGson().toJsonTree(reactionRolesMap));
        try {
            SolzBot.getGson().toJson(jsonObject, new FileWriter(reactionRolesFile));
            logger.debug("Saved reaction role file!");
        } catch (IOException e) {
            logger.error("Failed to save reaction roles file!", e);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class ReactionChannel {
        private final long channelId;
        private long messageId;

        private HashMap<CompactEmoji, Long> reactionRolesMap;

        public ReactionChannel(long channelId, long messageId) {
            this.channelId = channelId;
            this.messageId = messageId;
        }

        public void addReactionRole(CompactEmoji compactEmoji, Long roleId) {
            reactionRolesMap.put(compactEmoji, roleId);
            updateMessage();
        }

        public void removeReactionRole(CompactEmoji compactEmoji) {
            reactionRolesMap.remove(compactEmoji);
            updateMessage();
        }

        private void updateMessage() {
            TextChannel textChannel = SolzBot.getInstance().getJda().getTextChannelById(channelId);
            if (textChannel != null) {
                Message message = textChannel.getHistory().getMessageById(messageId);

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Reaction Roles")
                        .setColor(Color.MAGENTA)
                        .setTimestamp(Instant.now());

                StringBuilder stringBuilder = new StringBuilder();

                for (Map.Entry<CompactEmoji, Long> entry : reactionRolesMap.entrySet()) {
                    String emoji = entry.getKey().isUnicode() ?
                            EmojiManager.getByUnicode(entry.getKey().getUnicodeEmoji()).getUnicode() :
                            SolzBot.getInstance().getJda().getEmoteById(entry.getKey().getEmoteId()) != null ? SolzBot.getInstance().getJda().getEmoteById(entry.getKey().getEmoteId()).getAsMention() : "";

                    stringBuilder.append(emoji).append(" - ").append(SolzBot.getInstance().getJda().getRoleById(entry.getValue()).getAsMention()).append("\n");
                }

                embedBuilder.setDescription(stringBuilder.toString());

                if (message == null) {
                    textChannel.sendMessageEmbeds(embedBuilder.build()).queue(message1 -> {
                        messageId = message1.getIdLong();
                    });
                } else {
                    message.editMessageEmbeds(embedBuilder.build()).queue();
                }
            }
        }

        @AllArgsConstructor
        @Getter
        public static class CompactEmoji {
            private final boolean isUnicode;
            private final String unicodeEmoji;
            private final long emoteId;
        }
    }


}