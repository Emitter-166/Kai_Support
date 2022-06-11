package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileReader;

public class closeAndLogs extends ListenerAdapter {


    public void onMessageReceived(MessageReceivedEvent e){
        User author = e.getAuthor();
        if(author.isBot()) return;
        try{
            if(author.equals(e.getGuild().getSelfMember().getUser())) return;
        }catch(IllegalStateException exception){}

        String args[] = e.getMessage().getContentRaw().split(" ");
        if(args[0].equalsIgnoreCase("?close")){
            try{
                if(e.getTextChannel().getTopic().equalsIgnoreCase("Kai Support Modmail")){
                    e.getChannel().sendMessage("**Closing modmail....**").queue();
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }

                    //logs

                    try{
                        File file = new File(String.format("%s.txt", e.getChannel().getName()));
                        e.getGuild().getTextChannelById("980322609822576640").sendFile(file).queue();

                    }catch (IllegalArgumentException exception){}



                    StringBuilder reply = new StringBuilder();
                    for(int i = 1; i < args.length ; i++){
                        reply.append(args[i] + " ");
                    }
                    if(args.length != 1) {
                        e.getGuild().retrieveMemberById(e.getTextChannel().getName().split("-")[e.getTextChannel().getName().split(" ").length + 1])
                                .complete().getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(String.format("**Support ticket closed with the reason: %s .**", reply))).queue();
                    }else{
                        e.getGuild().retrieveMemberById(e.getTextChannel().getName().split("-")[e.getTextChannel().getName().split(" ").length + 1])
                                .complete().getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("**Support ticket closed**")).queue();
                    }

//                    e.getChannel().delete().queue();

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
