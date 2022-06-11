package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
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
   public static FileWriter log = null;


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
                    .setTopic("Kai Support Modmail")
                    .queue();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            // this is the message we will send when the ticket is open
            EmbedBuilder DMbuilder = new EmbedBuilder();

            Calendar calendar = new GregorianCalendar();

            DMbuilder.setTitle("Ticket opened!");
            DMbuilder.setColor(Color.CYAN);
            DMbuilder.setDescription("Ticket opened! we will get back to you asap!");
            DMbuilder.setThumbnail(e.getAuthor().getEffectiveAvatarUrl());
            DMbuilder.setFooter("Message ID: " + e.getMessage().getId() + " • " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
            e.getChannel().sendMessageEmbeds(DMbuilder.build()).queue();

            Database.adduser(author.getId());
            Object timeJoined;
            Member member = guild.retrieveMember(author).complete();
            try{
                System.out.println(author.getId());
                timeJoined = member.getTimeJoined();

            }catch(NullPointerException exception){
                timeJoined = "owner of this server";
            }

            //info about the user to send to the mod
            EmbedBuilder aboutBuilder = new EmbedBuilder();
            aboutBuilder.setAuthor(author.getName() + "#" + author.getDiscriminator(),  author.getAvatarUrl(), author.getAvatarUrl());
            aboutBuilder.setColor(Color.WHITE);

            try{
                assert timeJoined instanceof OffsetDateTime;
                aboutBuilder.addField("", String.format("%s was created at **%s**, joined at **%s** with **%d** past threads.", author.getAsMention(),
                        author.getTimeCreated().getDayOfMonth() + "/" + author.getTimeCreated().getMonthValue()+ "/" + ((OffsetDateTime)timeJoined).getYear(),
                        ((OffsetDateTime)timeJoined).getDayOfMonth() + "/" + ((OffsetDateTime)timeJoined).getMonthValue() + "/" + ((OffsetDateTime)timeJoined).getYear(),
                        Database.amountOfPastThread(author.getId())) , true);

            }catch(ClassCastException exception){
                aboutBuilder.addField("", (String) timeJoined, false);
                exception.printStackTrace();
            }

            StringBuilder roles = new StringBuilder();
            member.getRoles().forEach(role -> roles.append(role.getAsMention()));

            aboutBuilder.addField("Roles", roles.toString(), false);
            aboutBuilder.setFooter("User ID: " + author.getId() + " • " + "DM ID: " + e.getChannel().getId() +
                    " • " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));



            TextChannel channel = guild.getTextChannelsByName(ChannelName, true).get(0);
            channel.sendMessage("@here").queue();
            channel.sendMessageEmbeds(aboutBuilder.build()).queue();

            //mod log file
            try {
                log = new FileWriter(String.format("%s.txt", ChannelName.toLowerCase()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            try {
                StringBuilder timeAndDate = new StringBuilder();
                String date = e.getMessage().getTimeCreated().toString().replace('T', ' ').replace('Z', ' ').split(" ")[0];
                String Time = e.getMessage().getTimeCreated().toString().replace('T', ' ').replace('Z', ' ').split(" ")[1];

                log.write(String.format("User: %s(%s), Date: %s, Time created: %s UTC", author.getName() + "#" + author.getDiscriminator(), author.getId(), date, Time));
                log.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }


        //this is the part where the message from client will be redirected to mods
        TextChannel channel = guild.getTextChannelsByName(ChannelName, true).get(0);

        EmbedBuilder builder = new EmbedBuilder();
        Calendar calendar = new GregorianCalendar();

        builder.setColor(Color.yellow);
        builder.setAuthor(author.getName(), author.getAvatarUrl(), author.getAvatarUrl());
        builder.setDescription(e.getMessage().getContentRaw());
        builder.setFooter("Message ID: " + e.getMessage().getId() + " • " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));



        channel.sendMessageEmbeds(builder.build()).queue();
        e.getMessage().getAttachments().forEach(attachment -> channel.sendMessage(attachment.getUrl()).queue());

        e.getChannel().sendMessageEmbeds(builder.build()).queue();
        e.getMessage().getAttachments().forEach(attachment -> e.getChannel().sendMessage(attachment.getUrl()).queue());

        try {
            String Time = e.getMessage().getTimeCreated().toString().replace('T', ' ').replace('Z', ' ').split(" ")[1];
            String date = e.getMessage().getTimeCreated().toString().replace('T', ' ').replace('Z', ' ').split(" ")[0];

            StringBuilder embedUrls = new StringBuilder();
            e.getMessage().getEmbeds().forEach(messageEmbed -> embedUrls.append(messageEmbed.getUrl() + " "));

            log = new FileWriter(String.format("%s.txt", ChannelName));
            log.write("\n");
            log.write(String.format("User: %s => %s (Time: %s , Date: %s)", author.getName(), e.getMessage().getContentRaw() + embedUrls, Time, date));
            log.close();


        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
