package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.LotteryPlus;

public class GuiCreatorCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(args.length == 0) LotteryPlus.openGui();
		else LotteryPlus.openGui(args[0]);
		return false;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}
	
	public void listCommands(CommandSender sender, List<String> commands) {
		commands.add("plugin.command.gui");
		commands.add("plugin.command.gui-creator");
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, "plugin.command.gui", cmd);
		ChatUtils.sendCommandHelp(sender, "plugin.command.gui-creator", cmd);
	}

	public int minValues() {
		return 0;
	}

}
