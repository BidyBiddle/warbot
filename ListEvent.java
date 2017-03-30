import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class ListEvent extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public ListEvent(BotControl botControl, MysqlControl mysqlControl) {

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

        if(!messageSplit[0].equalsIgnoreCase("!msglist") && 
           !messageSplit[0].equalsIgnoreCase("!pcwlist") && 
           !messageSplit[0].equalsIgnoreCase("!cwlist") &&
           !messageSplit[0].equalsIgnoreCase("!ringerlist") && 
           !messageSplit[0].equalsIgnoreCase("!recruitlist")) {
           return;
        }

        if(user.isEmpty()) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[PCW] You need to be authed to use this function");
            return;
        }   
        
        if(!((boolean) user.get("admin")) && !ircChannel.getOps().contains(ircUser)) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] You need to be opped to use this function");
            return;
        }

        if (messageSplit.length != 1) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase());
            return;
        }

        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), messageSplit[0].toLowerCase(), message);
        
        ArrayList broadcasts = mysqlControl.getBroadcasts(((String) channel.get("game")), messageSplit[0].toLowerCase().substring(0, messageSplit[0].toLowerCase().length() - 4));
        if (broadcasts.size() == 0) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] Nothing found for the last 30 minutes.");
            return;
        }
        for(int broadcastsCounter = 0; broadcastsCounter < broadcasts.size(); broadcastsCounter++) {
            String broadcast = (String) broadcasts.get(broadcastsCounter);
            pircBotX.sendRawLineNow(ircChannel, broadcast);
        }

    }
    
}