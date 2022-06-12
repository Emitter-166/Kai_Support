package org.example;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.NoSuchElementException;

public class Database extends ListenerAdapter {
    static MongoCollection activeThreads;


    @Override
    public void onReady(ReadyEvent e){
        System.out.println("ready");
        MongoClientURI clientURI = new MongoClientURI(System.getenv("uri"));
        MongoClient mongoClient = new MongoClient(clientURI);
        MongoDatabase database = mongoClient.getDatabase("ModMail");
        activeThreads = database.getCollection("Active-Threads");
    }

    /*
    will contain:
    userid
    amount of threads (amount) //int
    log no.

    logs will have:
    channel name
    Messages sent and receive with the time
     */

    public static void adduser(String userId){
        //this method is to add user to the database
        try{
           Document userDoc = (Document) activeThreads.find(new Document("userId", userId)).cursor().next();

           Document threadAmount = new Document("amount", (Integer)userDoc.get("amount") + 1);
           Bson updateKey = new Document("$set", threadAmount);

           activeThreads.updateOne(userDoc, updateKey);

        }catch (NoSuchElementException exception){

            Document userDoc = new Document("userId", userId);
            userDoc.append("amount", 1);

            activeThreads.insertOne(userDoc);
        }
    }


    public static int amountOfPastThread(String userId){
        return  (Integer) ((Document) activeThreads.find(new Document("userId", userId)).cursor().next()).get("amount");
    }





}
