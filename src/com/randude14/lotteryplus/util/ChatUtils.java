package com.randude14.lotteryplus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.configuration.Config;

/*
 * Responsible for loading the lang.properties file and handles all plugin messages.
 */
public class ChatUtils {
	
	private static File langFile;
	private static Properties properties = new Properties();
	
	/*
	 * Reloads/loads the lang.properties file and saves the the default file if it doesn't exist.
	 * Also writes new properties from the defaults file if they do not exist on the saved file.
	 */
	public static void reload() {
		
		LotteryPlus plugin = LotteryPlus.getInstance();
		properties.clear();
		
		if(langFile == null)
			langFile = new File(plugin.getDataFolder(), "lang.properties");
		
		try {
			
			if(langFile.exists()) {
				properties.load(new FileInputStream(langFile));
			}
			
			// compare the saved file to the default file
			Scanner fromJar = new Scanner(plugin.getResource(langFile.getName()));
		    PrintWriter saveTo = new PrintWriter(langFile);
			
		    while(fromJar.hasNextLine()) {
		    	String line = fromJar.nextLine();
		    	
		    	if(line.startsWith("#")) {
		    		saveTo.println(line);
		    	}
		    	
		    	int equalIndex = line.indexOf('=');
		    	
		    	if(equalIndex >= 0) {
		    		String key = line.substring(0, equalIndex);
		    		String value = line.substring(equalIndex+1);
		    		
		    		// don't change saved file
		    		if(properties.containsKey(key)) {
		    			saveTo.println(key + "=" + properties.getProperty(key));
		    			
		    		// set to default value and save if saved file doesn't have the property
		    		} else {
		    			saveTo.println(key + "=" + value);
		    			properties.setProperty(key, value);
		    		}		
		    	}
		    	
		    }
		   
		    fromJar.close();
		    saveTo.flush();
		    saveTo.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.info("lang.properties failed to properly load. Some messages may not load correclty.");	
		}
	}
	
	/*
	 * Broadcast out a message to specific list of players with the LotteryPlus prefix
	 * @param players - players to broadcast to
	 * @param code - the property that points the message
	 * @param args - arguments to replace in the message
	 */
	public static void broadcast(List<Player> players, String code, Object... args) {
		String[] messages = getMessages(code, args);
		
		for (Player player : players) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}
	
	/*
	 * Broadcast out a message to all players with the LotteryPlus prefix
	 * @param code - the property that points the message
	 * @param args - arguments to replace in the message
	 */
	public static void broadcast(String code, Object... args) {
		String[] messages = getMessages(code, args);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}

	/*
	 * Broadcast out a message to specific list of players without the LotteryPlus prefix
	 * @param players - players to broadcast to
	 * @param code - the property that points the message
	 * @param args - arguments to replace in the message
	 */
	public static void broadcastRaw(List<Player> players, String code, Object... args) {
		String[] messages = getMessages(code, null, args);
		for (Player player : players) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}
	
	/*
	 * Broadcast out a message to all players without the LotteryPlus prefix
	 * @param code - the property that points the message
	 * @param args - arguments to replace in the message
	 */
	public static void broadcastRaw(String code, Object... args) {
		String[] messages = getMessages(code, null, args);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}
	
	/*
	 * Send out a message to a user with the LotteryPlus prefix
	 * @param sender - user to send message to
	 * @param code - the property that points the message
	 * @param args - arguments to replace in the message
	 */
	public static void send(CommandSender sender, String code, Object... args) {
		sender.sendMessage(getMessages(code, getChatPrefix(), args));
	}

	/*
	 * Send out a message to a user without the LotteryPlus prefix
	 * @param sender - user to send message to
	 * @param code - the property that points the message
	 * @param args - arguments to replace in the message
	 */
	public static void sendRaw(CommandSender sender, String code, Object... args) {
		sender.sendMessage(getMessages(code, null, args));
	}

