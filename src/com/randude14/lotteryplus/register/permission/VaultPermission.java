package com.randude14.lotteryplus.register.permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VaultPermission extends Permission {
	private final net.milkbowl.vault.permission.Permission perm;
	
	public VaultPermission() {
		perm = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();
		if(perm == null) {
			throw new NullPointerException("Permission system not found from Vault.");
		}
	}

	protected boolean playerHas(Player player, String permission) {
		return perm.playerHas(player.getWorld().getName(), player.getName(), permission);
	}
	
	public static boolean isVaultInstalled() {
		try {
			Class.forName("net.milkbowl.vault.permission.Permission");
			return Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider() != null;
		} catch (Exception ex) {
		}
		return false;
	}
}
