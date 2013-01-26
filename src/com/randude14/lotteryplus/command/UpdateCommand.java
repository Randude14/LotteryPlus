package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.Plugin;

public class UpdateCommand implements Command {
	private static final String currentVersion = Plugin.getVersion();

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(!Plugin.checkPermission(sender, Perm.UPDATE)) {
			return false;
		}
		Plugin.updateCheck(sender, currentVersion);
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.UPDATE, "plugin.command.update", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		if(Plugin.hasPermission(sender, Perm.UPDATE))
			list.add("plugin.command.update");
	}
	
	public int minValues() {
		return 0;
	}
}
