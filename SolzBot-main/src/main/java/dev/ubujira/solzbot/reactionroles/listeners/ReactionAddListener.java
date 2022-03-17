package dev.ubujira.solzbot.reactionroles.listeners;

import dev.ubujira.solzbot.SolzBot;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionAddListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        SolzBot.getInstance().getReactionRoles().handleReactionAdd(event.getMember(), event.getReactionEmote(), event.getTextChannel());
    }

}
