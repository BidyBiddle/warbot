import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class RingerEvent extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public RingerEvent(BotControl botControl, MysqlControl mysqlControl) {

		this.botControl = botControl;
        this.mysqlControl = mysqlControl;

	}


    public void onMessage(MessageEvent event) throws Exception {
        
        PircBotX pircBotX = event.getBot();
        HashMap network = botControl.getNetwork(event.getBot());
        HashMap channel = botControl.getChannel(((String) network.get("name")), event.getChannel().getName());
        HashMap user = botControl.getUser(((String) network.get("name")), event.getUser().getAuth());
        Channel ircChannel = event.getChannel();
        User ircUser = event.getUser();
        String message = event.getMessage();
        String[] messageSplit = event.getMessage().split(" ");

        if(!messageSplit[0].equalsIgnoreCase("!ringer")) {
           return;
        }

        if(user.isEmpty()) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RINGER] You need to be authed to use this function");
            return;
        }        

        if(!((boolean) user.get("admin")) && !ircChannel.getOps().contains(ircUser)) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RINGER] You need to be opped to use this function");
            return;
        }

        if(!((boolean) channel.get("submitringer"))) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[MSG] This channel is not allowed to use this command");
            return;
        } 
        
        if (messageSplit.length < 2) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RINGER] usage: !ringer [amount of players 1-9] {additional information}");
            return;
        }
        
        try {
            Integer.parseInt(messageSplit[1]);
        }
        catch(Exception exception) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RINGER] usage: !ringer [amount of players 1-9] {additional information}");
            return;
        }
        
        if (Integer.parseInt(messageSplit[1]) < 1 || Integer.parseInt(messageSplit[1]) > 9) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RINGER] usage: !ringer [amount of players 1-9] {additional information}");
            return;
        }
        
        if (user.containsKey("lastBroadcast") && ((new Date().getTime() - ((long) user.get("lastBroadcast"))) / 1000) < 600) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RINGER] You cannot do another broadcast for another " + String.valueOf(600 - (new Date().getTime() - ((long) user.get("lastBroadcast"))) / 1000) + " seconds.");
            return;
        }
        
        if (channel.containsKey("lastBroadcast") && ((new Date().getTime() - ((long) channel.get("lastBroadcast"))) / 1000) < 600) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RINGER] This channel cannot do another broadcast for another " + String.valueOf(600 - (new Date().getTime() - ((long) user.get("lastBroadcast"))) / 1000) + " seconds.");
            return;
        }
        
        /*if (!botControl.validate(messageSplit)) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] The broadcast contains illegal words");
            return;
        }*/
        
        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), "!ringer", message);
        
        if (messageSplit.length == 2) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RINGER] Succesfully broadcasted to: " + botControl.broadcast(((String) network.get("name")), ((String) channel.get("name")), ((String) channel.get("game")), "ringer", "[RINGER] " + ((String) channel.get("name")) + " @ " + ((String) network.get("name")) + " - " + ircUser.getNick() + " - Requested " + messageSplit[1]));
        }
        else {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RINGER] Succesfully broadcasted to: " + botControl.broadcast(((String) network.get("name")), ((String) channel.get("name")), ((String) channel.get("game")), "ringer", "[RINGER] " + ((String) channel.get("name")) + " @ " + ((String) network.get("name")) + " - " + ircUser.getNick() + " - Requested " + messageSplit[1] + " (Additional info: " + message.substring(messageSplit[0].length() + 1 + messageSplit[1].length() + 1) + ")"));
        }
        
        user.put("lastBroadcast", new Date().getTime());
        channel.put("lastBroadcast", new Date().getTime());

    }
    
}