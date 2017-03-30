import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class StatusEvent extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public StatusEvent(BotControl botControl, MysqlControl mysqlControl) {

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

        if(!messageSplit[0].equalsIgnoreCase("!status")) {
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

        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), "!status", message);
        
        String submitmsg = "off";
        if (((boolean) channel.get("submitmsg"))) {
            submitmsg = "on";
        }
        String displaymsg = "off";
        if (((boolean) channel.get("displaymsg"))) {
            displaymsg = "on";
        }
        String submitpcw = "off";
        if (((boolean) channel.get("submitpcw"))) {
            submitpcw = "on";
        }
        String displaypcw = "off";
        if (((boolean) channel.get("displaypcw"))) {
            displaypcw = "on";
        }
        String submitcw = "off";
        if (((boolean) channel.get("submitcw"))) {
            submitcw = "on";
        }
        String displaycw = "off";
        if (((boolean) channel.get("displaycw"))) {
            displaycw = "on";
        }
        String submitringer = "off";
        if (((boolean) channel.get("submitringer"))) {
            submitringer = "on";
        }
        String displayringer = "off";
        if (((boolean) channel.get("displayringer"))) {
            displayringer = "on";
        }
        String submitrecruit = "off";
        if (((boolean) channel.get("submitrecruit"))) {
            submitrecruit = "on";
        }
        String displayrecruit = "off";
        if (((boolean) channel.get("displayrecruit"))) {
            displayrecruit = "on";
        }
        String displayquakenet = "off";
        if (((boolean) channel.get("displayquakenet"))) {
            displayquakenet = "on";
        }
        String displayfreenode = "off";
        if (((boolean) channel.get("displayfreenode"))) {
            displayfreenode = "on";
        }
        String displaygamesurge = "off";
        if (((boolean) channel.get("displaygamesurge"))) {
            displaygamesurge = "on";
        }
        String displayenterthegame = "off";
        if (((boolean) channel.get("displayenterthegame"))) {
            displayenterthegame = "on";
        }

        pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[STATUS] Game: " + ((String) channel.get("game")) + " | submitmsg: " + submitmsg + " | displaymsg: " + displaymsg + " | submitpcw: " + submitpcw + " | displaypcw: " + displaypcw + " | submitcw: " + submitcw + " | displaycw: " + displaycw + " | submitringer: " + submitringer + " | displayringer: " + displayringer + " | submitrecruit: " + submitrecruit + " | displayrecruit: " + displayrecruit + " | displayquakenet: " + displayquakenet + " | displayfreenode: " + displayfreenode + " | displaygamesurge: " + displaygamesurge + " | displayenterthegame: " + displayenterthegame);
       
    }

}