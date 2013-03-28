package com.randude14.register.economy;

import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.SerializableAs;

import com.randude14.lotteryplus.ChatUtils;

@SerializableAs("VaultEconomy")
public class VaultEconomy extends Economy {
	private final net.milkbowl.vault.economy.Economy econ;
	
	public VaultEconomy() {
		econ = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
		if(econ == null) {
			throw new NullPointerException(ChatUtils.getRawName("plugin.exception.vault.economy"));
		}
	}

	public boolean hasEnough(String player, double amount) {
		return econ.has(player, amount);
	}

	public double deposit(String player, double amount) {
		econ.depositPlayer(player, amount);
		return 0;
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
	
	public Map<String, Object> serialize() {
		return Collections.emptyMap();
	}
	
	public static VaultEconomy deserialize(Map<String, Object> map) {
		return new VaultEconomy();
	}
}
