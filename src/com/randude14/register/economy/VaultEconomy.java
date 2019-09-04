package com.randude14.register.economy;

import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.util.Utils;

/*
 * This economy bridges out to a plugin called Vault that serves as an economy wrapper and
 * hooks into other major economy plugins without needing to create a different class for each one
 * 
 * @see Vault's bukkit dev page at https://dev.bukkit.org/projects/vault?gameCategorySlug=bukkit-plugins&projectID=33184 
 */
@SerializableAs("VaultEconomy")
public class VaultEconomy extends Economy {
	private final net.milkbowl.vault.economy.Economy econ;
	
	public VaultEconomy() {
		econ = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
		
		if(econ == null) {
			throw new NullPointerException(ChatUtils.getRawName("plugin.exception.vault.economy"));
		}
	}

	public boolean hasEnough(String playerName, double amount) {
		OfflinePlayer player = Utils.getOfflinePlayer(playerName);
		// only check players we could find
		if(player != null) {
			return econ.has(player, amount);
		}
		return false;
	}

	public double deposit(String playerName, double amount) {
		OfflinePlayer player = Utils.getOfflinePlayer(playerName);
		// only deposit from players we could find
		if(player != null) {
			econ.depositPlayer(player, amount);
		}
		return 0;
	}

	public void withdraw(String playerName, double amount) {
		OfflinePlayer player = Utils.getOfflinePlayer(playerName);
		// only withdraw from players we could find
		if(player != null) {
			econ.withdrawPlayer(player, amount);
		}
	}
	
	public String format(double amount) {
		return econ.format(amount);
	}
	
	public boolean hasAccount(String playerName) {
		OfflinePlayer player = Utils.getOfflinePlayer(playerName);
		// couldn't find player in server files
		if(player != null) {
			return econ.hasAccount(player);
		}
		return false;
	}
	
	public Map<String, Object> serialize() {
		return Collections.emptyMap();
	}
	
	public static VaultEconomy deserialize(Map<String, Object> map) {
		return new VaultEconomy();
	}

	public boolean hasEnough(Player player, double amount) {
		// couldn't find player in server files
		if(player == null)
			return false;
		
		return econ.has(player, amount);
	}

	public double deposit(Player player, double amount) {
		// couldn't find player in server files
		if(player == null)
			return 0;
		
		econ.depositPlayer(player, amount);
		return 0;
	}

	public void withdraw(Player player, double amount) {
		// couldn't find player in server files
		if(player == null)
			return;
		
		econ.withdrawPlayer(player, amount);
	}

	public boolean hasAccount(Player player) {
		// couldn't find player in server files
		if(player == null)
			return false;
				
		return econ.hasAccount(player);
	}
}
