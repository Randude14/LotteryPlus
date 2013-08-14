package com.randude14.lotteryplus.command;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface Listable {
	
	/*
	 * @param sender the @CommandSender to list commands for
	 * @param commands the @List to store the commands in
	 */
	void listCommands(CommandSender sender, List<String> commands);
	
}
