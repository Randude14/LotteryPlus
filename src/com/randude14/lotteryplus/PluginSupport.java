package com.randude14.lotteryplus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/*
 * This class is used to check if a certain plugin is also loaded onto the server
 * before actuall calling certain classes so that the Java class loader does
 * not throw errors when loading LotteryPlus
 */
public enum PluginSupport {
	VAULT("Vault", "net.milkbowl.vault.Vault"),
	TOWNY("Towny", "com.palmergames.bukkit.towny.Towny"),
	VOTIFIER("Votifier", "com.vexsoftware.votifier.Votifier");
	
	private PluginSupport(String name, String main) {
		this.name = name;
		this.main = main;
	}
	
	/*
	 * @return - return whether the plugin is loaded by checking its main class or its name
	 */
	public boolean isInstalled() {
		try {
			Class.forName(main);
		} catch (Exception e) {
			return false;
		}
		
		Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
		return plugin != null;
	}
	
	/*
	 * @return - return the plugin if is loaded onto the server
	 */
	public Plugin getPlugin() {
		if(!isInstalled()) return null;
		return Bukkit.getPluginManager().getPlugin(name);
	}
	
	private final String main, name;
}
