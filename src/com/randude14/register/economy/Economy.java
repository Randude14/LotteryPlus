package com.randude14.register.economy;


import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.configuration.Config;

/*
 * Serves as the basis for to conduct lottery transactions and rewarding winners
 */
public abstract class Economy implements ConfigurationSerializable {
	
	/*
	 * Check if player has enough money for a trasnaction
	 * 
	 * @param amount - the amount to check
	 * @return - whether the money in the player's account has at least amount
	 */
	public abstract boolean hasEnough(Player player, double amount);
	
	/*
	 * Deposit an amount into the player's account
	 * 
	 *  @param player - the player to deposit to
	 *  @param amount - the amount to deposit
	 *  @return - the amount left over after the transaction if it could not all be deposited
	 */
	public abstract double deposit(Player player, double amount);
	
	/*
	 * Deposit an amount into the offline player's account
	 * 
	 *  @param player - the player to deposit to
	 *  @param amount - the amount to deposit
	 *  @return - the amount left over after the transaction if it could not all be deposited
	 */
	public abstract double deposit(String player, double amount);
	
	/*
	 * Withdraw an amount into the player's account
	 * 
	 *  @param player - the player to withdraw from
	 *  @param amount - the amount to withdraw
	 */
	public abstract void withdraw(Player player, double amount);
	
	/*
	 * Used to format the currency of the economy
	 * 
	 *  @param amount - the amount used to format
	 *  @return - formatted currency with the amount
	 */
	public abstract String format(double amount);
	
	/*
	 * Check if player has an account with this economy
	 * 
	 *  @param player - the player to check
	 *  @return - whether the player has an account
	 */
	public abstract boolean hasAccount(Player player);
	
	/*
	 * Check if offline player has an account with this economy
	 * 
	 *  @param player - the player to check
	 *  @return - whether the player has an account
	 */
	public abstract boolean hasAccount(String playerName);
	
	/*
	 * Static class method that returns the economy associated with a material ID
	 * 0 is used for the server's local economy and all others is associated with an in game item
	 * 
	 *  @param materialID - id of the economy.
	 *  @return - the economy
	 */
	public static Economy getEconomy(int materialID) {
		return (materialID <= 0) ? new VaultEconomy() : new MaterialEconomy(""+materialID, Config.getString(Config.DEFAULT_MATERIAL_NAME));
	}
}
