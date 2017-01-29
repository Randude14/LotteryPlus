package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.lottery.Lottery;

public class InfoCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(!LotteryPlus.checkPermission(sender, Perm.INFO)) {
			return false;
		}
		Lottery lottery = LotteryManager.getLottery(sender, args[0]);
		if(lottery == null) {
			ChatUtils.send(sender, "lottery.error.notfound", "<lottery>", args[0]);
			return false;
		}
		ChatUtils.sendRaw(sender, "plugin.command.info.headliner", "<lottery>", lottery.getName());
		lottery.sendInfo(sender);
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.INFO, "plugin.command.info", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		if(LotteryPlus.hasPermission(sender, Perm.INFO))
			list.add("plugin.command.info");
	}
	
	public int minValues() {
		return 1;
	}
}
