package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.LotteryPlus;

public class ClaimCommand implements Command {
	
	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		Player player = (Player) sender;
		LotteryPlus.getRewardsManager().checkForPlayerClaims(player);
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.PLAYER;
	}
	
    public Perm getPermission() {
		return Perm.CLAIM;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.CLAIM, "plugin.command.claim", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		list.add("plugin.command.claim");
	}
	
	public int minValues() {
		return 0;
	}
}
