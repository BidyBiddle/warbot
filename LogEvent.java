import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class LogEvent extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public LogEvent(BotControl botControl, MysqlControl mysqlControl) {

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

        if(!messageSplit[0].equalsIgnoreCase("!log")) {
            return;
        }

        if(!((boolean) user.get("admin"))) {
            return;
        }

        if (messageSplit.length < 4) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[LOG] usage: !log [network] [channel/user/%..%] [amount]");
            return;
        }
        
        try {
            Integer.parseInt(message.substring(message.lastIndexOf(" ") + 1));
        }
        catch(Exception exception) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[LOG] usage: !log [network] [channel/user/%..%] [amount]");
            return;   
        }
        
        ArrayList logs = mysqlControl.getLogs(messageSplit[1], message.substring(messageSplit[0].length() + 1 + messageSplit[1].length() + 1, message.lastIndexOf(" ")), Integer.parseInt(message.substring(message.lastIndexOf(" ") + 1)));
        if (logs.size() == 0) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[LOG] " + messageSplit[2] + " @ " + messageSplit[1] + " - No logs found");
            return;
        }
  
        for(int logsCounter = 0; logsCounter < logs.size(); logsCounter++) {
            String log = (String) logs.get(logsCounter);
            pircBotX.sendRawLineNow(ircChannel, log);
        }
    
    }

}