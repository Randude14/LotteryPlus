package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.lottery.Lottery;

public class BuyCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(!LotteryPlus.checkPermission(sender, Perm.BUY)) {
			return false;
		}
		if(args.length == 1) return buyTickets((Player) sender, args[0], Config.getString(Config.MAIN_LOTTERY));
		else return buyTickets((Player) sender, args[1], args[0]);
	}

	public CommandAccess getAccess() {
		return CommandAccess.PLAYER;
	}
	
	public void listCommands(CommandSender sender, List<String> list) {
		if(LotteryPlus.hasPermission(sender, Perm.BUY)){
			list.add("plugin.command.buy");
			list.add("plugin.command.buy.main");
		}
	}
	
	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.BUY, "plugin.command.buy", cmd);
		ChatUtils.sendCommandHelp(sender, Perm.BUY, "plugin.command.buy.main", cmd);
	}
	
	public int minValues() {
		return 1;
	}
	
	private static boolean buyTickets(Player player, String ticketString, String lotteryName) {
		Lottery lottery = LotteryManager.getLottery(lotteryName);
		if(lottery == null) {
			ChatUtils.send(player, "lottery.error.notfound", "<lottery>", lotteryName);
			return false;
		}
		int tickets;
		try {
			tickets = Integer.parseInt(ticketString);
		} catch (Exception ex) {
			ChatUtils.send(player, "lottery.error.invalid-number");
			return false;
		}
		if(lottery.buyTickets(player, tickets)) {
			lottery.broadcast(player.getName(), tickets);
			if(lottery.isOver()) {
				lottery.draw();
			}
		}
		return true;
	}
}
