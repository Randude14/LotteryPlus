package com.randude14.lotteryplus;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.configuration.Properties;

public class ChatUtils {
	
	public static void reload() {
		if(!defaultsLoaded) {
			try {
				defaults.load(plugin.getResource(langFile.getName()));
			} catch (Exception e) {
				Logger.info("logger.exception.file.load", "<file>", langFile.getName());
			}
			defaultsLoaded = true;
		}
		if(!langFile.exists()) {
			plugin.saveResource(langFile.getName(), false);
		}
		try {
			properties.clear();
			properties.load(langFile);
			if(properties.getDefaults() == null) 
				properties.setDefaults(defaults);
		} catch (Exception ex) {
			Logger.info("logger.exception.file.load", "<file>", langFile.getName());
			ex.printStackTrace();
		}
	}
	
	public static void broadcast(String code, Object... args) {
		String[] messages = getMessages(code, args);
		for (Player player : Bukkit.getOnlinePlayers()) {
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
		if (!Plugin.hasPermission(sender, permission))
			return false;
		sendRaw(sender, code, "<command>", cmd.getLabel());
		return true;
	}

	public static final String getChatPrefix() {
		return replaceColorCodes(Config.getString(Config.CHAT_PREFIX));
	}

	// replace color codes with the colors
	public static final String replaceColorCodes(String mess) {
		return mess.replaceAll("(&([" + colorCodes + "]))", "\u00A7$2");
	}
	
	// get rid of color codes
	public static final String cleanColorCodes(String mess) {
		return mess.replaceAll("(&([" + colorCodes + "]))", "");
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
			mess = mess.replace(args[cntr].toString(), args[cntr+1].toString());
		}
		mess = replaceColorCodes(mess);
		return mess;
	}
	
	public static String getRawName(String code, Object... args) {
		String mess = properties.getProperty(code);
		for(int cntr = 0;cntr < args.length-1;cntr+=2) {
			mess = mess.replace(args[cntr].toString(), args[cntr+1].toString());
		}
		return mess;
	}
	
	private static final Plugin plugin = Plugin.getInstance();
	private static final File langFile = new File(Plugin.getInstance().getDataFolder(), "lang.properties");
	private static final Properties properties = new Properties();
	private static final Properties defaults = new Properties();
	private static boolean defaultsLoaded = false;
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
		colorCodes = string;
	}
}
