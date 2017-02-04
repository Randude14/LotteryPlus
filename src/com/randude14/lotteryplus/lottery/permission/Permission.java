package com.randude14.lotteryplus.lottery.permission;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.util.ChatUtils;

public abstract class Permission {
	
	public boolean checkAccess(CommandSender sender) {
		if(!hasAccess(sender)) {
			ChatUtils.sendRaw(sender, getErrorMessage());
			return false;
		}
		return true;
	}

	public abstract boolean hasAccess(CommandSender sender);
	
	protected abstract String getErrorMessage();
	
}
