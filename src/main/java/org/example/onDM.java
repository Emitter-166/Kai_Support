package org.example;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class onDM extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getChannel().getType() != ChannelType.PRIVATE) return;
        if(e.getMessage().getAuthor().isBot()) return;

        e.getMessage().reply("qwjfdijwqif").queue();


    }
}
