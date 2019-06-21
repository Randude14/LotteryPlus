package com.randude14.lotteryplus.command;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.util.Utils;

public class RewardCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		Lottery lottery = LotteryManager.getLottery(args[1]);
		
		if(lottery == null) {
			ChatUtils.send(sender, "lottery.error.notfound", "<lottery>", args[1]);
			return false;
		}
		
		if(args[0].equalsIgnoreCase(sender.getName())) {
			ChatUtils.send(sender, "plugin.command.reward.error.yourself");
			return false;
		}
		
		List<OfflinePlayer> players = Utils.matchForPlayers(args[0]);
		
		if (players.isEmpty()) {
			ChatUtils.send(sender, "plugin.command.reward.error.unknown-player", "<player>", args[0]);
			return false;
			
		} else if (players.size() > 1) {
			ChatUtils.send(sender, "plugin.command.reward.error.ambig-name", "<player>", args[0]);
			return false;
		}
		
		if (!Utils.isNumber(args[2])) {
			ChatUtils.send(sender, "lottery.error.invalid-number");
			return false;
		}
		
		int tickets = Integer.parseInt(args[2]);
		String name = players.get(0).getName();
		
		if(lottery.rewardPlayer(sender, name, tickets)) {
			ChatUtils.send(sender, "plugin.command.reward.mess", "<player>", name, "<lottery>", lottery.getName(), "<tickets>", tickets);
		}
		
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 0) {
			return Arrays.asList(Bukkit.getOfflinePlayers()).stream().map( 
					(OfflinePlayer player) -> player.getName()).collect(Collectors.toList());
			
		} else if (args.length == 1) {
			return Arrays.asList(Bukkit.getOfflinePlayers()).stream().map( 
					(OfflinePlayer player) -> player.getName()).
					filter( (String player) -> player.startsWith(args[0]) ).collect(Collectors.toList());
			
		} else if (args.length == 2) {
			return LotteryManager.getLotteryNames(args[1]);
		}
		
		
		return null;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}
	
    public Perm getPermission() {
		return Perm.REWARD;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.REWARD, "plugin.command.reward", cmd);
	}

	public void listCommands(CommandSender sender, Set<String> list) {
		list.add("plugin.command.reward");
	}
	
	public int minValues() {
		return 3;
	}
}
