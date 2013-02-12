package com.randude14.lotteryplus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum PluginSupport {
	VAULT("net.milkbowl.vault.economy.Economy", "Vault"),
	TOWNY("com.palmergames.bukkit.towny.Towny", "Towny"),
	VOTIFIER("com.vexsoftware.votifier.Votifier", "Votifier");
	
	private PluginSupport(String main, String name) {
		this.main = main;
		this.name = name;
	}
	
	public boolean isInstalled() {
		Class<?> pluginClass = null;
		try {
			pluginClass = Class.forName(main);
		} catch (Exception e) {
		}
		if(pluginClass == null) return false;
		Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
		return (plugin != null ? plugin.getClass().equals(pluginClass) : false);
	}
	
	private final String main, name;
}
