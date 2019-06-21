package com.randude14.lotteryplus.command;

import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.util.ChatUtils;

public class BuyCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		return buyTickets((Player) sender, args[1], args[0]);
	}
	
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		
		if(args.length == 0) {
			return LotteryManager.getLotteryNames(sender);
			
		} else if (args.length == 1) {
			return LotteryManager.getLotteryNames(sender, args[0]);
		}
		
		return null;
	}

	public CommandAccess getAccess() {
		return CommandAccess.PLAYER;
	}
	
    public Perm getPermission() {
		return Perm.BUY;
	}
	
	public void listCommands(CommandSender sender, Set<String> list) {
		list.add("plugin.command.buy");
		list.add("plugin.command.buy.main");
	}
	
	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.BUY, "plugin.command.buy", cmd);
	}
	
	public int minValues() {
		return 2;
	}
	
	private static boolean buyTickets(Player player, String ticketString, String lotteryName) {
		Lottery lottery = LotteryManager.getLottery(player, lotteryName);
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
			lottery.broadcast("lottery.mess.buy", "<player>", player.getName(), "<tickets>", tickets, "<lottery>", lottery.getName());
		}
		return true;
	}
}
