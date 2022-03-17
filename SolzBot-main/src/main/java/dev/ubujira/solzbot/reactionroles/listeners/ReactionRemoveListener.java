package dev.ubujira.solzbot.reactionroles.listeners;

import dev.ubujira.solzbot.SolzBot;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionRemoveListener extends ListenerAdapter {

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        SolzBot.getInstance().getReactionRoles().handleReactionRemove(event.getMember(), event.getReactionEmote(), event.getTextChannel());
    }

}
