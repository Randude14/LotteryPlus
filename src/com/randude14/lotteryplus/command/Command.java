package com.randude14.lotteryplus.command;

import org.bukkit.command.CommandSender;

public interface Command extends Listable {
	
	boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args);
	
	CommandAccess getAccess();
	
	void getCommands(CommandSender sender, org.bukkit.command.Command cmd);
	
	int minValues();

}
