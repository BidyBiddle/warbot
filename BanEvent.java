import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class BanEvent extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public BanEvent(BotControl botControl, MysqlControl mysqlControl) {

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

        if(!messageSplit[0].equalsIgnoreCase("!ban")) {
            return;
        }

        if(!((boolean) user.get("admin"))) {
            return;
        }

        if (messageSplit.length < 3) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[BAN] usage: !ban [network] [user] {reason}");
            return;
        }
    
        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), "!ban", message);
        
        boolean test1 = botControl.updateUser(messageSplit[1], messageSplit[2], "status", false);
        System.out.println("first test is |" + test1 + "|");
        String reason = message.substring(messageSplit[0].length() + 1 + messageSplit[1].length() + 1 + messageSplit[2].length() + 1);
        System.out.println("reason is |" + reason + "|");
        boolean test2 = botControl.updateUser(messageSplit[1], messageSplit[2], "reason", reason);
        System.out.println("second test is |" + test2 + "|");
        
        if (test1 && test2) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[BAN] " + messageSplit[2] + " @ " + messageSplit[1] + " - Success");
        } 
        else {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[BAN] " + messageSplit[2] + " @ " + messageSplit[1] + " - Failure");
        }

    }

}