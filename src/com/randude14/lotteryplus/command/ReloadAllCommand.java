package com.randude14.lotteryplus.command;

import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;

public class ReloadAllCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		LotteryManager.reloadLotteries(sender, true);
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}
	
    public Perm getPermission() {
		return Perm.RELOAD_ALL;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.RELOAD_ALL, "plugin.command.reloadall", cmd);
	}

	public void listCommands(CommandSender sender, Set<String> list) {
		list.add("plugin.command.reloadall");
	}
	
	public int minValues() {
		return 0;
	}
}
