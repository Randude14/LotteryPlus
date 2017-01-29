package com.randude14.register.economy;

import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;
<<<<<<< HEAD
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.LotteryPlus;
=======
import org.bukkit.configuration.serialization.SerializableAs;

import com.randude14.lotteryplus.ChatUtils;
>>>>>>> upstream/master

@SerializableAs("VaultEconomy")
public class VaultEconomy extends Economy {
	private final net.milkbowl.vault.economy.Economy econ;
	
	public VaultEconomy() {
		econ = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
		if(econ == null) {
			throw new NullPointerException(ChatUtils.getRawName("plugin.exception.vault.economy"));
		}
	}

<<<<<<< HEAD
	public boolean hasEnough(String playerName, double amount) {
		OfflinePlayer player = LotteryPlus.getOfflinePlayer(playerName);
		return econ.has(player, amount);
	}

	public double deposit(String playerName, double amount) {
		OfflinePlayer player = LotteryPlus.getOfflinePlayer(playerName);
=======
	public boolean hasEnough(String player, double amount) {
		return econ.has(player, amount);
	}

	public double deposit(String player, double amount) {
>>>>>>> upstream/master
		econ.depositPlayer(player, amount);
		return 0;
	}

<<<<<<< HEAD
	public void withdraw(String playerName, double amount) {
		OfflinePlayer player = LotteryPlus.getOfflinePlayer(playerName);
=======
	public void withdraw(String player, double amount) {
>>>>>>> upstream/master
		econ.withdrawPlayer(player, amount);
	}
	
	public String format(double amount) {
		return econ.format(amount);
	}
	
<<<<<<< HEAD
	public boolean hasAccount(String playerName) {
		OfflinePlayer player = LotteryPlus.getOfflinePlayer(playerName);
=======
	public boolean hasAccount(String player) {
>>>>>>> upstream/master
		return econ.hasAccount(player);
	}
	
	public Map<String, Object> serialize() {
		return Collections.emptyMap();
	}
	
	public static VaultEconomy deserialize(Map<String, Object> map) {
		return new VaultEconomy();
	}
<<<<<<< HEAD

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
=======
>>>>>>> upstream/master
}
