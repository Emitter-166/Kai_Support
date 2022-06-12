package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class Main {

    public static void main(String[] args) throws LoginException, InterruptedException {
         JDA jda = JDABuilder.createLight(System.getenv("token"))
                .setActivity(Activity.playing("DM FOR HELP"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new Database())
                .addEventListeners(new reply())
                .build();
                jda.awaitReady();
                jda.addEventListener(new onDM(jda));
                jda.addEventListener(new closeAndLogs());

    }
}