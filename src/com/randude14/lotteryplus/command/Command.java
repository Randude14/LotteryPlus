package com.randude14.lotteryplus.command;

import org.bukkit.command.CommandSender;

/*
 * represents a command
 */
public interface Command extends Listable {
	
	/*
	 * @param sender the @CommandSender that called the command
	 * @param cmd the @org.bukkit.command.Command that was called
	 * @param args the various arguments that was called along with the command
	 */
	boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args);
	
	/*
	 * @return the @CommandAccess of this command
	 */
	CommandAccess getAccess();
	
	/*
	 * @param sender the @CommandSender to get commands for
	 * @param cmd the @org.bukkit.command.Command that was called
	 */
	void getCommands(CommandSender sender, org.bukkit.command.Command cmd);
	
	/*
	 * @return the minimum number of arguments that can run this command
	 */
	int minValues();

}
