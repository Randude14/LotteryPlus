package com.randude14.lotteryplus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.configuration.Config;

public class ChatUtils {
	
	private static void saveLangFile() {
		
		LotteryPlus plugin = LotteryPlus.getInstance();
		
		if(!langFile.exists()) {
			plugin.saveResource(langFile.getName(), false);
			return;
		}
		
		//update files
		try {
			
		    PropertiesConfiguration config = new PropertiesConfiguration();
		    config.read(new FileReader(langFile.getPath()));
		    
		    for(Object object : defaults.keySet()) {
		    	String key = object.toString();
		    	
		    	if(!config.containsKey(key)) {
		    		config.setProperty(key, defaults.getProperty(key));
		    	}
		    }
		    
		    config.write(new FileWriter(langFile.getPath()));
		} catch (Exception ex) {
			Logger.info("logger.exception.file.load", "<file>", langFile.getName());
			ex.printStackTrace();
		}
	}
	
	public static void reload() {
		
		LotteryPlus plugin = LotteryPlus.getInstance();
		if(langFile == null)
			langFile = new File(plugin.getDataFolder(), "lang.properties");
		
		if(defaults == null) {
			defaults = new Properties();
			try {
				defaults.load(plugin.getResource(langFile.getName()));
			} catch (IOException e) {
				Logger.info("logger.exception.file.load", "<file>", langFile.getName());
				e.printStackTrace();
			}
		}
		
		saveLangFile();
		
		try {			
			properties = new Properties(defaults);
			properties.load(new FileInputStream(langFile.getPath()));
			properties.clear();
		} catch (Exception ex) {
			Logger.info("logger.exception.file.load", "<file>", langFile.getName());
			ex.printStackTrace();
		}
	}
	
	public static void broadcast(List<Player> players, String code, Object... args) {
		String[] messages = getMessages(code, args);
		for (Player player : players) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}
	
	public static void broadcast(String code, Object... args) {
		String[] messages = getMessages(code, args);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}

	public static void broadcastRaw(List<Player> players, String code, Object... args) {
		String[] messages = getMessages(code, null, args);
		for (Player player : players) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}
	
	public static void broadcastRaw(String code, Object... args) {
		String[] messages = getMessages(code, null, args);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}
	
	public static void send(CommandSender sender, String code, Object... args) {
		sender.sendMessage(getMessages(code, getChatPrefix(), args));
	}

	public static void sendRaw(CommandSender sender, String code, Object... args) {
		sender.sendMessage(getMessages(code, null, args));
	}

	public static boolean sendCommandHelp(CommandSender sender, Perm permission, String code, org.bukkit.command.Command cmd) {
		if (!permission.checkPermission(sender))
			return false;
		sendRaw(sender, code, "<command>", cmd.getLabel());
		return true;
	}
	
	public static void sendCommandHelp(CommandSender sender, String code, org.bukkit.command.Command cmd) {
		sendRaw(sender, code, "<command>", cmd.getLabel());
	}

	public static final String getChatPrefix() {
		return replaceColorCodes(Config.getString(Config.CHAT_PREFIX));
	}

	// replace color codes with the colors
	public static final String replaceColorCodes(String mess) {
		return mess.replaceAll(colorCodes, "\u00A7$2");
	}
	
	// get rid of color codes
	public static final String cleanColorCodes(String mess) {
		return mess.replaceAll(colorCodes, "");
	}
	
	public static String[] getMessages(String code, Object... args) {
		return getMessages(code, getChatPrefix(), args);
	}
	
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
	
	public static String getNameFor(String code, Object... args) {
		String mess = properties.getProperty(code);
		if(mess == null) mess = code;
		for(int cntr = 0;cntr < args.length-1;cntr+=2) {
			String string = args[cntr+1].toString();
			if(string == null || string.equals("")) string = "\"\"";
			mess = mess.replace(args[cntr].toString(), string);
		}
		mess = replaceColorCodes(mess);
		return mess;
	}
	
	public static String getRawName(String code, Object... args) {
		String mess = properties.getProperty(code);
		for(int cntr = 0;cntr < args.length-1;cntr+=2) {
			String string = args[cntr+1].toString();
			if(string == null || string.equals("")) string = "\"\"";
			mess = mess.replace(args[cntr].toString(), string);
		}
		return cleanColorCodes(mess);
	}
	
	private static File langFile;
	private static Properties properties;
	private static Properties defaults;
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