	/*
	 * Send message containing info about a command to a user
	 * @param sender - user to send message to
	 * @param permission - permission to check before sending command info
	 * @param code - the property that points the message
	 * @param cmd - bukkit command containing the label
	 */
	public static boolean sendCommandHelp(CommandSender sender, Perm permission, String code, org.bukkit.command.Command cmd) {
		if (!permission.checkPermission(sender))
			return false;
		sendRaw(sender, code, "<command>", cmd.getLabel());
		return true;
	}
	
	/*
	 * Send message containing info about a command to a user
	 * @param sender - user to send message to
	 * @param code - the property that points the message
	 * @param cmd - bukkit command containing the label
	 */
	public static void sendCommandHelp(CommandSender sender, String code, org.bukkit.command.Command cmd) {
		sendRaw(sender, code, "<command>", cmd.getLabel());
	}

	/*
	 * @return - the chat prefix defined in the config
	 */
	public static final String getChatPrefix() {
		return replaceColorCodes(Config.getString(Config.CHAT_PREFIX));
	}

	/*
	 * Given a message, replaces the color codes with their corresponding symbols
	 * @param mess - message to edit
	 * @return - the edited message with the color codes
	 */
	public static final String replaceColorCodes(String mess) {
		return mess.replaceAll(colorCodes, "\u00A7$2");
	}
	
	/*
	 * Given a message, erases the color codes
	 * @param mess - message to edit
	 * @return - the edited message without the color codes
	 */
	public static final String cleanColorCodes(String mess) {
		return mess.replaceAll(colorCodes, "");
	}
	
	public static String[] getMessages(String code, Object... args) {
		return getMessages(code, getChatPrefix(), args);
	}
	
	/*
	 * Given a code, returns the message that code points to and splits the message into
	 * multiple messages defined by the config's line separator
	 * @param code - code that points to the message
	 * @param prefix - prefix to insert before each line
	 * @param args - arguments to change in the message
	 */
	public static String[] getMessages(String code, String prefix, Object... args) {
		String mess = getNameFor(code, args);
		String[] messages = mess.split(Config.getString(Config.LINE_SEPARATOR));
		if(prefix != null) {
			for (int cntr = 0; cntr < messages.length; cntr++) {
				messages[cntr] = prefix + messages[cntr];
			}
		}
		return messages;
	}
	
	/*
	 * Given a code, returns the unedited message with the replaced color codes
	 * @param code - code that points to the message
	 * @param args - arguments toe change in the message
	 */
	public static String getNameFor(String code, Object... args) {
		
		String mess = code;
		
		if(properties.containsKey(code)) {
			mess = properties.getProperty(code);
		}
		
		for(int cntr = 0;cntr < args.length-1;cntr+=2) {
			String string = args[cntr+1].toString();
			
			if(string == null || string.equals("")) 
				string = "\"\"";
			
			mess = mess.replace(args[cntr].toString(), string);
		}
		mess = replaceColorCodes(mess);
		return mess;
	}
	
	/*
	 * Given a code, returns the unedited message with the color codes erased
	 * @param code - code that points to the message
	 * @param args - arguments toe change in the message
	 */
	public static String getRawName(String code, Object... args) {
		
		String mess = code;
		
		if(properties.containsKey(code)) {
			mess = properties.getProperty(code);
		}
		
		for(int cntr = 0;cntr < args.length-1;cntr+=2) {
			String string = args[cntr+1].toString();
			
			if(string == null || string.equals("")) 
				string = "\"\"";
			
			mess = mess.replace(args[cntr].toString(), string);
		}
		return cleanColorCodes(mess);
	}
	
	// string that contains the color codes
	private static final String colorCodes;
	
	static {
		String string = "";
		
		for(ChatColor color : ChatColor.values()) {
			char c = color.getChar();
			
			if(!Character.isLetter(c)) {
				string += c;
				
			} else {
				string += Character.toUpperCase(c);
				string += Character.toLowerCase(c);
			}
		}
		colorCodes = "(&([" + string + "]))";
	}
}
