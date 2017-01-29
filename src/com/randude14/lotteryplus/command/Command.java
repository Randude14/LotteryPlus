package com.randude14.lotteryplus.command;

import org.bukkit.command.CommandSender;

public interface Command extends Listable {
	
	// called when the console/player executes this command
	boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args);
	
	// whether or not the console or player has access to this command
	// @see com.randude14.lotteryplus.command.CommandAccess
	CommandAccess getAccess();
	
	void getCommands(CommandSender sender, org.bukkit.command.Command cmd);
	
	int minValues();

}
