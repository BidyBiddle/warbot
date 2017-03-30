import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class DisplayEvent extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public DisplayEvent(BotControl botControl, MysqlControl mysqlControl) {

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

        if(!messageSplit[0].equalsIgnoreCase("!displaymsg") && 
           !messageSplit[0].equalsIgnoreCase("!displaypcw") && 
           !messageSplit[0].equalsIgnoreCase("!displaycw") &&
           !messageSplit[0].equalsIgnoreCase("!displayringer") && 
           !messageSplit[0].equalsIgnoreCase("!displayrecruit") && 
           !messageSplit[0].equalsIgnoreCase("!displayquakenet") &&
           !messageSplit[0].equalsIgnoreCase("!displayfreenode") && 
           !messageSplit[0].equalsIgnoreCase("!displaygamesurge") && 
           !messageSplit[0].equalsIgnoreCase("!displayenterthegame")) {
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

        if (messageSplit.length != 2 || (!messageSplit[1].equalsIgnoreCase("on") && !messageSplit[1].equalsIgnoreCase("off"))) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase() + " [on/off]");
            return;
        }

        boolean value = false;
        if(messageSplit[1].equalsIgnoreCase("on")) {
            value = true;
        }

        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), messageSplit[0].toUpperCase(), message);
        
        if (botControl.updateChannel((String) network.get("name"), (String) channel.get("name"), messageSplit[0].substring(1).toLowerCase(), value)) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] Success");
        } 
        else {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] Failure");
        }

    }

}