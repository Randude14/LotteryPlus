package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;

public class ReloadCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		for(String lottery : args) {
			LotteryManager.reloadLottery(sender, lottery, true);
		}
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}
	
    public Perm getPermission() {
		return Perm.RELOAD;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.RELOAD, "plugin.command.reload", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		list.add("plugin.command.reload");
	}
	
	public int minValues() {
		return 1;
	}
}
