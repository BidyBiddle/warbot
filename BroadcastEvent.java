import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class BroadcastEvent extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public BroadcastEvent(BotControl botControl, MysqlControl mysqlControl) {

		this.botControl = botControl;
        this.mysqlControl = mysqlControl;

	}


    public void onMessage(MessageEvent event) throws Exception {
        
        PircBotX pircBotX = event.getBot();
        HashMap network = botControl.getNetwork(event.getBot());
        HashMap channel = botControl.getChannel(((String) network.get("name")), event.getChannel().getName());
        HashMap user = botControl.getUser(((String) network.get("name")), event.getUser().getNick());
        Channel ircChannel = event.getChannel();
        User ircUser = event.getUser();
        String message = event.getMessage();
        String[] messageSplit = event.getMessage().split(" ");

        if(!messageSplit[0].equalsIgnoreCase("!pcw") && 
           !messageSplit[0].equalsIgnoreCase("!cw") &&
           !messageSplit[0].equalsIgnoreCase("!ringer") && 
           !messageSplit[0].equalsIgnoreCase("!recruit")) {
           return;
        }

        System.out.println("broadcast ontvangen");
        
        if(!((boolean) user.get("admin")) && !ircChannel.getOps().contains(ircUser)) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] You need to be opped to use this function");
            return;
        }

        System.out.println("1");
        
        if (messageSplit.length < 2) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase() + " [amount of players 1-9] {additional information}");
            return;
        }
        
        System.out.println("2");
        try {
            Integer.parseInt(messageSplit[1]);
        }
        catch(Exception exception) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase() + " [amount of players 1-9] {additional information}");
            return;
        }

        System.out.println("4");
        
        if (Integer.parseInt(messageSplit[1]) < 1 || Integer.parseInt(messageSplit[1]) > 9) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase() + " [amount of players 1-9] {additional information}");
            return;
        }
        
        System.out.println("5");
        
        if (user.containsKey("lastBroadcast") && ((new Date().getTime() - ((long) user.get("lastBroadcast"))) / 1000) < 600) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] You cannot do another broadcast for another " + String.valueOf(600 - (new Date().getTime() - ((long) user.get("lastBroadcast"))) / 1000) + " seconds.");
            return;
        }
        
        System.out.println("6");
        
        if (channel.containsKey("lastBroadcast") && ((new Date().getTime() - ((long) channel.get("lastBroadcast"))) / 1000) < 600) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] This channel cannot do another broadcast for another " + String.valueOf(600 - (new Date().getTime() - ((long) user.get("lastBroadcast"))) / 1000) + " seconds.");
            return;
        }
        
        System.out.println("7");
        
        /*if (!botControl.validate(messageSplit)) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] The broadcast contains illegal words");
            return;
        }*/
        
        System.out.println("8");
        
        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), messageSplit[0].toLowerCase(), message);
        
        System.out.println("9");
        
        if (messageSplit.length == 2) {
        System.out.println("10");
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] Succesfully broadcasted to: " + botControl.broadcast(((String) network.get("name")), ((String) channel.get("name")), ((String) channel.get("game")), messageSplit[0].substring(1).toLowerCase(), "[" + messageSplit[0].substring(1).toUpperCase() + "] " + ((String) channel.get("name")) + " @ " + ((String) network.get("name")) + " - " + ircUser.getNick() + " - Requested a " + messageSplit[1] + " vs " + messageSplit[1]));
            System.out.println("11");
        }
        else {
        System.out.println("12");
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] Succesfully broadcasted to: " + botControl.broadcast(((String) network.get("name")), ((String) channel.get("name")), ((String) channel.get("game")), messageSplit[0].substring(1).toLowerCase(), "[" + messageSplit[0].substring(1).toUpperCase() + "] " + ((String) channel.get("name")) + " @ " + ((String) network.get("name")) + " - " + ircUser.getNick() + " - Requested a " + messageSplit[1] + " vs " + messageSplit[1] + " (Additional info: " + message.substring(messageSplit[0].length() + 1 + messageSplit[1].length() + 1) + ")"));
            System.out.println("13");
        }
        
        user.put("lastBroadcast", new Date().getTime());
        channel.put("lastBroadcast", new Date().getTime());

    }
    
}