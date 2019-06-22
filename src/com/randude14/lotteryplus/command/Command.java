package com.randude14.lotteryplus.command;

import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.Perm;

public interface Command {
	
	/*
	 * called when the console/player executes this command
	 */
	boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args);
	
	/*
	 * 
	 */
	List<String> onTabComplete(CommandSender sender, String[] args);
	
	/*
	 * whether or not the console or player has access to this command
	 * @see com.randude14.lotteryplus.command.CommandAccess
	 */
	CommandAccess getAccess();
	
	/*
	 * return the Permission required to use this command
	 */
	Perm getPermission();
	
	/*
	 * Sends the user information about this command and how to use it
	 * @param sender - user to send to
	 * @param cmd - bukkit command that contains the label
	 */
	void getCommands(CommandSender sender, org.bukkit.command.Command cmd);
	
	/*
	 * Adds the code that points to this commands information help.
	 * Used when user invokes the help command and lists the commands by page.
	 * @param sender - user to send to
	 * @param list - list to add the command codes to
	 */
	void listCommands(CommandSender sender, Set<String> list);
	
	/*
	 * @return - the minimum number of arguments needed to use this command
	 */
	int minValues();

}
