package com.randude14.lotteryplus;

import org.bukkit.Bukkit;

import com.randude14.lotteryplus.util.ChatUtils;

/*
 * This class is responsible for outputting information to the console
 * 
 * @author Randall Ferree
 */
public class Logger {
	public static final LotteryPlus plugin = LotteryPlus.getInstance();
	
	/*
	 * This method outputs a message to the console using the prefix
	 * @param code - code pointing to the text within the lang.properties
	 * @param args - the arguments that will fill in certain tags
	 */
	public static void info(String code, Object... args) {
		Bukkit.getConsoleSender().sendMessage(ChatUtils.getMessages(code, getLogPrefix(), args));
	}
	
	/*
	 * This method outputs a message to the console
	 * @param code - code pointing to the text within the lang.properties
	 * @param args - the arguments that will fill in certain tags
	 */
	public static void infoRaw(String code, Object... args) {
		Bukkit.getConsoleSender().sendMessage(ChatUtils.getMessages(code, args));
	}
	
	/*
	 * @return - returns the log prefix containing the plugin name and version
	 */
	public static final String getLogPrefix() {
		return String.format("[%s] v%s - ", plugin.getName(), plugin
				.getDescription().getVersion());
	}
}
