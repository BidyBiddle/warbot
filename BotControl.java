/*

Copyright Jasper Verberk, 2001-2013, http://www.warfields.net/

This file is part of Warfields Bot

*/


import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.*;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;


public class BotControl {


    protected ListenerManager listenerManager;
   
    protected MysqlControl mysqlControl;
    protected ArrayList allowedWords;
    protected ArrayList networks;

    protected String name = "warbot";
	protected String login = "z0rbot";
	protected String version = "visit our website: www.warfields.net !";
	protected boolean autoNickChange = false;
	protected int messageDelay = 2000;
	protected boolean verbose = false;

	
	public BotControl(MysqlControl mysqlControl) {
		
        //Create Listener Manager to use (This will have to be made dynamic)
        listenerManager = new ThreadedListenerManager();
        listenerManager.addListener(new IrcEvents(this, mysqlControl));
        listenerManager.addListener(new JoinEvent(this, mysqlControl));
        listenerManager.addListener(new PartEvent(this, mysqlControl));
        listenerManager.addListener(new GameEvent(this, mysqlControl));
        listenerManager.addListener(new SubmitEvent(this, mysqlControl));
        listenerManager.addListener(new BanEvent(this, mysqlControl));
        listenerManager.addListener(new UnbanEvent(this, mysqlControl));
        listenerManager.addListener(new LogEvent(this, mysqlControl));
        listenerManager.addListener(new DisplayEvent(this, mysqlControl));
        //listenerManager.addListener(new BroadcastEvent(this, mysqlControl));
        listenerManager.addListener(new MsgEvent(this, mysqlControl));
        listenerManager.addListener(new PcwEvent(this, mysqlControl));
        listenerManager.addListener(new CwEvent(this, mysqlControl));
        listenerManager.addListener(new RingerEvent(this, mysqlControl));
        listenerManager.addListener(new RecruitEvent(this, mysqlControl));
        
        listenerManager.addListener(new ListEvent(this, mysqlControl));
        listenerManager.addListener(new StatusEvent(this, mysqlControl));

        //Set the default variables
		this.mysqlControl = mysqlControl;
		this.allowedWords = mysqlControl.getAllowedWords();
		this.networks = mysqlControl.getNetworks();

		//Create a nice tree of information about the bots
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			ArrayList servers = mysqlControl.getServers((Integer) network.get("id"));
			for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
				HashMap server = (HashMap) servers.get(serversCounter);
				server.put("bots", new ArrayList());
			}
			network.put("servers", servers);
			ArrayList channels = mysqlControl.getChannels((Integer) network.get("id"));
			network.put("channels", channels);
			ArrayList users = mysqlControl.getUsers((Integer) network.get("id"));
			network.put("users", users);
			if (servers.size() == 0 || channels.size() == 0) {
				System.out.println("No servers or channels defined in the database for network: " + (String) network.get("name"));
				continue;
			}
			ArrayList botChannels = new ArrayList();
			for (int channelsCounter = 0; channelsCounter < channels.size(); channelsCounter++) {
				HashMap channel = (HashMap) channels.get(channelsCounter);
				if(((boolean) channel.get("status"))) {
					botChannels.add(channel);
				}
				if (botChannels.size() == 10 || (botChannels.size() > 0 && channelsCounter + 1 == channels.size())) {
                	HashMap botServer = new HashMap();
					for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
						HashMap server = (HashMap) servers.get(serversCounter);
						if(botServer.isEmpty() || ((ArrayList) server.get("bots")).size() < ((ArrayList) botServer.get("bots")).size()) {
							botServer = server;
						}
					}
                    System.out.println("nieuwe bot aangemaakt");
					HashMap bot = new HashMap();
					bot.put("number", this.getBots((int) network.get("id")).size() + 1);
                	bot.put("channels", botChannels);
					PircBotX pircBotX = new PircBotX();
    	            pircBotX.setName(this.name + String.format("%03d", ((int) bot.get("number"))));
            	    pircBotX.setLogin(this.login);
                	pircBotX.setVersion(this.version);
                	pircBotX.setAutoNickChange(this.autoNickChange);
					pircBotX.setMessageDelay(this.messageDelay);
                	pircBotX.setVerbose(this.verbose);
                	pircBotX.setListenerManager(listenerManager);
                	bot.put("pircbotx", pircBotX);
					((ArrayList) botServer.get("bots")).add(bot);
					botChannels = new ArrayList();
				}
			}
		}

	}


	public void connect() {
		ArrayList bots = this.getBots();
		for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
            HashMap bot = (HashMap) bots.get(botsCounter);
			HashMap server = this.getServer(bot);
	        BotConnect botConnect = new BotConnect(bot, (String) server.get("host"));
			new Thread(botConnect).start();
		}
	}


	public void disconnect() {
		ArrayList bots = this.getBots();
		for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
			((PircBotX) bots.get(botsCounter)).disconnect();
		}
	}


	public ArrayList getBots() {
		ArrayList result = new ArrayList();
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			ArrayList servers = (ArrayList) network.get("servers");
			for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
				HashMap server = (HashMap) servers.get(serversCounter);
				ArrayList bots = (ArrayList) server.get("bots");
				for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
					HashMap bot = (HashMap) bots.get(botsCounter);
					result.add(bot);
				}
			}
		}
		return result;
	}


	public ArrayList getBots(int network_id) {
		ArrayList result = new ArrayList();
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			if(((int) network.get("id")) == network_id) {
				ArrayList servers = (ArrayList) network.get("servers");
				for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
					HashMap server = (HashMap) servers.get(serversCounter);
					ArrayList bots = (ArrayList) server.get("bots");
					for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
						HashMap bot = (HashMap) bots.get(botsCounter);
						result.add(bot);
					}
				}
			}
		}
		return result;
	}


	public ArrayList getBots(int network_id, int server_id) {
		ArrayList result = new ArrayList();
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			if((int) network.get("id") == network_id) {
				ArrayList servers = (ArrayList) network.get("servers");
				for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
					HashMap server = (HashMap) servers.get(serversCounter);
					if((int) server.get("id") == server_id) {
						ArrayList bots = (ArrayList) server.get("bots");
						for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
							HashMap bot = (HashMap) bots.get(botsCounter);
							result.add(bot);
						}
					}
				}
			}
		}
		return result;
	}


	public HashMap getBot(PircBotX pircBotX) { 
		ArrayList bots = this.getBots();
		for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
			HashMap bot = (HashMap) bots.get(botsCounter);
			if(((PircBotX) bot.get("pircbotx")) == pircBotX) {
				return bot;
			}
		}
		return new HashMap();
	}


	public HashMap getNetwork(PircBotX pircBotX) {
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			ArrayList servers = (ArrayList) network.get("servers");
			for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
				HashMap server = (HashMap) servers.get(serversCounter);
				ArrayList bots = (ArrayList) server.get("bots");
				for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
					HashMap bot = (HashMap) bots.get(botsCounter);
					if(((PircBotX) bot.get("pircbotx")) == pircBotX) {
						return network;
					}
				}
			}
		}
		return new HashMap();
	}


	public HashMap getServer(PircBotX pircBotX) {
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			ArrayList servers = (ArrayList) network.get("servers");
			for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
				HashMap server = (HashMap) servers.get(serversCounter);
				ArrayList bots = (ArrayList) server.get("bots");
				for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
					HashMap bot = (HashMap) bots.get(botsCounter);
					if(((PircBotX) bot.get("pircbotx")) == pircBotX) {
						return server;
					}
				}
			}
		}
		return new HashMap();
	}

    
	public HashMap getServer(HashMap bot) {
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			ArrayList servers = (ArrayList) network.get("servers");
			for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
				HashMap server = (HashMap) servers.get(serversCounter);
				ArrayList bots = (ArrayList) server.get("bots");
				for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
					if((HashMap) bots.get(botsCounter) == bot) {
						return server;
					}
				}
			}
		}
		return new HashMap();
	}

    
    
	public ArrayList getChannels(PircBotX pircBotX) {
		ArrayList result = new ArrayList();
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			ArrayList servers = (ArrayList) network.get("servers");
			for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
				HashMap server = (HashMap) servers.get(serversCounter);
				ArrayList bots = (ArrayList) server.get("bots");
				for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
					HashMap bot = (HashMap) bots.get(botsCounter);
					if(((PircBotX) bot.get("pircbotx")) == pircBotX) {
						result = (ArrayList) bot.get("channels");
					}
				}
			}
		}
		return result;
	}


	public HashMap getNetwork(String network_name) {
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			if(((String) network.get("name")).equalsIgnoreCase(network_name)) {
				return network;
			}
		}
		return new HashMap();
	}


	public HashMap getChannel(String network_name, String channel_name) {
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			if(((String) network.get("name")).equalsIgnoreCase(network_name)) {
				ArrayList channels = (ArrayList) network.get("channels");
				for (int channelsCounter = 0; channelsCounter < channels.size(); channelsCounter++) {
					HashMap channel = (HashMap) channels.get(channelsCounter);
					if(((String) channel.get("name")).equalsIgnoreCase(channel_name)) {
						return channel;
					}
				}
			}
		}
		return new HashMap();
	}


	public boolean addChannel(String network_name, String channel_name, String channel_password) {
		HashMap network = this.getNetwork(network_name);
		HashMap channel = this.getChannel(network_name, channel_name);
		if (network.isEmpty()) {
			return false;
		}
		if (channel.isEmpty()) {
			if (!mysqlControl.addChannel(((Integer) network.get("id")), channel_name, channel_password)) {
				return false;
			}
			channel = mysqlControl.getChannel(((Integer) network.get("id")), channel_name);
			if(channel.isEmpty()){
				return false;
			}
            ((ArrayList) network.get("channels")).add(channel);
		}
		else {
			if (((boolean) channel.get("status"))) {
				return false;
			}
			if (!mysqlControl.updateChannel(((Integer) channel.get("id")), "status", "1")) {
				return false;
			}
			channel.put("status", true);
		}
		ArrayList servers = (ArrayList) network.get("servers");
		for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
			HashMap server = (HashMap) servers.get(serversCounter);
			ArrayList bots = (ArrayList) server.get("bots");
			for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
				HashMap bot = (HashMap) bots.get(botsCounter);
				if(((ArrayList) bot.get("channels")).size() < 10) {
					((ArrayList) bot.get("channels")).add(channel);			
					((PircBotX) bot.get("pircbotx")).joinChannel(channel_name, channel_password);
					return true;
				}
			}
		}
       	HashMap botServer = new HashMap();
		for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
			HashMap server = (HashMap) servers.get(serversCounter);
			if(botServer.isEmpty() || ((ArrayList) server.get("bots")).size() < ((ArrayList) botServer.get("bots")).size()) {
				botServer = server;
			}
		}
		HashMap bot = new HashMap();
		bot.put("number", this.getBots((int) network.get("id")).size() + 1);
		ArrayList botChannels = new ArrayList();
    	botChannels.add(channel);
	   	bot.put("channels", botChannels);
		PircBotX pircBotX = new PircBotX();
        pircBotX.setName(this.name + String.format("%03d", ((int) bot.get("number"))));
	    pircBotX.setLogin(this.login);
    	pircBotX.setVersion(this.version);
    	pircBotX.setAutoNickChange(this.autoNickChange);
		pircBotX.setMessageDelay(this.messageDelay);
    	pircBotX.setVerbose(this.verbose);
    	pircBotX.setListenerManager(listenerManager);
    	bot.put("pircbotx", pircBotX);
    	((ArrayList) botServer.get("bots")).add(bot);
		try {
			BotConnect botConnect = new BotConnect(bot, ((String) botServer.get("host")));
        	new Thread(botConnect).start();
		}
		catch (Exception exception) {
		}
		return true;
	}


	public boolean removeChannel(String network_name, String channel_name) {
		HashMap network = this.getNetwork(network_name);
		HashMap channel = this.getChannel(network_name, channel_name);
		if (network.isEmpty()) {
            System.out.println("1");
			return false;
		}
		if (channel.isEmpty()) {
			System.out.println("2");
            return false;
		}
		else {
			if (!((boolean) channel.get("status"))) {
				System.out.println("3");
                return false;
			}
			if (!mysqlControl.updateChannel(((Integer) channel.get("id")), "status", false)) {
				System.out.println("4");
                return false;
			}
            System.out.println("5");
			channel.put("status", false);
		}
		ArrayList bots = this.getBots((int) network.get("id"));
		for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
			HashMap bot = (HashMap) bots.get(botsCounter);
			ArrayList channels = (ArrayList) bot.get("channels");
			for (int channelsCounter = 0; channelsCounter < channels.size(); channelsCounter++) {
				if(((String) ((HashMap) channels.get(channelsCounter)).get("name")).equalsIgnoreCase(channel_name)) {
					System.out.println("7");
                    channels.remove(channels.get(channelsCounter));
                    System.out.println("8");
					((PircBotX) bot.get("pircbotx")).partChannel(((PircBotX) bot.get("pircbotx")).getChannel(channel_name));
				}
			}
		}
        System.out.println("6");
		return true;
	}


	public boolean updateChannel(String network_name, String channel_name, String key, String value) {
		HashMap network = this.getNetwork(network_name);
		HashMap channel = this.getChannel(network_name, channel_name);
		if (network.isEmpty()) {
			return false;
		}
		if (channel.isEmpty()) {
			return false;
		}
		else {
            if(channel.containsKey(key) && ((String) channel.get(key)).equalsIgnoreCase(value)) {
                return false;
            }
			if (!mysqlControl.updateChannel(((Integer) channel.get("id")), key, value)) {
				return false;
			}
			channel.put(key, value);
			return true;
		}
	}
    
    
    public boolean updateChannel(String network_name, String channel_name, String key, boolean value) {
		HashMap network = this.getNetwork(network_name);
		HashMap channel = this.getChannel(network_name, channel_name);
		if (network.isEmpty()) {
			return false;
		}
		if (channel.isEmpty()) {
			return false;
		}
		else {
            if(channel.containsKey(key) && ((boolean) channel.get(key)) == value) {
                return false;
            }
			if (!mysqlControl.updateChannel(((Integer) channel.get("id")), key, value)) {
				return false;
			}
			channel.put(key, value);
			return true;
		}
	}


	public HashMap getUser(String network_name, String user_name) {
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			ArrayList users = (ArrayList) network.get("users");
			for (int usersCounter = 0; usersCounter < users.size(); usersCounter++) {
				HashMap user = (HashMap) users.get(usersCounter);
				if(((String) network.get("name")).equalsIgnoreCase(network_name) && ((String) user.get("name")).equalsIgnoreCase(user_name)) {
					return user;
				}
			}
		}
		return new HashMap();
	}


	public boolean addUser(String network_name, String user_name) {
		HashMap network = this.getNetwork(network_name);
		HashMap user = this.getUser(network_name, user_name);
		if (network.isEmpty()) {
			return false;
		}
		if (user.isEmpty()) {
			if (!mysqlControl.addUser(((Integer) network.get("id")), user_name)) {
				return false;
			}
			user = mysqlControl.getUser(((Integer) network.get("id")), user_name);
			if (user.isEmpty()) {
				return false;
			}
			else {
				((ArrayList) network.get("users")).add(user);
				return true;
			}
		}
		return false;
	}


	public boolean updateUser(String network_name, String user_name, String key, String value) {
        HashMap network = this.getNetwork(network_name);
        HashMap user = this.getUser(network_name, user_name);
        if (network.isEmpty()) {
            return false;
		}
		if (user.isEmpty()) {
			return false;
		}
		else {
            if(user.containsKey(key) && ((String) user.get(key)).equalsIgnoreCase(value)) {
                return false;
            }
			if (!mysqlControl.updateUser(((Integer) user.get("id")), key, value)) {
				return false;
			}
			user.put(key, value);
			return true;
		}
	}


    public boolean updateUser(String network_name, String user_name, String key, boolean value) {
		HashMap network = this.getNetwork(network_name);
		HashMap user = this.getUser(network_name, user_name);
		if (network.isEmpty()) {
			return false;
		}
		if (user.isEmpty()) {
			return false;
		}
		else {
            if(user.containsKey(key) && ((boolean) user.get(key)) == value) {
                return false;
            }
			if (!mysqlControl.updateUser(((Integer) user.get("id")), key, value)) {
				return false;
			}
			user.put(key, value);
			return true;
		}
	}


	public boolean allow(String word) {
		
		for(int allowedWordsCounter = 0; allowedWordsCounter < allowedWords.size(); allowedWordsCounter++) {
			String allowedWord = (String) allowedWords.get(allowedWordsCounter);
			if (allowedWord.equalsIgnoreCase(word)) {
				return false;
			}
		}

        if(!mysqlControl.allow(word)) {
           	return false;
        }
		allowedWords.add(word);
        return true;

	}
    
  
  	public boolean disallow(String word) {
		
     for(int allowedWordsCounter = 0; allowedWordsCounter < allowedWords.size(); allowedWordsCounter++) {
			String allowedWord = (String) allowedWords.get(allowedWordsCounter);
			if (allowedWord.equalsIgnoreCase(word)) {
				if(!mysqlControl.disallow(word)) {
           			return false;
        		}
				allowedWords.remove(word);
        		return true;
			}
		}

		return false;

	}    


	public boolean reset(String network_name, int number) {
		
		//Loop through every network/server/bot and return the needed bot
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			if (((String) network.get("name")).equalsIgnoreCase(network_name)) {
				ArrayList servers = (ArrayList) network.get("servers");
				for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
					HashMap server = (HashMap) servers.get(serversCounter);
					ArrayList bots = (ArrayList) server.get("bots");
					for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
						HashMap bot = (HashMap) bots.get(botsCounter);
						if (((int) bot.get("number")) == number) {
							((PircBotX) bot.get("pircbotx")).disconnect();
							return true;
						}
					}
				}
			}		
		}
		return false;

	}
	

	public String adminmsg(String game_name, String message) {
		
		ArrayList sendChannels = new ArrayList();
		ArrayList sendUsers = new ArrayList();
		
		//Loop through every network/server/bot and return the needed bot
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			ArrayList servers = (ArrayList) network.get("servers");
			for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
				HashMap server = (HashMap) servers.get(serversCounter);
				ArrayList bots = (ArrayList) server.get("bots");
				for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
					HashMap bot = (HashMap) bots.get(botsCounter);
					PircBotX pircBotX = (PircBotX) bot.get("pircbotx");
					ArrayList channels = (ArrayList) bot.get("channels");
					for(int channelsCounter = 0; channelsCounter < channels.size(); channelsCounter++) {
						HashMap channel = (HashMap) channels.get(channelsCounter);
						if (((String) channel.get("game")).equalsIgnoreCase(game_name)) {
							pircBotX.sendMessage(((String) channel.get("name")), message);
							Channel ircChannel = pircBotX.getChannel(((String) channel.get("name")));
							if(!sendChannels.contains(ircChannel)) {
								sendChannels.add(ircChannel);
							}
							Set<User> ircUsers = ircChannel.getUsers();
							for(User ircUser : ircUsers) {
								if(!sendUsers.contains(ircUser)) {
									sendUsers.add(ircUser);
								}
							}
						}
					}
				}
			}
		}

		return sendChannels.size() + " channels, with " + sendUsers.size() + " individual users in those channels.";
		
	}


	public boolean display(String network_name, String channel_name, String key, boolean value) {
		
		HashMap network = this.getNetwork(network_name);
		HashMap channel = this.getChannel(network_name, channel_name);

		if (network.isEmpty()) {
			return false;
		}

		if (channel.isEmpty()) {
			return false;
		}
		else {
			if (((boolean) channel.get(key)) == value) {
				return false;
			}
			if (!mysqlControl.updateChannel(((Integer) channel.get("id")), key, value)) {
				return false;
			}
			channel.put(key, value);
			return true;
		}
	
	}


    public boolean validate(String[] messageSplit) {
        for (int messageSplitCounter = 2; messageSplitCounter < messageSplit.length; messageSplitCounter++) {
            String messageWord = (String) messageSplit[messageSplitCounter];
            boolean additionalWordAllowed = false;
            for (int allowedWordsCounter = 0; allowedWordsCounter < this.allowedWords.size(); allowedWordsCounter++ ) {
                String allowedWord = (String) this.allowedWords.get(allowedWordsCounter);
                if (messageWord.equalsIgnoreCase(allowedWord)) {
                    additionalWordAllowed = true;
                    break;
                }
            }
            if(!additionalWordAllowed) {
                return false;
            }
        }
        return true;
    }


	public String broadcast(String network_name, String channel_name, String game_name, String type, String message) {
		
		ArrayList sendChannels = new ArrayList();
		ArrayList sendUsers = new ArrayList();
        
		//Loop through every network/server/bot and return the needed bot
		for (int networksCounter = 0; networksCounter < this.networks.size(); networksCounter++) {
			HashMap network = (HashMap) this.networks.get(networksCounter);
			ArrayList servers = (ArrayList) network.get("servers");
			for (int serversCounter = 0; serversCounter < servers.size(); serversCounter++) {
				HashMap server = (HashMap) servers.get(serversCounter);
				ArrayList bots = (ArrayList) server.get("bots");
				for (int botsCounter = 0; botsCounter < bots.size(); botsCounter++) {
					HashMap bot = (HashMap) bots.get(botsCounter);
					PircBotX pircBotX = (PircBotX) bot.get("pircbotx");
                    if (pircBotX.isConnected()) {
                        Set<Channel> ircChannels = pircBotX.getChannels();
                        for (Channel ircChannel : ircChannels) {
                            HashMap channel = (HashMap) this.getChannel((String) network.get("name"), ircChannel.getName());
                            if(channel.isEmpty()) {
                                System.out.println("channel not found");
                                continue;
                            }

                            boolean test1 = ((String) channel.get("game")).equalsIgnoreCase(game_name);
                            boolean test2 = (boolean) channel.get("display" + network_name);
                            boolean test3 = (boolean) channel.get("display" + type);
                            boolean test4 = ((String) network.get("name")).equalsIgnoreCase(network_name); 
                            boolean test5 = ((String) channel.get("name")).equalsIgnoreCase(channel_name);

                            boolean test6 = true;
                            if (test4 && test5) {
                                test6 = false;
                            }
                            
                            System.out.println(((String) channel.get("name")) + " " + test1 + " " + test2 + " " + test3 + " " + test4 + " " + test5);
                            
                            if (test1 && test2 && test3 && test6) {

                                System.out.println("sending message to: " + ((String) channel.get("name")));
                            
                                pircBotX.sendMessage(((String) channel.get("name")), message);
                                if(!sendChannels.contains(ircChannel)) {
                                    sendChannels.add(ircChannel);
                                }
                                Set<User> ircUsers = ircChannel.getUsers();
                                for(User ircUser : ircUsers) {
                                    if(!sendUsers.contains(ircUser)) {
                                        sendUsers.add(ircUser);
                                    }
                                }
                            }
                        }
                    }
				}
			}
		}

		return sendChannels.size() + " channels, with " + sendUsers.size() + " individual users in those channels.";
		
	}

}
