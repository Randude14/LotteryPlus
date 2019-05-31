package com.randude14.lotteryplus.command;

import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;

public class GuiCreatorCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		String lotteryName = null;
		if(args.length > 0)
			lotteryName = args[0];
		LotteryPlus.openGui(lotteryName);
		return false;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}
	
    public Perm getPermission() {
		return null;
	}
	
	public void listCommands(CommandSender sender, Set<String> commands) {
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
