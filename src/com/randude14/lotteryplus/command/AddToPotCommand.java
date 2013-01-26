 package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.Plugin;
import com.randude14.lotteryplus.lottery.Lottery;

public class AddToPotCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(!Plugin.checkPermission(sender, Perm.ADD_TO_POT)) {
			return false;
		}
		Lottery lottery = LotteryManager.getLottery(args[0]);
		if(lottery == null) {
			ChatUtils.send(sender, "lottery.error.notfound", "<lottery>", args[0]);
			return false;
		}
		try {
			double add = Double.parseDouble(args[1]);
			return lottery.addToPot(sender, add);
		} catch (Exception ex) {
			ChatUtils.send(sender, "lottery.error.invalid-number");
		}
		return false;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.ADD_TO_POT, "plugin.command.atp", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		if(Plugin.hasPermission(sender, Perm.ADD_TO_POT))
			list.add("plugin.command.atp");
	}
	
	public int minValues() {
		return 2;
	}
}
