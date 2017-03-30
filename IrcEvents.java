import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.*;


public class IrcEvents extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public IrcEvents(BotControl botControl, MysqlControl mysqlControl) {

		this.botControl = botControl;
        this.mysqlControl = mysqlControl;

	}

    
    public void onConnect(ConnectEvent event) throws Exception {

    	HashMap network = botControl.getNetwork(event.getBot());
		
    	System.out.println("Trying to send perform line |" + (String) network.get("perform") + "|");

		((PircBotX) event.getBot()).sendRawLine((String) network.get("perform"));		

		//Sleep for 10 seconds to not flood IRC
        try {
            Thread.sleep(10000);
        }
        catch(Exception exception) {
        	ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
        }

		((PircBotX) event.getBot()).joinChannel("#warfields");

		//Sleep for 10 seconds to not flood IRC
        try {
            Thread.sleep(30000);
        }
        catch(Exception exception) {
        	ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
        }

    	HashMap bot = botControl.getBot(event.getBot());

    	while(event.getBot().isConnected()) {

            ArrayList channels = (ArrayList) bot.get("channels"); 

        	//Loop through every channel
    		for (int channelsCounter = 0; channelsCounter < channels.size(); channelsCounter++) {

    			//Get the specific channel
    			HashMap channel = (HashMap) channels.get(channelsCounter);

                Set<Channel> ircChannels = event.getBot().getChannels();
                boolean ircChannelFound = false;

                for(Channel ircChannel : ircChannels) {

                    if(ircChannel.getName().equalsIgnoreCase(((String) channel.get("name")))) {
                        ircChannelFound = true;
                    }

                }
                
                if(!ircChannelFound) {
        			//Join the channel
        			((PircBotX) event.getBot()).joinChannel((String) channel.get("name"), (String) channel.get("password"));
                    
                    //Sleep for 30 seconds to not flood IRC
                    try {
                        Thread.sleep(30000);
                    }
                    catch(Exception exception) {
                    	ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
                    }
                }
                
    		}
            
            //Sleep for 5 minutes
            try {
                Thread.sleep(300000);
            }
            catch(Exception exception) {
                ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
            }
            
        }

    }    


    //Function which is called when the bot gets disconnected from the irc server
    public void onDisconnect(DisconnectEvent event) throws Exception {

        try {
            Thread.sleep(120000);
        }
        catch(Exception exception) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
        }
        
        HashMap bot = botControl.getBot(event.getBot());
        System.out.println(event.getBot().getName() + " Trying to connect to: " + event.getBot().getServer());
        BotConnect botConnect = new BotConnect(bot, event.getBot().getServer());
        new Thread(botConnect).start();
    }


    public void onUserList(UserListEvent event) throws Exception {
 
        for (User user : (Set<User>) event.getUsers()) {
            if (user.getAuth() == "" && user.getNick().indexOf("warbot") == -1) {
                ((PircBotX) event.getBot()).sendRawLine("WHOIS " + user.getNick());
            }
        }
 
    }


    public void onJoin(JoinEvent event) throws Exception {
 
        if (event.getUser().getAuth() == "" && event.getUser().getNick().indexOf("warbot") == -1) {
            ((PircBotX) event.getBot()).sendRawLine("WHOIS " + event.getUser().getNick());
        }
 
    }


    public void onInvite(InviteEvent event) throws Exception {

        HashMap bot = botControl.getBot(event.getBot());
        
        ArrayList channels = (ArrayList) bot.get("channels"); 

        //Loop through every channel
        for (int channelsCounter = 0; channelsCounter < channels.size(); channelsCounter++) {

            //Get the specific channel
            HashMap channel = (HashMap) channels.get(channelsCounter);

            if ((String) channel.get("name") == event.getChannel()) {
                ((PircBotX) event.getBot()).joinChannel(event.getChannel());
                break;
            }

        }

    }


    public void onKick(KickEvent event) throws Exception {
 
        ((PircBotX) event.getBot()).joinChannel(event.getChannel().getName());
 
    }   


    public void onUserMode(UserModeEvent event) throws Exception {
 
        if (event.getTarget().getAuth() == "" && event.getTarget().getNick().indexOf("warbot") == -1) {
            ((PircBotX) event.getBot()).sendRawLine("WHOIS " + event.getTarget().getNick());
        }
 
    }


    public void onServerResponse(ServerResponseEvent event) throws Exception {

        HashMap network = botControl.getNetwork(event.getBot());
        HashMap bot = botControl.getBot(event.getBot());
        
        if (((String) network.get("name")).equalsIgnoreCase("quakenet")) {
            if (event.getCode() == 330) {
                String[] responseSplit = event.getResponse().split(" ");   
                Set<User> users = ((PircBotX) event.getBot()).getUsers();
                for (User user : users) {
                    if (user.getNick().equalsIgnoreCase(responseSplit[1])) {
                        if (user.getAuth() == "") {
                            user.setAuth(responseSplit[2].toLowerCase());
                            botControl.addUser("quakenet", responseSplit[2].toLowerCase());
                        }
                        break;
                    }
                }
            }
        }

        if (((String) network.get("name")).equalsIgnoreCase("freenode")) {
            if (event.getCode() == 320) {
                String[] responseSplit = event.getResponse().split(" ");
                if (responseSplit.length == 8) {
                    Set<User> users = ((PircBotX) event.getBot()).getUsers();
                    for (User user : users) {
                        if (user.getNick().equalsIgnoreCase(responseSplit[1])) {
                            if (user.getAuth() == "") {
                                user.setAuth(responseSplit[7].toLowerCase());
                                botControl.addUser("freenode", responseSplit[7].toLowerCase());
                            }
                            break;
                        }
                    }
                }
            }
        }

        if (((String) network.get("name")).equalsIgnoreCase("gamesurge")) {
            if (event.getCode() == 330) {
                String[] responseSplit = event.getResponse().split(" ");
                Set<User> users = ((PircBotX) event.getBot()).getUsers();
                for (User user : users) {
                    if (user.getNick().equalsIgnoreCase(responseSplit[1])) {
                        if (user.getAuth() == "") {
                            user.setAuth(responseSplit[2].toLowerCase());
                            botControl.addUser("gamesurge", responseSplit[2].toLowerCase());
                        }
                        break;
                    }
                }
            }
        }

    }

}