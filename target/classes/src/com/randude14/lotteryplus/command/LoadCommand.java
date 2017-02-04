package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;

public class LoadCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(args.length == 0) {
			int numLotteries = LotteryManager.loadLotteries(sender, false);
			ChatUtils.send(sender, "logger.lottery.num", "<number>", numLotteries);
		}
		else
			for(String lottery : args) {
				LotteryManager.loadLottery(sender, lottery);
			}
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}
	
    public Perm getPermission() {
		return Perm.LOAD;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.LOAD, "plugin.command.load1", cmd);
		ChatUtils.sendCommandHelp(sender, Perm.LOAD, "plugin.command.load2", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		list.add("plugin.command.load1");
		list.add("plugin.command.load2");
	}
	
	public int minValues() {
		return 0;
	}
}
