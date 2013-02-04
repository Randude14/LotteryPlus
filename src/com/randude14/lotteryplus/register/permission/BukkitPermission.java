package com.randude14.lotteryplus.register.permission;

import org.bukkit.command.CommandSender;

public class BukkitPermission extends Permission {

	protected boolean playerHas(CommandSender sender, String permission) {
		return sender.hasPermission(permission);
	}
}
