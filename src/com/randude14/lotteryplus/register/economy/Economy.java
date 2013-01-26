package com.randude14.lotteryplus.register.economy;

import org.bukkit.entity.Player;

import com.randude14.lotteryplus.configuration.Config;

public abstract class Economy {
	
	public boolean hasEnough(Player player, double amount) {
		return hasEnough(player.getName(), amount);
	}
	
	public abstract boolean hasEnough(String player, double amount);
	
	public void desposit(Player player, double amount) {
		deposit(player.getName(), amount);
	}
	
	public abstract void deposit(String player, double amount);
	
	public void withdraw(Player player, double amount) {
		withdraw(player.getName(), amount);
	}
	
	public abstract void withdraw(String player, double amount);
	
	public abstract String format(double amount);
	
	public boolean hasAccount(Player player) {
		return hasAccount(player.getName());
	}
	
	public abstract boolean hasAccount(String player);
	
	public abstract int getMaterialID();
	
	public static Economy valueOf(int materialID) {
		return (materialID < 0) ? new VaultEconomy() : new MaterialEconomy(materialID, Config.getString(Config.DEFAULT_MATERIAL_NAME));
	}

}
