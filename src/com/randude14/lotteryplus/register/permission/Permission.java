package com.randude14.lotteryplus.register.permission;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.Perm;

public abstract class Permission {

	public boolean hasPermission(CommandSender sender, Perm permission) {
		if (playerHas(sender, permission.getPermission().getName())) {
			return true;
		} else {
			Perm parent = permission.getParent();
			return (parent != null) ? hasPermission(sender, parent) : false;
		}
	}

	protected abstract boolean playerHas(CommandSender sender, String permission);

}
