package com.randude14.lotteryplus.register.permission;

import org.bukkit.entity.Player;

import com.randude14.lotteryplus.Perm;

public abstract class Permission {
	
	public boolean hasPermission(Player player, Perm permission) {
		if(player != null) {
			if(playerHas(player, permission.getPermission())) {
				return true;
			} else {
				Perm parent = permission.getParent();
				return (parent != null) ? hasPermission(player, parent) : false;
			}
		}
		return false;
	}
	
	protected abstract boolean playerHas(Player player, String permission);

}
