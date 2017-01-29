package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.ChatUtils;
<<<<<<< HEAD
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.RewardManager;
=======
import com.randude14.lotteryplus.ClaimManager;
import com.randude14.lotteryplus.Perm;
>>>>>>> upstream/master
import com.randude14.lotteryplus.LotteryPlus;

public class ClaimCommand implements Command {
	
	public boolean execute(CommandSender sender,
			org.bukkit.command.Command cmd, String[] args) {
		if (!LotteryPlus.checkPermission(sender, Perm.CLAIM)) {
			return false;
		}
<<<<<<< HEAD
		Player player = (Player) sender;
		RewardManager.rewardPlayer(player);
=======
		ClaimManager.rewardClaims((Player) sender);
>>>>>>> upstream/master
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.PLAYER;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.CLAIM, "plugin.command.claim", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		if (LotteryPlus.hasPermission(sender, Perm.CLAIM))
			list.add("plugin.command.claim");
	}
	
	public int minValues() {
		return 0;
	}
}
