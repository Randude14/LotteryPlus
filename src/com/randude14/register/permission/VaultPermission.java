package com.randude14.register.permission;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VaultPermission extends Permission {
	private final net.milkbowl.vault.permission.Permission perm;
	
	public VaultPermission() {
		perm = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();
		if(perm == null) {
			throw new NullPointerException("Permission system not found from Vault.");
		}
	}

	protected boolean playerHas(CommandSender sender, String permission) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			return perm.has(player.getWorld(), player.getName(), permission) || player.hasPermission(permission);
		}
		return perm.has(sender, permission);
	}
}
