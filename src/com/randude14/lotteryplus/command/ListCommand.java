package com.randude14.lotteryplus.command;

import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;

public class ListCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		int page = 1;
		if(args.length > 0) {
			try {
				if(args.length == 1) page = Integer.parseInt(args[0]);
				else page = Integer.parseInt(args[1]);
			} catch (Exception ex) {
				ChatUtils.send(sender, "lottery.error.invalid-number");
				return false;
			}
		}
		if(args.length <= 1)
			LotteryManager.listLotteries(sender, page);
		else
			LotteryManager.listLotteries(sender, page, args[0]);
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}
	
    public Perm getPermission() {
		return Perm.LIST;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.LIST, "plugin.command.list", cmd);
		ChatUtils.sendCommandHelp(sender, Perm.LIST, "plugin.command.list.filter", cmd);
	}

	public void listCommands(CommandSender sender, Set<String> list) {
		list.add("plugin.command.list");
		list.add("plugin.command.list.filter");
	}
	
	public int minValues() {
		return 0;
	}
}
