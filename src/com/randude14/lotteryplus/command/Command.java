package com.randude14.lotteryplus.command;

import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.Perm;

public interface Command {
	
	/*
	 * called when the console/player executes this command
	 */
	boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args);
	
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
	 * 
	 */
	void getCommands(CommandSender sender, org.bukkit.command.Command cmd);
	
	void listCommands(CommandSender sender, Set<String> list);
	
	int minValues();

}
