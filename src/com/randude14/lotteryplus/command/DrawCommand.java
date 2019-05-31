package com.randude14.lotteryplus.command;

import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.util.ChatUtils;

public class DrawCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		Lottery lottery = LotteryManager.getLottery(sender, args[0]);
		if(lottery == null) {
			ChatUtils.send(sender, "lottery.error.notfound", "<lottery>", args[0]);
			return false;
		}
		lottery.draw(sender);
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}
	
    public Perm getPermission() {
		return Perm.DRAW;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.DRAW, "plugin.command.draw", cmd);
	}

	public void listCommands(CommandSender sender, Set<String> list) {
		list.add("plugin.command.draw");
	}
	
	public int minValues() {
		return 1;
	}
}
