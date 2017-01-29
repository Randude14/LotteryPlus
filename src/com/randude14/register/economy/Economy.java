package com.randude14.register.economy;


import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.configuration.Config;

public abstract class Economy implements ConfigurationSerializable {
	
	public abstract boolean hasEnough(Player player, double amount);
	
	public abstract double deposit(Player player, double amount);
	
	public abstract double deposit(String player, double amount);
	
	public abstract void withdraw(Player player, double amount);
	
	public abstract String format(double amount);
	
	public abstract boolean hasAccount(Player player);
	
	public abstract boolean hasAccount(String playerName);
	
	public static Economy getEconomy(int materialID) {
		return (materialID <= 0) ? new VaultEconomy() : new MaterialEconomy(""+materialID, Config.getString(Config.DEFAULT_MATERIAL_NAME));
	}
}
