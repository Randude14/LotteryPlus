package com.randude14.lotteryplus.lottery.permission;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.util.ChatUtils;

/*
 * Used by lotteries to determine whether players have access to use them
 */
public abstract class Permission {
	
	/*
	 * Check if user has access. Sends an error message if they do not
	 * 
	 * @param sender - user to check
	 * @return - whether user has access
	 */
	public boolean checkAccess(CommandSender sender) {
		if(!hasAccess(sender)) {
			ChatUtils.sendRaw(sender, getErrorMessage());
			return false;
		}
		return true;
	}

	/*
	 * @param sender - user to check
	 * @return - whether user has access 
	 */
	public abstract boolean hasAccess(CommandSender sender);
	
	/*
	 * @return - the code pointing to the error message
	 */
	protected abstract String getErrorMessage();
	
}
