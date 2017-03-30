import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.*;


public class UserEvents extends ListenerAdapter {


	protected BotControl botControl;
    protected MysqlControl mysqlControl;


	public UserEvents(BotControl botControl, MysqlControl mysqlControl) {

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

        switch (messageSplit[0].toLowerCase()) {
            /*case "!msg":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.msg(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;*/
            case "!pcw":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.broadcast(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!cw":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.broadcast(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!ringer":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.broadcast(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!recruit":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.broadcast(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!displaymsg":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.display(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!displaypcw":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.display(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!displaycw":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.display(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!displayringer":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.display(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!displayrecruit":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.display(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!displayquakenet":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.display(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!displayfreenode":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.display(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!displaygamesurge":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.display(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!displayenterthegame":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.display(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!msglist":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.list(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!pcwlist":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.list(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!cwlist":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.list(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!ringerlist":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.list(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!recruitlist":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.list(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
            case "!status":
                if(ircChannel.getOps().contains(ircUser) || ((boolean) user.get("admin"))) {
                    this.status(pircBotX, network, channel, user, ircChannel, ircUser, message, messageSplit);
                }                
                break;
        }

    }


   public void broadcast(PircBotX pircBotX, HashMap network, HashMap channel, HashMap user, Channel ircChannel, User ircUser, String message, String[] messageSplit) {

        if (!((boolean) channel.get("submit" + messageSplit[0].substring(1).toLowerCase()))) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] This channel is not allowed to use this function.");
            return;
        }
        if (messageSplit.length < 2) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase() + " [amount of players 1-9] {additional information}");
            return;
        }
        try {
            Integer.parseInt(messageSplit[1]);
        }
        catch(Exception exception) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase() + " [amount of players 1-9] {additional information}");
        }
            
        if (Integer.parseInt(messageSplit[1]) < 1 || Integer.parseInt(messageSplit[1]) > 9) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase() + " [amount of players 1-9] {additional information}");
        }
        if (user.containsKey("lastBroadcast") && ((new Date().getTime() - ((long) user.get("lastBroadcast"))) / 1000) < 600) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] You cannot do another broadcast for another " + String.valueOf(600 - (new Date().getTime() - ((long) user.get("lastBroadcast"))) / 1000) + " seconds.");
            return;
        }
        if (channel.containsKey("lastBroadcast") && ((new Date().getTime() - ((long) channel.get("lastBroadcast"))) / 1000) < 600) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] This channel cannot do another broadcast for another " + String.valueOf(600 - (new Date().getTime() - ((long) user.get("lastBroadcast"))) / 1000) + " seconds.");
            return;
        }
        if (!botControl.validate(messageSplit)) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] The broadcast contains illegal words");
            return;
        }
        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), "!submit" + messageSplit[0].substring(1).toLowerCase(), message);
        if (messageSplit.length == 2) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] Succesfully broadcasted to: " + botControl.broadcast(((String) network.get("name")), ((String) channel.get("name")), ((String) channel.get("game")), messageSplit[0].substring(1).toLowerCase(), "[" + messageSplit[0].substring(1).toUpperCase() + "] " + ((String) channel.get("name")) + " @ " + ((String) network.get("name")) + " - " + ircUser.getNick() + " - Requested a " + messageSplit[1] + " vs " + messageSplit[1]));
        }
        else {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] Succesfully broadcasted to: " + botControl.broadcast(((String) network.get("name")), ((String) channel.get("name")), ((String) channel.get("game")), messageSplit[0].substring(1).toLowerCase(), "[" + messageSplit[0].substring(1).toUpperCase() + "] " + ((String) channel.get("name")) + " @ " + ((String) network.get("name")) + " - " + ircUser.getNick() + " - Requested a " + messageSplit[1] + " vs " + messageSplit[1] + " (Additional info: " + message.substring(messageSplit[0].length() + 1 + messageSplit[1].length() + 1) + ")"));
        }
        user.put("lastBroadcast", new Date().getTime());
        channel.put("lastBroadcast", new Date().getTime());

    }


    public void display(PircBotX pircBotX, HashMap network, HashMap channel, HashMap user, Channel ircChannel, User ircUser, String message, String[] messageSplit) {

        if (messageSplit.length != 2 || (!messageSplit[1].equalsIgnoreCase("on") && !messageSplit[1].equalsIgnoreCase("off"))) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase() + " [on/off]");
            return;
        }

        boolean value = false;
        if(messageSplit[1].equalsIgnoreCase("on")) {
            value = true;
        }

        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), "!display" + messageSplit[0].substring(1).toLowerCase(), message);
        if (botControl.display(((String) network.get("name")), ((String) channel.get("name")), messageSplit[0].substring(1).toLowerCase(), value)) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] Success");
        } 
        else {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] Failure");
        }

    }


    public void list(PircBotX pircBotX, HashMap network, HashMap channel, HashMap user, Channel ircChannel, User ircUser, String message, String[] messageSplit) {

        if(messageSplit.length != 1) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] usage: " + messageSplit[0].toLowerCase());
            return;
        }
        
        mysqlControl.addLog(((Integer) channel.get("id")), ((Integer) user.get("id")), ircUser.getAuth(), messageSplit[0].toLowerCase(), message);
        ArrayList broadcasts = mysqlControl.getBroadcasts(((String) channel.get("game")), "!msg");
        if (broadcasts.size() == 0) {
            pircBotX.sendRawLineNow("PRIVMSG " + ircChannel.getName() + " :[" + messageSplit[0].substring(1).toUpperCase() + "] No messages found for the last 30 minutes.");
            return;
        }
        for(int broadcastsCounter = 0; broadcastsCounter < broadcasts.size(); broadcastsCounter++) {
            String broadcast = (String) broadcasts.get(broadcastsCounter);
            pircBotX.sendMessage(ircChannel, broadcast);
        }

    }


    public void status(PircBotX pircBotX, HashMap network, HashMap channel, HashMap user, Channel ircChannel, User ircUser, String message, String[] messageSplit) {

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