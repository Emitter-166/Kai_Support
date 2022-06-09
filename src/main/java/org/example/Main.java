package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createLight("OTYxNjM3NDA1NTcwNDk4NjUy.Yk74nw.LufU3gdlV1CLmf1nRFwkXcZRMho")
                .setActivity(Activity.listening("Kai support"))
                .addEventListeners(new onDM())
                .build();
    }
}