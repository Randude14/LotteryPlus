package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.Plugin;
import com.randude14.lotteryplus.lottery.Lottery;

public class BuyCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(!Plugin.checkPermission(sender, Perm.BUY)) {
			return false;
		}
		Lottery lottery = LotteryManager.getLottery(args[0].toLowerCase());
		if(lottery == null) {
			ChatUtils.send(sender, "lottery.error.notfound", "<lottery>", args[0]);
			return false;
		}
		int tickets;
		try {
			tickets = Integer.parseInt(args[1]);
		} catch (Exception ex) {
			ChatUtils.send(sender, "lottery.error.invalid-number");
			return false;
		}
		if(lottery.buyTickets((Player) sender, tickets)) {
			lottery.broadcast(sender.getName(), tickets);
			if(lottery.isOver()) {
				lottery.draw();
			}
		}
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.PLAYER;
	}
	
	public void listCommands(CommandSender sender, List<String> list) {
		if(Plugin.hasPermission(sender, Perm.BUY))
			list.add("plugin.command.buy");
	}
	
	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.BUY, "plugin.command.buy", cmd);
	}
	
	public int minValues() {
		return 2;
	}
}
