package com.randude14.lotteryplus.command;

import java.util.Set;

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
		OfflinePlayer player = Utils.getOfflinePlayer(args[0]);
		String name = player.getName();
		try {
			int tickets = Integer.parseInt(args[2]);
			if(lottery.rewardPlayer(sender, name, tickets)) {
				ChatUtils.send(sender, "plugin.command.reward.mess", "<player>", name, "<lottery>", lottery.getName(), "<tickets>", tickets);
			}		
			return true;
		} catch (Exception ex) {
			ChatUtils.send(sender, "lottery.error.invalid-number");
		}
		return false;
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
