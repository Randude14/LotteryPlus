package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.LotteryPlus;

public class ReloadAllCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(!LotteryPlus.checkPermission(sender, Perm.RELOAD_ALL)) {
			return false;
		}
		LotteryManager.reloadLotteries(sender);
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.RELOAD_ALL, "plugin.command.reloadall", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		if(LotteryPlus.hasPermission(sender, Perm.RELOAD_ALL))
			list.add("plugin.command.reloadall");
	}
	
	public int minValues() {
		return 0;
	}
}
