/*import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class DisallowEvent extends ListenerAdapter {


    protected BotControl botControl;
    protected MysqlControl mysqlControl;


    public DisallowEvent(BotControl botControl, MysqlControl mysqlControl) {

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

        if(!messageSplit[0].equalsIgnoreCase("!disallow")) {
            return;
        }

        if(!((boolean) user.get("admin"))) {
            return;
        }

        if (messageSplit.length != 2) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[DISALLOW] usage: !disallow [word]");
            return;
        }

        //mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), "!disallow", message);
        if (botControl.removeAllowedWord(messageSplit[1])) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[DISALLOW] " + messageSplit[1] + " - Success");
        }
        else {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[DISALLOW] " + messageSplit[1] + " - Failure");
        }
    
    }

}*/