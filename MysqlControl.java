/*

Copyright Jasper Verberk, 2001-2013, http://www.warfields.net/

This file is part of Warfields Bot

*/


import java.util.*;
import java.lang.*;
import java.sql.*;


public class MysqlControl {
	

	protected Connection connection;
	

	public MysqlControl(String address, String username, String password, String database) {
		try {
			this.open("jdbc:mysql://"+ address + "/" + database + "?user=" + username + "&password=" + password +"&autoReconnect=true&dontTrackOpenResources=true");
		}
		catch(Exception exception) {
		    ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
		}
	}
	

    public void open(String address) {
		try {
			Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
			connection = DriverManager.getConnection( address );
		}
		catch(Exception exception) {
		    ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
		}
    }


    public void close() {
		try {
			connection.close();
		}
		catch(Exception exception) {
		    ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
		}
    }


	public ArrayList getNetworks() {
		ArrayList networks = new ArrayList();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `network`;");
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				HashMap network = new HashMap(); 
				network.put("id", resultSet.getInt("network.id"));
				network.put("name", resultSet.getString("network.name").toLowerCase());
				network.put("perform", resultSet.getString("network.perform").toLowerCase());
				networks.add(network);
			}
			resultSet.close();
			preparedStatement.close();
			return networks;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return networks;
		}
	}	
	

	public ArrayList getServers(int network_id) {
		ArrayList servers = new ArrayList();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `server` WHERE `server`.`network_id` = ?;" );
			preparedStatement.setInt(1, network_id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				HashMap server = new HashMap();
				server.put("id", resultSet.getInt("server.id"));
				server.put("host", resultSet.getString("server.host").toLowerCase());
				servers.add(server);
			}
			resultSet.close();
			preparedStatement.close();
			return servers;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return servers;
		}
	}

	
	public ArrayList getChannels(int network_id) {
		ArrayList channels = new ArrayList();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `channel` WHERE `channel`.`network_id` = ? ORDER BY `channel`.`id` ASC;");
			preparedStatement.setInt(1, network_id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				HashMap channel = new HashMap();
				channel.put("id", resultSet.getInt("channel.id"));
				channel.put("name", resultSet.getString("channel.name").toLowerCase());
				channel.put("password", resultSet.getString("channel.password").toLowerCase());
				channel.put("game", resultSet.getString("channel.game").toLowerCase());
				channel.put("submitmsg", resultSet.getBoolean("channel.submitmsg"));
				channel.put("displaymsg", resultSet.getBoolean("channel.displaymsg"));
				channel.put("submitpcw", resultSet.getBoolean("channel.submitpcw"));
				channel.put("displaypcw", resultSet.getBoolean("channel.displaypcw"));
				channel.put("submitcw", resultSet.getBoolean("channel.submitcw"));
				channel.put("displaycw", resultSet.getBoolean("channel.displaycw"));
				channel.put("submitringer", resultSet.getBoolean("channel.submitringer"));
				channel.put("displayringer", resultSet.getBoolean("channel.displayringer"));
				channel.put("submitrecruit", resultSet.getBoolean("channel.submitrecruit"));
				channel.put("displayrecruit", resultSet.getBoolean("channel.displayrecruit"));
				channel.put("displayquakenet", resultSet.getBoolean("channel.displayquakenet"));
				channel.put("displayfreenode", resultSet.getBoolean("channel.displayfreenode"));
				channel.put("displaygamesurge", resultSet.getBoolean("channel.displaygamesurge"));
				channel.put("displayenterthegame", resultSet.getBoolean("channel.displayenterthegame"));
				channel.put("status", resultSet.getBoolean("channel.status"));
				channels.add(channel);
			}
			resultSet.close();
			preparedStatement.close();
			return channels;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return channels;
		}
	}


	public HashMap getChannel(int network_id, String channel_name) {
		HashMap channel = new HashMap();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `channel` WHERE `channel`.`network_id` = ? AND `channel`.`name` = ?;");
			preparedStatement.setInt(1, network_id);
			preparedStatement.setString(2, channel_name);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				channel.put("id", resultSet.getInt("channel.id"));
				channel.put("name", resultSet.getString("channel.name").toLowerCase());
				channel.put("password", resultSet.getString("channel.password").toLowerCase());
				channel.put("game", resultSet.getString("channel.game").toLowerCase());
				channel.put("submitmsg", resultSet.getBoolean("channel.submitmsg"));
				channel.put("displaymsg", resultSet.getBoolean("channel.displaymsg"));
				channel.put("submitpcw", resultSet.getBoolean("channel.submitpcw"));
				channel.put("displaypcw", resultSet.getBoolean("channel.displaypcw"));
				channel.put("submitcw", resultSet.getBoolean("channel.submitcw"));
				channel.put("displaycw", resultSet.getBoolean("channel.displaycw"));
				channel.put("submitringer", resultSet.getBoolean("channel.submitringer"));
				channel.put("displayringer", resultSet.getBoolean("channel.displayringer"));
				channel.put("submitrecruit", resultSet.getBoolean("channel.submitrecruit"));
				channel.put("displayrecruit", resultSet.getBoolean("channel.displayrecruit"));
				channel.put("displayquakenet", resultSet.getBoolean("channel.displayquakenet"));
				channel.put("displayfreenode", resultSet.getBoolean("channel.displayfreenode"));
				channel.put("displaygamesurge", resultSet.getBoolean("channel.displaygamesurge"));
				channel.put("displayenterthegame", resultSet.getBoolean("channel.displayenterthegame"));
				channel.put("status", resultSet.getBoolean("channel.status"));
			}
			resultSet.close();
			preparedStatement.close();
			return channel;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return channel;
		}
	}


	public ArrayList getUsers(int network_id) {
		ArrayList users = new ArrayList();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user` WHERE `user`.`network_id` = ?;");
			preparedStatement.setInt(1, network_id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				HashMap user = new HashMap();
				user.put("id", resultSet.getInt("user.id"));
				user.put("name", resultSet.getString("user.name").toLowerCase());
				user.put("status", resultSet.getBoolean("user.status"));
				user.put("admin", resultSet.getBoolean("user.admin"));
				user.put("superadmin", resultSet.getBoolean("user.superadmin"));
				users.add(user);
			}
			resultSet.close();
			preparedStatement.close();
			return users;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return users;
		}
	}


	public HashMap getUser(int network_id, String user_name) {
		HashMap user = new HashMap();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user` WHERE `user`.`network_id` = ? AND `user`.`name` = ?;");
			preparedStatement.setInt(1, network_id);
			preparedStatement.setString(2, user_name);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				user.put("id", resultSet.getInt("user.id"));
				user.put("name", resultSet.getString("user.name").toLowerCase());
				user.put("status", resultSet.getBoolean("user.status"));
				user.put("admin", resultSet.getBoolean("user.admin"));
				user.put("superadmin", resultSet.getBoolean("user.superadmin"));
			}
			resultSet.close();
			preparedStatement.close();
			return user;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return user;
		}
	}


	public boolean addUser(int network_id, String user_name) {
		boolean result = false;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `user` (`network_id`, `name`) VALUES (?, ?);", PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, network_id);
			preparedStatement.setString(2, user_name);
			if(preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			preparedStatement.close();
			return result;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return result;
		}
	}


	public boolean addChannel(int network_id, String channel_name, String channel_password) {
		boolean result = false;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `channel` (`network_id`, `name`, `password`) VALUES (?, ?, ?);", PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, network_id);
			preparedStatement.setString(2, channel_name);
			preparedStatement.setString(3, channel_password);
			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			preparedStatement.close();
			return result;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return result;
			
		}
	}


	public boolean updateChannel(int channel_id, String key, boolean value) {
		boolean result = false;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `channel` SET `channel`.`" + key + "` = ? WHERE `channel`.`id` = ?;");
			preparedStatement.setBoolean(1, value);
			preparedStatement.setInt(2, channel_id);
			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			preparedStatement.close();
			return result;			
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return result;
		}
	}


	public boolean updateChannel(int channel_id, String key, String value) {
		boolean result = false;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `channel` SET `channel`.`" + key + "` = ? WHERE `channel`.`id` = ?;");
			preparedStatement.setString(1, value);
			preparedStatement.setInt(2, channel_id);
			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			preparedStatement.close();
			return result;			
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return result;
		}
	}


	public boolean updateUser(int user_id, String key, boolean value) {
		boolean result = false;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `user` SET `user`.`" + key + "` = ? WHERE `user`.`id` = ?;");
			preparedStatement.setBoolean(1, value);
			preparedStatement.setInt(2, user_id);
			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			preparedStatement.close();
			return result;			
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return result;
		}
	}


	public boolean updateUser(int user_id, String key, String value) {
		boolean result = false;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `user` SET `user`.`" + key + "` = ? WHERE `user`.`id` = ?;");
			preparedStatement.setString(1, value);
			preparedStatement.setInt(2, user_id);
			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			preparedStatement.close();
			return result;			
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return result;
		}
	}


	public ArrayList getLogs(String network_name, String target, int amount) {
		ArrayList logs = new ArrayList();
		try {
			PreparedStatement preparedStatement = null;
			if ((target.substring(0, 1)).equals("#")) {
				preparedStatement = connection.prepareStatement("SELECT * FROM `log`, `channel`, `user`, `network` WHERE `channel`.`network_id` = `network`.`id` AND `network`.`name` = ? AND `log`.`channel_id` = `channel`.`id` AND `channel`.`name` = ? AND `log`.`user_id` = `user`.`id` ORDER BY `log`.`timestamp` DESC LIMIT ?;");
				preparedStatement.setString(1, network_name);
				preparedStatement.setString(2, target);
				preparedStatement.setInt(3, amount);				
			}
			else if ((target.substring(0, 1)).equals("%")) {				
				System.out.println("SELECT * FROM `log` , `channel` , `user` , `network` WHERE `channel`.`network_id` = `network`.`id` AND `network`.`name` = ? AND `log`.`channel_id` = `channel`.`id` AND `log`.`user_id` = `user`.`id` AND `log`.`content` LIKE ? ORDER BY `log`.`timestamp` DESC LIMIT ?;");
				preparedStatement = connection.prepareStatement("SELECT * FROM `log` , `channel` , `user` , `network` WHERE `channel`.`network_id` = `network`.`id` AND `network`.`name` = ? AND `log`.`channel_id` = `channel`.`id` AND `log`.`user_id` = `user`.`id` AND `log`.`content` LIKE ? ORDER BY `log`.`timestamp` DESC LIMIT ?;");
				preparedStatement.setString(1, network_name);
				preparedStatement.setString(2, target);
				preparedStatement.setInt(3, amount);				
			}
			else {				
				preparedStatement = connection.prepareStatement("SELECT * FROM `log` , `channel` , `user` , `network` WHERE `user`.`network_id` = `network`.`id` AND `network`.`name` = ? AND `log`.`user_id` = `user`.`id` AND `user`.`name` = ? AND `log`.`channel_id` = `channel`.`id` ORDER BY `log`.`timestamp` DESC LIMIT ?;");
				preparedStatement.setString(1, network_name);
				preparedStatement.setString(2, target);
				preparedStatement.setInt(3, amount);				
			}
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				java.util.Date date = new java.util.Date(resultSet.getLong("log.timestamp"));
				String log = "[LOG] " + (resultSet.getString("channel.name")).toLowerCase() + " @ " + (resultSet.getString("network.name")).toLowerCase() + " - " + (resultSet.getString("user.name")).toLowerCase() + " - " + (resultSet.getString("log.nick")).toLowerCase() + " - " + resultSet.getString("log.content") + " - " + date.toString();
				logs.add(log);
			}
			resultSet.close();
			preparedStatement.close();
			return logs;
			
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return logs;			
		}		
	}


    public ArrayList getAllowedWords() {
		ArrayList allowedWords = new ArrayList();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `allowedwords`;");
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				String allowedWord = (resultSet.getString("allowedwords.word")).toLowerCase();
				allowedWords.add(allowedWord);
			}
			resultSet.close();
			preparedStatement.close();
			return allowedWords;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return allowedWords;
		}
	}	


	public boolean allow(String word) {
		boolean result = false;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `allowedwords` (`word`) VALUES (?);", PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, word);
			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			preparedStatement.close();
			return result;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return result;
		}
	}

    public boolean disallow(String word) {
        boolean result = false;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `allowedwords` WHERE `word` = ?;");
			preparedStatement.setString(1, word);
			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			preparedStatement.close();
			return result;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return result;
		}
    }


	public ArrayList getBroadcasts(String game, String type) {
		ArrayList broadcasts = new ArrayList();
		try {
			java.util.Date date = new java.util.Date();
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `log` , `channel` , `network` WHERE `log`.`channel_id` = `channel`.`id`  AND `channel`.`game` = ? AND `channel`.`network_id` = `network`.`id` AND `log`.`type` = ? AND `log`.`timestamp` > ? ORDER BY `log`.`timestamp` DESC LIMIT 10;");
			preparedStatement.setString(1, game);
			preparedStatement.setString(2, type);
			preparedStatement.setLong(3, date.getTime() - 1800000);
			ResultSet resultSet = preparedStatement.executeQuery();
			while( resultSet.next() ) {
				long timestamp = resultSet.getLong("log.timestamp");
				if (type.equals("!msg")) {
					String broadcast = "[" + type.substring(1).toUpperCase() + "LIST] " + resultSet.getString("channel.name").toLowerCase() + " @ " + resultSet.getString("network.name").toLowerCase() + " - " + resultSet.getString("log.nick").toLowerCase() + " - " + resultSet.getString("log.content").substring(type.length() + 1) + " - " + (date.getTime() - timestamp) / 60000 + " minutes ago";
					broadcasts.add(broadcast);
					
				}
				else if (type.equals("!pcw") || type.equals("!cw")) {
					String[] contentSplit = (resultSet.getString("log.content")).split(" ");
					if(contentSplit.length == 2) {
						String broadcast = "[" + type.substring(1).toUpperCase() + "LIST] " + resultSet.getString("channel.name").toLowerCase() + " @ " + resultSet.getString("network.name").toLowerCase() + " - " + resultSet.getString("log.nick").toLowerCase() + " - Requested a " + contentSplit[1] + " vs " + contentSplit[1] + " - " + (date.getTime() - timestamp) / 60000 + " minutes ago";
						broadcasts.add(broadcast);
					}
					else {
						String broadcast = "[" + type.substring(1).toUpperCase() + "LIST] " + resultSet.getString("channel.name").toLowerCase() + " @ " + resultSet.getString("network.name").toLowerCase() + " - " + resultSet.getString("log.nick").toLowerCase() + " - Requested a " + contentSplit[1] + " vs " + contentSplit[1] + " (Additional info: " + (resultSet.getString("log.content")).substring(type.length() + 1 + contentSplit[1].length() + 1) + ") - " + (date.getTime() - timestamp) / 60000 + " minutes ago";
						broadcasts.add(broadcast);
					}
					
				}
				else if (type.equals("!ringer") || type.equals("!recruit")) {
					String[] contentSplit = (resultSet.getString("log.content")).split(" ");
					if(contentSplit.length == 2) {
						String broadcast = "[" + type.substring(1).toUpperCase() + "LIST] " + resultSet.getString("channel.name").toLowerCase() + " @ " + resultSet.getString("network.name").toLowerCase() + " - " + resultSet.getString("log.nick").toLowerCase() + " - Requesting " + contentSplit[1] + " - " + (date.getTime() - timestamp) / 60000 + " minutes ago";
						broadcasts.add(broadcast);
					}
					else {
						String broadcast = "[" + type.substring(1).toUpperCase() + "LIST] " + resultSet.getString("channel.name").toLowerCase() + " @ " + resultSet.getString("network.name").toLowerCase() + " - " + resultSet.getString("log.nick").toLowerCase() + " - Requesting " + contentSplit[1] + " (Additional info: " + (resultSet.getString("log.content")).substring(type.length() + 1 + contentSplit[1].length() + 1) + ") - " + (date.getTime() - timestamp) / 60000 + " minutes ago";
						broadcasts.add(broadcast);
					}
				}	
			}			
			resultSet.close();
			preparedStatement.close();
			return broadcasts;
			
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return broadcasts;
		}
		
	}


	public boolean addLog(int channel_id, int user_id, String user_nick, String type, String content) {
	
		boolean result = false;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement( "INSERT INTO `log` (`timestamp`, `type`, `channel_id`, `user_id`, `nick`, `content`) VALUES (?, ? ,?, ?, ?, ?);", PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, Long.toString(new java.util.Date().getTime()));
			preparedStatement.setString(2, type);
			preparedStatement.setInt(3, channel_id);
			preparedStatement.setInt(4, user_id);
			preparedStatement.setString(5, user_nick);
			preparedStatement.setString(6, content);
			if( preparedStatement.executeUpdate() > 0 ) {
				result = true;
			}
			preparedStatement.close();
			return result;
		}
		catch (Exception exception) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(exception);
			return result;
		}
	
	}	
	
}
