package com.randude14.register.economy;


import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.configuration.Config;

/*
 * represents an Economy
 */
public abstract class Economy implements ConfigurationSerializable {
	
	public boolean hasEnough(Player player, double amount) {
		return hasEnough(player.getName(), amount);
	}
	
	/*
	 * @param player the player
	 * @param amount the amount to check
	 * @return true if: player's balance >= amount, otherwise false
	 */
	public abstract boolean hasEnough(String player, double amount);
	
	public double desposit(Player player, double amount) {
		return deposit(player.getName(), amount);
	}
	
	/*
	 * @param player the player
	 * @param amount the amount to deposit
	 * @return the amount that could not go into player's bank, only used in @MaterialEconomy
	 */
	public abstract double deposit(String player, double amount);
	
	public void withdraw(Player player, double amount) {
		withdraw(player.getName(), amount);
	}
	
	/*
	 * @param player the player
	 * @param amount the amount to withdraw 
	 * @info withdraws amount from player's balance
	 */
	public abstract void withdraw(String player, double amount);
	
	/*
	 * @param amount the amount to format
	 * @return the formatted amount
	 */
	public abstract String format(double amount);
	
	public boolean hasAccount(Player player) {
		return hasAccount(player.getName());
	}
	
	/*
	 * @param player the player
	 * @return if player has an account with this economy
	 */
	public abstract boolean hasAccount(String player);
	
	/*
	 * @param materialID the materialID of the economy to return
	 * @info this method is @Deprecated, planning to remove completely in future
	 */
	@Deprecated
	public static Economy getEconomy(int materialID) {
		return (materialID < 0) ? new VaultEconomy() : new MaterialEconomy(""+materialID, Config.getString(Config.DEFAULT_MATERIAL_NAME));
	}
}
