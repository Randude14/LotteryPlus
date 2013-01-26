package com.randude14.lotteryplus.register.economy;

import org.bukkit.Bukkit;

import com.randude14.lotteryplus.ChatUtils;


public class VaultEconomy extends Economy {
	net.milkbowl.vault.economy.Economy econ;
	
	public VaultEconomy() {
		econ = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
		if(econ == null) {
			throw new NullPointerException(ChatUtils.getNameFor("plugin.exception.vault.economy"));
		}
	}

	public boolean hasEnough(String player, double amount) {
		return econ.has(player, amount);
	}

	public void deposit(String player, double amount) {
		econ.depositPlayer(player, amount);
	}

	public void withdraw(String player, double amount) {
		econ.withdrawPlayer(player, amount);
	}
	
	public String format(double amount) {
		return econ.format(amount);
	}
	
	public boolean hasAccount(String player) {
		return econ.hasAccount(player);
	}
	
	public static boolean isVaultInstalled() {
		try {
			Class.forName("net.milkbowl.vault.economy.Economy");
			return Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider() != null;
		} catch (Exception ex) {
			return false;
		}
	}
	
	public int getMaterialID() {
		return -1;
	}
}
