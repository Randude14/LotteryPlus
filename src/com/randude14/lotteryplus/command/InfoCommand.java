package com.randude14.lotteryplus.command;

import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.util.ChatUtils;

public class InfoCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		Lottery lottery = LotteryManager.getLottery(sender, args[0]);
		if(lottery == null) {
			ChatUtils.send(sender, "lottery.error.notfound", "<lottery>", args[0]);
			return false;
		}
		ChatUtils.sendRaw(sender, "plugin.command.info.headliner", "<lottery>", lottery.getName());
		lottery.sendInfo(sender);
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 0) {
			return LotteryManager.getLotteryNames(sender);
		} else if (args.length == 1) {
			return LotteryManager.getLotteryNames(sender, args[0]);
		}
		
		return null;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}
	
    public Perm getPermission() {
		return Perm.INFO;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.INFO, "plugin.command.info", cmd);
	}

	public void listCommands(CommandSender sender, Set<String> list) {
		list.add("plugin.command.info");
	}
	
	public int minValues() {
		return 1;
	}
}
