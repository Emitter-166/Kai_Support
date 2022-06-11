package org.example;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class closeAndLogs extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e){
        User author = e.getAuthor();
        if(author.isBot()) return;
        if(author.equals(e.getGuild().getSelfMember().getUser())) return;

        if(e.getMessage().getContentRaw().equalsIgnoreCase("?close")){

            try{
                if(e.getTextChannel().getTopic().equalsIgnoreCase("Kai Support Modmail")){
                    e.getChannel().sendMessage("**Closing modmail....**").queue();
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }

                    e.getChannel().delete().queue();

                    //here should be the logging system
                }else{
                    e.getChannel().sendMessage("**this is not a modmail thread**").queue();
                }
            }catch(NullPointerException exception){
                //this will also catch if it's a modmail thread incase there is no topics
                e.getChannel().sendMessage("**this is not a modmail thread**").queue();
            }
        }
    }
}
