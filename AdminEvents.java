/*import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class AdminEvents extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public AdminEvents(BotControl botControl, MysqlControl mysqlControl) {

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

        switch (messageSplit[0].toLowerCase()) {
            case "!reset":
                if(((boolean) user.get("admin"))) {    
                    this.reset(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }
                break;
            case "!adminmsg":
                if(((boolean) user.get("admin"))) {    
                    this.adminmsg(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }
                break;
        }

    }


    public void reset(PircBotX pircBotX, HashMap network, HashMap channel, HashMap user, Channel ircChannel, User ircUser, String message, String[] messageSplit) {

        if (messageSplit.length != 3) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RESET] usage: !reset [network] [number]");
            return;
        }

        try {
            Integer.parseInt(messageSplit[2]);
        }
        catch(Exception exception) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RESET] usage: !reset [network] [number]");
            return;   
        }

        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), "!reset", message);
        if (botControl.reset(messageSplit[1], Integer.parseInt(messageSplit[2]))) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RESET] " + messageSplit[2] + " @ " + messageSplit[1] + " - Success");
        }
        else {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[RESET] " + messageSplit[2] + " @ " + messageSplit[1] + " - Failure");
        }
    
    }
 

    public void adminmsg(PircBotX pircBotX, HashMap network, HashMap channel, HashMap user, Channel ircChannel, User ircUser, String message, String[] messageSplit) {

        if (messageSplit.length < 3) {              
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[ADMINMSG] usage: !adminmsg [game] [message]");
        }

        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), "!adminmsg", message);
        pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[ADMINMSG] Message succesfully broadcasted to: " + botControl.adminmsg(messageSplit[1],"[ADMINMSG] #warfields @ Quakenet - " + ircUser.getNick() + " - " + message.substring(messageSplit[0].length() + 1 + messageSplit[1].length() + 1)));

    }

}*/