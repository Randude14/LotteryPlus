package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.LotteryPlus;

public class ReloadCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(!LotteryPlus.checkPermission(sender, Perm.RELOAD)) {
			return false;
		}
		for(String lottery : args) {
			LotteryManager.reloadLottery(sender, lottery, true);
		}
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.RELOAD, "plugin.command.reload", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		if(LotteryPlus.hasPermission(sender, Perm.RELOAD))
			list.add("plugin.command.reload");
	}
	
	public int minValues() {
		return 1;
	}
}
