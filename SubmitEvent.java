import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class SubmitEvent extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public SubmitEvent(BotControl botControl, MysqlControl mysqlControl) {

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

        if(!messageSplit[0].equalsIgnoreCase("!submitmsg") && 
           !messageSplit[0].equalsIgnoreCase("!submitpcw") && 
           !messageSplit[0].equalsIgnoreCase("!submitcw") &&
           !messageSplit[0].equalsIgnoreCase("!submitringer") && 
           !messageSplit[0].equalsIgnoreCase("!submitrecruit")) {
           return;
        }

        if(!((boolean) user.get("admin"))) {
            return;
        }

        if (messageSplit.length != 4 || (!messageSplit[3].equalsIgnoreCase("on") && !messageSplit[3].equalsIgnoreCase("off"))) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase() + " [network] [#channel] [on/off]");
            return;
        }

        boolean value = false;
        if(messageSplit[3].equalsIgnoreCase("on")) {
            value = true;
        }

        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), messageSplit[0].toUpperCase(), message);
        
        if (botControl.updateChannel(messageSplit[1], messageSplit[2], messageSplit[0].substring(1).toLowerCase(), value)) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] " + messageSplit[2] + " @ " + messageSplit[1] + " - Success");
        } 
        else {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] " + messageSplit[2] + " @ " + messageSplit[1] + " - Failure");
        }

    }

}