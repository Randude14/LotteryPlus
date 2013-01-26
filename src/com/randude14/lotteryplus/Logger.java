package com.randude14.lotteryplus;

import org.bukkit.Bukkit;

public class Logger {
	public static final Plugin plugin = Plugin.getInstance();
	
	public static void info(String code, Object... args) {
		Bukkit.getConsoleSender().sendMessage(ChatUtils.getMessages(code, getLogPrefix(), args));
	}
	
	public static void infoRaw(String code, Object... args) {
		Bukkit.getConsoleSender().sendMessage(ChatUtils.getMessages(code, args));
	}
	
	public static final String getLogPrefix() {
		return String.format("[%s] v%s - ", plugin.getName(), plugin
				.getDescription().getVersion());
	}
}
