package com.randude14.lotteryplus.command;

import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.LotteryPlus;

public class UpdateCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		LotteryPlus.updateCheck(sender);
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}
	
    public Perm getPermission() {
		return Perm.UPDATE;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.UPDATE, "plugin.command.update", cmd);
	}

	public void listCommands(CommandSender sender, Set<String> list) {
		list.add("plugin.command.update");
	}
	
	public int minValues() {
		return 0;
	}
}
