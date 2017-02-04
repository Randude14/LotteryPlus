package com.randude14.register.economy;

import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.util.Utils;

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
		return econ.has(player, amount);
	}

	public double deposit(String playerName, double amount) {
		OfflinePlayer player = Utils.getOfflinePlayer(playerName);
		econ.depositPlayer(player, amount);
		return 0;
	}

	public void withdraw(String playerName, double amount) {
		OfflinePlayer player = Utils.getOfflinePlayer(playerName);
		econ.withdrawPlayer(player, amount);
	}
	
	public String format(double amount) {
		return econ.format(amount);
	}
	
	public boolean hasAccount(String playerName) {
		OfflinePlayer player = Utils.getOfflinePlayer(playerName);
		return econ.hasAccount(player);
	}
	
	public Map<String, Object> serialize() {
		return Collections.emptyMap();
	}
	
	public static VaultEconomy deserialize(Map<String, Object> map) {
		return new VaultEconomy();
	}

	public boolean hasEnough(Player player, double amount) {
		return this.hasEnough(player.getName(), amount);
	}

	public double deposit(Player player, double amount) {
		return this.deposit(player.getName(), amount);
	}

	public void withdraw(Player player, double amount) {
		this.withdraw(player.getName(), amount);
	}

	public boolean hasAccount(Player player) {
		return this.hasAccount(player.getName());
	}
}
