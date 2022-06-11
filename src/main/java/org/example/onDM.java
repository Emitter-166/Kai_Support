package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class onDM extends ListenerAdapter {
Guild guild;
JDA jda = null;
    onDM(JDA jda){
       this.jda = jda; 
    }



    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        guild = jda.getGuildById("818373020816637952");
        if(e.getChannel().getType() != ChannelType.PRIVATE) return;
        if(e.getMessage().getAuthor().isBot()) return;
        //checking if the user has pervious thread
        User author = e.getMessage().getAuthor();
        Category modMailCategory = guild.getCategoryById("961736325076250704");
        StringBuilder authorName = new StringBuilder();
        Arrays.stream(author.getName().split(" ")).forEach(args -> authorName.append(args + "-"));

        String ChannelName = authorName + "Id-" + e.getMessage().getAuthor().getId();

        boolean hasThread = false;
        try{
             hasThread = modMailCategory.getTextChannels().contains(guild.getTextChannelsByName(ChannelName, true).get(0));
        }catch (IndexOutOfBoundsException exception) {}
        
        if(!hasThread){
            System.out.println(ChannelName);
            modMailCategory.createTextChannel(ChannelName)
                    .clearPermissionOverrides()
                    .queue();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            // this is the message we will send when the ticket is open
            EmbedBuilder DMbuilder = new EmbedBuilder();

            Calendar calendar = new GregorianCalendar();

            DMbuilder.setTitle("Ticket open!");
            DMbuilder.setColor(Color.CYAN);
            DMbuilder.setDescription("Ticket open! we will get back to you asap!");
            DMbuilder.setThumbnail(e.getAuthor().getEffectiveAvatarUrl());
            DMbuilder.setFooter("Message ID: " + e.getMessage().getId() + " • " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
            e.getChannel().sendMessageEmbeds(DMbuilder.build()).queue();

            Database.adduser(author.getId());
            Object timeJoined;
            try{
                System.out.println(guild.getName());
                timeJoined = guild.getMemberById(author.getId()).getTimeJoined();

            }catch(NullPointerException exception){
                timeJoined = "owner of this server";
            }

            //info about the user to send to the mod
            EmbedBuilder aboutBuilder = new EmbedBuilder();
            aboutBuilder.setAuthor(author.getName() + "#" + author.getDiscriminator(),  author.getAvatarUrl(), author.getAvatarUrl());
            aboutBuilder.setColor(Color.WHITE);

            try{
                aboutBuilder.addField("", String.format("%s was created %s days ago, joined at %s with &d past threads.", author.getAsMention(),
                        author.getTimeCreated().getDayOfMonth() + "/" + ((OffsetDateTime)timeJoined).getMonth().toString() + "/" + ((OffsetDateTime)timeJoined).getYear(),
                        ((OffsetDateTime)timeJoined).getDayOfMonth() + "/" + ((OffsetDateTime)timeJoined).getMonth().toString() + "/" + ((OffsetDateTime)timeJoined).getYear(), Database.amountOfPastThread(author.getId())) , true);
            }catch(Exception exception){
                aboutBuilder.addField("", (String) timeJoined, false);
                exception.printStackTrace();
            }

            aboutBuilder.addField("Roles", "", false);
            aboutBuilder.setFooter("User ID: " + author.getId() + " • " + "DM ID: " + e.getChannel().getId() +
                    " • " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));



            TextChannel channel = guild.getTextChannelsByName(ChannelName, true).get(0);
            channel.sendMessage("@here").queue();
            channel.sendMessageEmbeds(aboutBuilder.build()).queue();

        }


        //this is the part where the message from client will be redirected to mods
        System.out.println(ChannelName);
        TextChannel channel = guild.getTextChannelsByName(ChannelName, true).get(0);

        EmbedBuilder builder = new EmbedBuilder();
        Calendar calendar = new GregorianCalendar();

        builder.setColor(Color.yellow);
        builder.setAuthor(author.getName(), author.getAvatarUrl(), author.getAvatarUrl());
        builder.setDescription(e.getMessage().getContentRaw());
        builder.setFooter("Message ID: " + e.getMessage().getId() + " • " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));



        channel.sendMessageEmbeds(builder.build()).queue();
        e.getChannel().sendMessageEmbeds(builder.build()).queue();



    }
}
