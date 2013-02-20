package com.randude14.lotteryplus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum PluginSupport {
	VAULT("Vault", "net.milkbowl.vault.Vault"),
	TOWNY("Towny", "com.palmergames.bukkit.towny.Towny"),
	VOTIFIER("Votifier", "com.vexsoftware.votifier.Votifier");
	
	private PluginSupport(String name, String main) {
		this.name = name;
		this.main = main;
	}
	
	public boolean isInstalled() {
		try {
			Class.forName(main);
		} catch (Exception e) {
			return false;
		}
		Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
		return plugin != null;
	}
	
	public Plugin getPlugin() {
		if(!isInstalled()) return null;
		return Bukkit.getPluginManager().getPlugin(name);
	}
	
	private final String main, name;
}
