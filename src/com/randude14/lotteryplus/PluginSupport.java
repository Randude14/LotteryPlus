package com.randude14.lotteryplus;

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
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	@SuppressWarnings("unused")
	private final String main, name;
}
