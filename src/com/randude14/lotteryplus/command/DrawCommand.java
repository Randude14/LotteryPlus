package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.lottery.Lottery;

public class DrawCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(!LotteryPlus.checkPermission(sender, Perm.DRAW)) {
			return false;
		}
		Lottery lottery = LotteryManager.getLottery(sender, args[0]);
		if(lottery == null) {
			ChatUtils.send(sender, "lottery.error.notfound", "<lottery>", args[0]);
			return false;
		}
		lottery.draw(sender);
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.DRAW, "plugin.command.draw", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		if(LotteryPlus.hasPermission(sender, Perm.DRAW))
			list.add("plugin.command.draw");
	}
	
	public int minValues() {
		return 1;
	}
}
