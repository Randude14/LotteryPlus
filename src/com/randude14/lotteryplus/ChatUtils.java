package com.randude14.lotteryplus;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.configuration.Properties;

/*
 * This classes methods are used for chat handling
 */
public class ChatUtils {
	
	/*
	 * @info Reload the messages from 'lang.properties'
	 */
	public static void reload() {
		if(!defaultsLoaded) {
			try {
				defaults.load(plugin.getResource(langFile.getName()));
			} catch (Exception e) {
				Logger.info("logger.exception.file.load", "<file>", langFile.getName());
			}
			defaultsLoaded = true;
		}
		// create file if it doesn't exist
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
	
	/*
	 * @info broadcasts a message to certain players and to the console
	 * @param players the list of players to broadcast to
	 * @param code the path to the message
	 * @param args this is used to replace 'tags' in messages, see #getNameFor(String code, Object... args)
	 */
	public static void broadcast(List<Player> players, String code, Object... args) {
		String[] messages = getMessages(code, args);
		for (Player player : players) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}
	
	/*
	 * @info broadcasts a message to all players and to the console
	 * @param code the path to the message
	 * @param args this is used to replace 'tags' in messages, see #getNameFor(String code, Object... args)
	 */
	public static void broadcast(String code, Object... args) {
		String[] messages = getMessages(code, args);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}

	/*
	 * @info broadcasts a raw message to certain players and to the console
	 * @param players the list of players to broadcast to
	 * @param code the path to the message
	 * @param args this is used to replace 'tags' in messages, see #getNameFor(String code, Object... args)
	 */
	public static void broadcastRaw(List<Player> players, String code, Object... args) {
		String[] messages = getMessages(code, null, args);
		for (Player player : players) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}
	
	/*
	 * @info broadcasts a message to all players and to the console
	 * @param code the path to the message
	 * @param args this is used to replace 'tags' in messages, see #getNameFor(String code, Object... args)
	 */
	public static void broadcastRaw(String code, Object... args) {
		String[] messages = getMessages(code, null, args);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(messages);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}
	
	/*
	 * @info sends a message to a @CommandSender
	 * @param sender the @CommandSender to send the message to
	 * @param code the path to the message
	 * @param args this is used to replace 'tags' in messages, see #getNameFor(String code, Object... args)
	 */
	public static void send(CommandSender sender, String code, Object... args) {
		sender.sendMessage(getMessages(code, getChatPrefix(), args));
	}

	/*
	 * @info sends a raw message to a @CommandSender
	 * @param sender the @CommandSender to send the message to
	 * @param code the path to the message
	 * @param args this is used to replace 'tags' in messages, see #getNameFor(String code, Object... args)
	 */
	public static void sendRaw(CommandSender sender, String code, Object... args) {
		sender.sendMessage(getMessages(code, null, args));
	}

	/*
	 * @param sender the @CommandSender to send the command help to, if he/she has permission
	 * @param permission the @Perm to check
	 * @param code the path to the command help message
	 * @param cmd the @org.bukkit.command.Command that will be in the command help message
	 * @return true if sender has permission and the message was sent, otherwise false
	 */
	public static boolean sendCommandHelp(CommandSender sender, Perm permission, String code, org.bukkit.command.Command cmd) {
		if (!LotteryPlus.checkPermission(sender, permission))
			return false;
		sendRaw(sender, code, "<command>", cmd.getLabel());
		return true;
	}
	
	/*
	 * @info send a command help message
	 * @param sender the @CommandSender to send the command help to, if he/she has permission
	 * @param permission the @Perm to check
	 * @param code the path to the command help message
	 * @param cmd the @org.bukkit.command.Command that will be in the command help message
	 */
	public static void sendCommandHelp(CommandSender sender, String code, org.bukkit.command.Command cmd) {
		sendRaw(sender, code, "<command>", cmd.getLabel());
	}

	/*
	 * @return the chat prefix that is used in sending messages
	 */
	public static final String getChatPrefix() {
		return replaceColorCodes(Config.getString(Config.CHAT_PREFIX));
	}

	/*
	 * @param mess the message to replace codes
	 * @return the "color code" replaced message
	 */
	public static final String replaceColorCodes(String mess) {
		return mess.replaceAll(colorCodes, "\u00A7$2");
	}
	
	/*
	 * @param mess the message to wipe color codes off of
	 * @return the "color code" free message, see #getNameFor(String code, Object... args)
	 */
	public static final String cleanColorCodes(String mess) {
		return mess.replaceAll(colorCodes, "");
	}
	
	/*
	 * @param code the path to the message
	 * @param args this is used to replace 'tags' in message(s), see #getNameFor(String code, Object... args)
	 * @info uses default chat prefix
	 */
	public static String[] getMessages(String code, Object... args) {
		return getMessages(code, getChatPrefix(), args);
	}

	/*
	 * @param code the path to the message
	 * @param prefix the prefix used in the message(s)
	 * @param args this is used to replace 'tags' in message(s), see #getNameFor(String code, Object... args)
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
	 * @param code the path to the message
	 * @param args this is used to replace 'tags' in message
	 *   '<player> bought a ticket' and you pass in ["<player>", "Randude14"] as the args, it would send 'Randude14 bought a ticket!'
	 */
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
	
	private static final LotteryPlus plugin = LotteryPlus.getInstance();
	private static final File langFile = new File(LotteryPlus.getInstance().getDataFolder(), "lang.properties");
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
		colorCodes = "(&([" + string + "]))";
	}
}
